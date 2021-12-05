package firsttask.taskmanager.Controller;

import firsttask.taskmanager.Exceptions.PasswordNotCorrectException;
import firsttask.taskmanager.Exceptions.UserNotFoundException;
import firsttask.taskmanager.Repositories.TaskRepository;
import firsttask.taskmanager.Repositories.UserRepository;
import firsttask.taskmanager.assemblers.TaskModelAssembler;
import firsttask.taskmanager.assemblers.UserModelAssembler;
import firsttask.taskmanager.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

public class UserController {


    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final UserModelAssembler userModelAssembler;
    private final TaskModelAssembler taskModelAssembler;
    private static Logger logger = LoggerFactory.getLogger(UserController.class);


    public UserController(TaskRepository taskRepository, UserRepository userRepository,UserModelAssembler UserAssembler, TaskModelAssembler TaskAssembler) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.userModelAssembler=UserAssembler;
        this.taskModelAssembler=TaskAssembler;
        logger.trace("creating Repositories and assemblers ");
    }



    //return all the users in that database
    @GetMapping("/users")
    public CollectionModel<EntityModel<User>> returnAllUsers() {
        logger.info("A get all users request initialized ");
        List<EntityModel<User>> users = userRepository.findAll().stream() //
                .map(userModelAssembler::toModel) //
                .collect(Collectors.toList());
        logger.trace("retrieving all the users " );
        return CollectionModel.of(users, linkTo(methodOn(UserController.class).returnAllUsers()).withSelfRel());
    }



    // return a  user and his task
    @GetMapping("/users/{id}")
    public EntityModel<User> returnUser(@PathVariable Long id)  {

        User user = userRepository.findById(id) //
                .orElseThrow(() -> new UserNotFoundException(id));

        return userModelAssembler.toModel(user);

    }


    //creating user
    @PostMapping("/users")
    ResponseEntity<EntityModel<User>> createNewUser(@RequestBody User newUser) {
        logger.info("A create user request initialized ");
        EntityModel<User> userEntityModel = userModelAssembler.toModel(userRepository.save(newUser));
        logger.trace("Creating new user "+ userEntityModel);
        return ResponseEntity //
                .created(userEntityModel.getRequiredLink(IanaLinkRelations.SELF).toUri()) //
                .body(userEntityModel);
    }



    //Editing existing user after making sure of his password
    @PutMapping("/users/{id}")
    ResponseEntity<?> edUser(@RequestBody User editUser, @PathVariable Long id,@RequestParam String password) {
        logger.info("A update user request initialized ");
        User updatedUser = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        if (updatedUser.getPassword().equals(password)){
            updatedUser.setId(editUser.getId());
            updatedUser.setName(editUser.getName());
            updatedUser.setEmail(editUser.getEmail());
            updatedUser.setAge(editUser.getAge());
            EntityModel<User> userEntityModel = userModelAssembler.toModel(updatedUser);
            logger.trace("updating user information "+userEntityModel);
            return ResponseEntity //
                    .created(userEntityModel.getRequiredLink(IanaLinkRelations.SELF).toUri()) //
                    .body(userEntityModel);
        }else {

            throw new PasswordNotCorrectException();
        }
    }



    //Delete the user by his id and verify the password of that user
    @DeleteMapping("/users/{id}")
    void deleteUser(@PathVariable Long id,@RequestParam String password, HttpServletResponse response) throws IOException {
        logger.info("A Delete user request initialized ");
        User user = userRepository.findById(id) //
                .orElseThrow(() -> new UserNotFoundException(id));
        if(user.getPassword().equals(password)) {
            userRepository.deleteById(id);
            taskRepository.deleteAllByUser_Id(id);
            logger.trace("Redirecting to the /Users page after deleting a user with id : "+ id );
            response.sendRedirect("");

        }
        else{
            throw new PasswordNotCorrectException();

        }

    }

}
