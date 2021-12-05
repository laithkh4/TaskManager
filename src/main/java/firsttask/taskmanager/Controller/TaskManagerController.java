package firsttask.taskmanager.Controller;


import firsttask.taskmanager.Exceptions.PasswordNotCorrectException;
import firsttask.taskmanager.Exceptions.TaskNotFoundException;
import firsttask.taskmanager.Exceptions.UserNotFoundException;
import firsttask.taskmanager.Repositories.TaskRepository;
import firsttask.taskmanager.Repositories.UserRepository;
import firsttask.taskmanager.assemblers.TaskModelAssembler;
import firsttask.taskmanager.assemblers.UserModelAssembler;
import firsttask.taskmanager.domain.Task;
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



@RestController
public class TaskManagerController {


    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final UserModelAssembler userModelAssembler;
    private final TaskModelAssembler taskModelAssembler;
    private static Logger logger = LoggerFactory.getLogger(TaskManagerController.class);


    public TaskManagerController(TaskRepository taskRepository, UserRepository userRepository,UserModelAssembler UserAssembler, TaskModelAssembler TaskAssembler) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.userModelAssembler=UserAssembler;
        this.taskModelAssembler=TaskAssembler;
        logger.trace("creating Repositories and assemblers ");
    }



    //return all the users in that database
    @GetMapping("/Users")
    public CollectionModel<EntityModel<User>> returnAllUsers() {
        logger.info("A get all users request initialized ");
        List<EntityModel<User>> users = userRepository.findAll().stream() //
                .map(userModelAssembler::toModel) //
                .collect(Collectors.toList());
        logger.trace("retrieving all the users " );
        return CollectionModel.of(users, linkTo(methodOn(TaskManagerController.class).returnAllUsers()).withSelfRel());
    }



    // return a  user and his task
    @GetMapping("/Users/{id}")
    public EntityModel<User> returnUser(@PathVariable Long id)  {

        User user = userRepository.findById(id) //
                .orElseThrow(() -> new UserNotFoundException(id));
       // if (user.getPassword().equals(password)) {
            //logger.trace("retrieving  the user to the user with the id " + id);
            return userModelAssembler.toModel(user);
        /*}else{
            throw new PasswordNotCorrectException();
        }*/
    }/*
    @GetMapping("/Users/{id}")
    public EntityModel<User> returnUser(@PathVariable Long id,@RequestParam String password)  {
        logger.info("A get user request initialized ");
        User user = userRepository.findById(id) //
                .orElseThrow(() -> new UserNotFoundException(id));
         if (user.getPassword().equals(password)) {
        logger.trace("retrieving  the user to the user with the id " + id);
        return userModelAssembler.toModel(user);
        }else{
            throw new PasswordNotCorrectException();
        }
    }

*/
    //creating user
    @PostMapping("/CreateUser")
    ResponseEntity<EntityModel<User>> createNewUser(@RequestBody User newUser) {
        logger.info("A create user request initialized ");
        EntityModel<User> userEntityModel = userModelAssembler.toModel(userRepository.save(newUser));
        logger.trace("Creating new user "+ userEntityModel);
        return ResponseEntity //
                .created(userEntityModel.getRequiredLink(IanaLinkRelations.SELF).toUri()) //
                .body(userEntityModel);
    }



    //Editing existing user after making sure of his password
    @PutMapping("/Users/{id}")
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
    @DeleteMapping("/Users/{id}")
    void deleteUser(@PathVariable Long id,@RequestParam String password, HttpServletResponse response) throws IOException  {
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



    //return all tasks
    @GetMapping("/Tasks")
    public CollectionModel<EntityModel<Task>> returnAllTasks() {
        logger.info("A get all tasks  request initialized ");
        List<EntityModel<Task>> tasks = taskRepository.findAll().stream() //
                .map(taskModelAssembler::toModel) //
                .collect(Collectors.toList());
            logger.trace("retrieve all tasks ");
        return CollectionModel.of(tasks, linkTo(methodOn(TaskManagerController.class).returnAllTasks()).withSelfRel());
    }



    //return a task by its id
    @GetMapping("/Tasks/{id}")
    public EntityModel<Task> returnTask(@PathVariable Long id) {
        logger.info("A get task request initialized ");
        Task task = taskRepository.findById(id) //
                .orElseThrow(() -> new UserNotFoundException(id));
        logger.trace("retrieve task with id "+ id );
        return taskModelAssembler.toModel( task);
    }



    @PostMapping("/createTask")
    ResponseEntity<?> CreateTask(@RequestBody Task task, @RequestParam Long id,@RequestParam String password ){
        logger.info("A create task request initialized ");
        User user = userRepository.findById(id).orElseThrow(()->new UserNotFoundException(id));
        if (user.getPassword().equals(password)){
            EntityModel<Task> newtask = taskModelAssembler.toModel(taskRepository.save(task));
            task.setUser(user);
            //notice that we have to add the task to the user object and add the user object to the task so that jpa can create load the table correctly for us
            user.addTask(task);
            userRepository.save(user);
            logger.trace("Creating new  task"+newtask);
            return ResponseEntity.created(newtask.getRequiredLink(IanaLinkRelations.SELF).toUri()).body(newtask);
        }else{
            throw new PasswordNotCorrectException();
        }
    }



    // edit the task from the owner user only so we make authentication first
    @PutMapping("/Tasks/{id}")
    ResponseEntity<?> edTask(@RequestBody Task editTask, @PathVariable Long id,@RequestParam Long userId,@RequestParam String password) {
       logger.info("A Update task request initialized ");
        User updatedUser = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(id));
        Task updatedTask= taskRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        if (updatedUser.getPassword().equals(password)){
            updatedTask.setId(editTask.getId());
            updatedTask.setDescription(editTask.getDescription());
            updatedTask.setCompleted(editTask.isCompleted());
            EntityModel<Task> taskEntityModel = taskModelAssembler.toModel(updatedTask);
            // the correct way is that we must give a request param for each of the attributes and change what required
            logger.trace("Updating a task to a user with id : "+ id +" The task : "+taskEntityModel);
            return ResponseEntity //
                    .created(taskEntityModel.getRequiredLink(IanaLinkRelations.SELF).toUri()) //
                    .body(taskEntityModel);
            // return ResponseEntity.ok(userModelAssembler.toModel(userRepository.save(updatedUser)));
        }else {
            throw new PasswordNotCorrectException();
        }

    }



    // delete a task from the owner only
    @DeleteMapping("/Tasks/{id}")
  /*  ResponseEntity<?>*/void  deleteTask(@PathVariable Long id, @RequestParam Long userId, @RequestParam String password, HttpServletResponse response) throws IOException {
        logger.info("A delete task  request initialized ");
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(id));
        Task task = taskRepository.findById(id).orElseThrow(() -> new TaskNotFoundException(id));
        if (user.getPassword().equals(password)) {
            taskRepository.deleteById(id);
            logger.trace("Redirecting to the Tasks page after deleting task with id : " +id);
            response.sendRedirect("");
        } else {
            throw new PasswordNotCorrectException();

        }


    }
}




