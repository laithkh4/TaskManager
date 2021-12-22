package firsttask.taskmanager.Controller;


import firsttask.taskmanager.Logic.UserControllerLogic;
import firsttask.taskmanager.Models.AuthenticationRequest;
import firsttask.taskmanager.Models.AuthenticationResponse;
import firsttask.taskmanager.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@RestController
public class UserController {
    private final AuthenticationManager authenticationManager;
    private final UserControllerLogic userControllerLogic;
    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);
    public UserController(AuthenticationManager authenticationManager, UserControllerLogic userControllerLogic) {
        this.authenticationManager = authenticationManager;
        this.userControllerLogic = userControllerLogic;
    }
    @PostMapping("/register")
    public User createNewUser(@RequestBody User newUser)  {
        LOGGER.info("A create user request initialized ");
        LOGGER.trace("Creating new user ");
        return userControllerLogic.createNewUser(newUser);
    }
    @PostMapping("/login")
    public AuthenticationResponse createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest)  throws BadCredentialsException {
        try {
            LOGGER.info("Authentication is starting ...");
        }
        catch (BadCredentialsException e) {
            LOGGER.info("an exception must be thrown here ");

            throw new BadCredentialsException("Incorrect username or password", e);
        }
        return  userControllerLogic.createAuthenticationToken(authenticationRequest);
    }
    @PostMapping("/user/logout")
    public void logOut(HttpServletRequest request){
        userControllerLogic.logOut(request);
    }
    @PostMapping("/user/logoutall")
    public void logOutAll(HttpServletRequest request){
        userControllerLogic.logOutAll(request);

    }
    @GetMapping("/user")
    public  User returnUser()  {
        LOGGER.info("A get user request initialized ");
        return userControllerLogic.returnUser();
    }
   @PutMapping("/user")
    public User edUser(@RequestBody User editUser )  {
        LOGGER.info("A update user request initialized ");
        LOGGER.trace("updating user information " );
        return userControllerLogic.editOneUser(editUser);
    }
   @DeleteMapping("/user")
    public void deleteUser() throws IOException {
        userControllerLogic.deleteUser( );
    }
}