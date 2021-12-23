package firsttask.taskmanager.Logic;

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
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Service
public class UserControllerLogic {

    private final UserDetailsServiceImpl userDetailsService;
    private final JwtUtil jwtTokenUtil;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    public UserControllerLogic(TaskRepository taskRepository, UserRepository userRepository, TokenRepository tokenRepository,  UserDetailsServiceImpl userDetailsService, JwtUtil jwtTokenUtil)  {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.userDetailsService = userDetailsService;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    public User createNewUser( User newUser)  {
        if(userRepository.findByEmail(newUser.getUsername()).isEmpty()){
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        newUser.setPassword( "{bcrypt}" + encoder.encode(newUser.getPassword()));
        userRepository.save(newUser);
        return newUser;
        }
        else throw new UserAlreadyExistException();
    }

    public AuthenticationResponse createAuthenticationToken( AuthenticationRequest authenticationRequest)  throws BadCredentialsException {
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

        if (userRepository.existsById(requestingUser.getId())) {
            taskRepository.deleteAllByUser_Id(requestingUser.getId());
            tokenRepository.deleteAllByUserId(requestingUser.getId());
            userRepository.deleteById(requestingUser.getId());

        }

      //  response.sendRedirect("/login");// the reason that delete return forbidden after the delete request  is that this redirect to the previously called users page which forbidden to user not logged in

    }


}
