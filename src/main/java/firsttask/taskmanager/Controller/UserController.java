package firsttask.taskmanager.Controller;

import firsttask.taskmanager.Exceptions.GeneralException;
import firsttask.taskmanager.Exceptions.UserNotFoundException;
import firsttask.taskmanager.Repositories.TaskRepository;
import firsttask.taskmanager.Repositories.UserRepository;
import firsttask.taskmanager.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Optional;


@RestController
public class UserController {


    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);


    public UserController(TaskRepository taskRepository, UserRepository userRepository) throws GeneralException {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        logger.trace("creating Repositories and assemblers ");
    }



    //return all the users in that database
    @GetMapping("/users")
    public  List<User> returnAllUsers() throws GeneralException {
        logger.info("A get all users request initialized ");

        List<User> users = userRepository.findAll();

        logger.trace("retrieving all the users " );
        return users;
    }



    // return a  user and his task
    @GetMapping("/users/{id}")
    public  User returnUser(@PathVariable Long id) throws GeneralException, AccessDeniedException {
        logger.info("A get userwith id "+ id  +" request initialized ");
        //User requestUser=userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        User user = userRepository.findById(id) //
                .orElseThrow(() -> new UserNotFoundException(id));
        User requestingUser=Optional.of((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).orElseThrow(() -> new UserNotFoundException(id));
        if (user.getId()==requestingUser.getId() && user.getPassword().equals(requestingUser.getPassword())) {
            System.out.println(Optional.of(((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getEmail()));
            logger.trace("retrieving the user with id :  " + id);
            return user;
        }else {
            throw new AccessDeniedException("access denied please try again");
        }

    }


    //creating user
    @PostMapping("/users")
    User createNewUser(@RequestBody User newUser) throws GeneralException {
        logger.info("A create user request initialized ");

      userRepository.save(newUser);
        logger.trace("Creating new user "+ newUser);
        return newUser;
    }



    //Editing existing user after making sure of his password
    @PutMapping("/users/{id}")
    User edUser(@RequestBody User editUser, @PathVariable Long id) throws GeneralException {
        logger.info("A update user request initialized ");
        User updatedUser = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        updatedUser.setId(editUser.getId());
        updatedUser.setName(editUser.getName());
        updatedUser.setEmail(editUser.getEmail());
        updatedUser.setAge(editUser.getAge());

        logger.trace("updating user information " + updatedUser);
        return updatedUser;
    }



    //Delete the user by his id and verify the password of that user
    @DeleteMapping("/users/{id}")
    void deleteUser(@PathVariable Long id,HttpServletResponse response) throws IOException, GeneralException {

          logger.info("A Delete user request initialized ");
          if (userRepository.existsById(id)) {
              userRepository.deleteById(id);
              taskRepository.deleteAllByUser_Id(id);
          }

          logger.trace("Redirecting to the /Users page after deleting a user with id : " + id);
          response.sendRedirect("");

    }
}