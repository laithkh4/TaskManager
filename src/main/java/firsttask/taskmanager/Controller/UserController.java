package firsttask.taskmanager.Controller;


import firsttask.taskmanager.Models.AuthenticationRequest;
import firsttask.taskmanager.Models.AuthenticationResponse;
import firsttask.taskmanager.Repositories.TaskRepository;
import firsttask.taskmanager.Repositories.TokenRepository;
import firsttask.taskmanager.Repositories.UserRepository;
import firsttask.taskmanager.Security.JWTSecurity.JwtUtil;
import firsttask.taskmanager.Security.UserDetailsServiceImpl;
import firsttask.taskmanager.domain.Tokens;
import firsttask.taskmanager.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@RestController
public class UserController {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserDetailsServiceImpl userDetailsService;
    @Autowired
    private JwtUtil jwtTokenUtil;

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);
    public UserController(TaskRepository taskRepository, UserRepository userRepository, TokenRepository tokenRepository)  {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
    }
    @PostMapping("/login")
    public AuthenticationResponse createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest)  throws BadCredentialsException {
        try {
            LOGGER.info("Authentication is starting ...");
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword()));
        }
        catch (BadCredentialsException e) {
            LOGGER.info("an exception must be thrown here ");

            throw new BadCredentialsException("Incorrect username or password", e);
        }


        final UserDetails userDetails = userDetailsService
                .loadUserByUsername(authenticationRequest.getUsername());

        final String jwt = jwtTokenUtil.generateToken(userDetails);
        Tokens token= new Tokens();
        token.setJwtToken(jwt);
        User user=(User)userDetails;
        token.setUser(user);
        tokenRepository.save(token);
        user.addToken(token);
        userRepository.save(user);
        return new AuthenticationResponse(jwt);
    }

    @PostMapping("/user/logout")
    public void logOut(HttpServletRequest request){
        final String authorizationHeader = request.getHeader("Authorization");
        String jwt = authorizationHeader.substring(7);
        tokenRepository.deleteById(jwt);
    }
    @PostMapping("/user/logoutall")
    public void logOutAll(HttpServletRequest request){
        User requestingUser= (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        tokenRepository.deleteAllByUserId(requestingUser.getId());

    }
    // return a  user and his task
    @GetMapping("/user")
    public  User returnUser()  {
        LOGGER.info("A get user  request initialized ");
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }


    //creating user
    @PostMapping("/register")
    User createNewUser(@RequestBody User newUser)  {
        LOGGER.info("A create user request initialized ");
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        newUser.setPassword( "{bcrypt}" + encoder.encode(newUser.getPassword()));
      userRepository.save(newUser);
        LOGGER.trace("Creating new user ");
        return newUser;
    }



    //Editing existing user after making sure of his password
    @PutMapping("/user")
    User edUser(@RequestBody User editUser )  {
        LOGGER.info("A update user request initialized ");
        User requestingUser= (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            requestingUser.setPassword( "{bcrypt}" + encoder.encode(editUser.getPassword()));
            requestingUser.setName(editUser.getName());
            requestingUser.setEmail(editUser.getEmail());
            requestingUser.setAge(editUser.getAge());

            LOGGER.trace("updating user information " );
        return requestingUser;
    }



    //Delete the user by his id and verify the password of that user
    @DeleteMapping("/user")
    void deleteUser(HttpServletResponse response) throws IOException {

        User requestingUser= (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            LOGGER.info("A Delete user request initialized ");
            if (userRepository.existsById(requestingUser.getId())) {
                taskRepository.deleteAllByUser_Id(requestingUser.getId());
                userRepository.deleteById(requestingUser.getId());

            }

            LOGGER.trace("Redirecting to the /User page after deleting a user with id");
            response.sendRedirect("/login");// the reason that delete return forbidden after the delete request  is that this redirect to the previously called users page which forbidden to user not logged in

    }
}