package firsttask.taskmanager.Controller;


import firsttask.taskmanager.Exceptions.UserNotFoundException;
import firsttask.taskmanager.Models.AuthenticationRequest;
import firsttask.taskmanager.Models.AuthenticationResponse;
import firsttask.taskmanager.Repositories.TaskRepository;
import firsttask.taskmanager.Repositories.UserRepository;
import firsttask.taskmanager.Security.JWTSecurity.JwtUtil;
import firsttask.taskmanager.Security.UserDetailsServiceImpl;
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

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.List;


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
    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);
    public UserController(TaskRepository taskRepository, UserRepository userRepository) throws Exception {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
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

        return new AuthenticationResponse(jwt);
    }

    //return all the users in that database
    @GetMapping("/users")
    public  List<User> returnAllUsers() {

        LOGGER.info("A get all users request initialized ");

        List<User> users = userRepository.findAll();

        LOGGER.trace("retrieving all the users " );
        return users;
    }



    // return a  user and his task
    @GetMapping("/users/{id}")
    public  User returnUser(@PathVariable Long id) throws  AccessDeniedException {
        LOGGER.info("A get userwith id "+ id  +" request initialized ");
        User requestedUser = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        User requestingUser= (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (requestedUser.getId().longValue()==requestingUser.getId().longValue() && requestedUser.getPassword().equals(requestingUser.getPassword())) {
            LOGGER.trace("retrieving the user with id :  " + id);
            return requestedUser  ;
        }else {
            throw new AccessDeniedException("You are not allowed to access this page!");
        }

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
    @PutMapping("/users/{id}")
    User edUser(@RequestBody User editUser, @PathVariable Long id) throws  AccessDeniedException {
        LOGGER.info("A update user request initialized ");
        User updatedUser = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        User requestingUser= (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (updatedUser.getId().longValue()==requestingUser.getId().longValue() && updatedUser.getPassword().equals(requestingUser.getPassword())) {
            updatedUser.setId(id);
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            updatedUser.setPassword( "{bcrypt}" + encoder.encode(editUser.getPassword()));
            updatedUser.setName(editUser.getName());
            updatedUser.setEmail(editUser.getEmail());
            updatedUser.setAge(editUser.getAge());

            LOGGER.trace("updating user information " + updatedUser);
        return updatedUser;
        } else {
            throw new AccessDeniedException("You are not allowed to access this page!");
        }
    }



    //Delete the user by his id and verify the password of that user
    @DeleteMapping("/users/{id}")
    void deleteUser(@PathVariable Long id,HttpServletResponse response) throws IOException {
        User requestedUser =  userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        User requestingUser= (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (requestedUser.getId().longValue()==requestingUser.getId().longValue() && requestedUser.getPassword().equals(requestingUser.getPassword())) {

            LOGGER.info("A Delete user request initialized ");
            if (userRepository.existsById(id)) {
                userRepository.deleteById(id);
                taskRepository.deleteAllByUser_Id(id);
            }

            LOGGER.trace("Redirecting to the /Users page after deleting a user with id : " + id);
            response.sendRedirect("");
        }
    }
}