package firsttask.taskmanager.Services;

import firsttask.taskmanager.Exceptions.UserAlreadyExistException;
import firsttask.taskmanager.Models.AuthenticationRequest;
import firsttask.taskmanager.Models.AuthenticationResponse;
import firsttask.taskmanager.Repositories.TaskRepository;
import firsttask.taskmanager.Repositories.TokenRepository;
import firsttask.taskmanager.Repositories.UserRepository;
import firsttask.taskmanager.Security.JWTSecurity.JwtUtil;
import firsttask.taskmanager.Security.UserDetailsServiceImpl;
import firsttask.taskmanager.domain.Tokens;
import firsttask.taskmanager.domain.User;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;

@Service
public class UserServices {

    private final UserDetailsServiceImpl userDetailsService;
    private final JwtUtil jwtTokenUtil;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final AuthenticationManager authenticationManager;

    public UserServices(TaskRepository taskRepository, UserRepository userRepository, TokenRepository tokenRepository, UserDetailsServiceImpl userDetailsService, JwtUtil jwtTokenUtil, AuthenticationManager authenticationManager)  {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.userDetailsService = userDetailsService;
        this.jwtTokenUtil = jwtTokenUtil;
        this.authenticationManager = authenticationManager;
    }

    public User createNewUser( User newUser)  {

        if(userRepository.findByEmail(newUser.getUsername()).isEmpty()){
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        newUser.setPassword( "{bcrypt}" + encoder.encode(newUser.getPassword()));
        userRepository.save(newUser);
        return newUser;
        }
        else {

            throw new UserAlreadyExistException();
        }
    }

    public AuthenticationResponse createAuthenticationToken( AuthenticationRequest authenticationRequest)  {
        try {

            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(),authenticationRequest.getPassword()));
        }
        catch (BadCredentialsException e) {

            throw new BadCredentialsException("Incorrect username or password", e);
        }

        final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());
        final String jwt = jwtTokenUtil.generateToken(userDetails);
        Tokens token= new Tokens();
        User user=(User)userDetails;
        token.setUser(user);
        token.setJwtToken(jwt);
        tokenRepository.save(token);
        user.addToken(token);
        userRepository.save(user);
        return new AuthenticationResponse(jwt);
    }

    public void logOut(HttpServletRequest request){
        final String authorizationHeader = request.getHeader("Authorization");
        String jwt = authorizationHeader.substring(7);
        tokenRepository.deleteById(jwt);
    }

    public void logOutAll(){
        User requestingUser= (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        tokenRepository.deleteAllByUserId(requestingUser.getId());

    }
    public  User returnUser()  {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    public User editOneUser(@RequestBody User editUser )  {
        User requestingUser= (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        requestingUser.setPassword( "{bcrypt}" + encoder.encode(editUser.getPassword()));
        requestingUser.setName(editUser.getName());
        requestingUser.setEmail(editUser.getEmail());
        requestingUser.setAge(editUser.getAge());
        userRepository.save(requestingUser);
        return requestingUser;
    }

    public void deleteUser()  {
        User requestingUser= (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            taskRepository.deleteAllByUser_Id(requestingUser.getId());
            tokenRepository.deleteAllByUserId(requestingUser.getId());
            userRepository.deleteById(requestingUser.getId());
    }


}
