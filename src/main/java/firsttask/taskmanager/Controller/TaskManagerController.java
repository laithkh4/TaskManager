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
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.mediatype.problem.Problem;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;
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



    public TaskManagerController(TaskRepository taskRepository, UserRepository userRepository,UserModelAssembler UserAssembler, TaskModelAssembler TaskAssembler) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.userModelAssembler=UserAssembler;
        this.taskModelAssembler=TaskAssembler;
    }



    //return all the users in that database
    @GetMapping("/Users")
    public CollectionModel<EntityModel<User>> returnAllUsers() {

        List<EntityModel<User>> users = userRepository.findAll().stream() //
                .map(userModelAssembler::toModel) //
                .collect(Collectors.toList());

        return CollectionModel.of(users, linkTo(methodOn(TaskManagerController.class).returnAllUsers()).withSelfRel());
    }



    // return a  user and his task
    @GetMapping("/Users/{id}")
    public EntityModel<User> returnUser(@PathVariable Long id) {

        User user = userRepository.findById(id) //
                .orElseThrow(() -> new UserNotFoundException(id));

        return userModelAssembler.toModel(user);
    }



    //creating user
    @PostMapping("/CreateUser")
    ResponseEntity<EntityModel<User>> createNewUser(@RequestBody User newUser) {

        EntityModel<User> userEntityModel = userModelAssembler.toModel(userRepository.save(newUser));

        return ResponseEntity //
                .created(userEntityModel.getRequiredLink(IanaLinkRelations.SELF).toUri()) //
                .body(userEntityModel);
    }



    //Editing existing user after making sure of his password
    @PutMapping("/Users/{id}")
    ResponseEntity<?> edUser(@RequestBody User editUser, @PathVariable Long id,@RequestParam String password) {
        User updatedUser = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        if (updatedUser.getPassword().equals(password)){
            updatedUser.setId(editUser.getId());
            updatedUser.setName(editUser.getName());
            updatedUser.setEmail(editUser.getEmail());
            updatedUser.setAge(editUser.getAge());
            EntityModel<User> userEntityModel = userModelAssembler.toModel(updatedUser);
            return ResponseEntity //
                    .created(userEntityModel.getRequiredLink(IanaLinkRelations.SELF).toUri()) //
                    .body(userEntityModel);
        }else {
            throw new PasswordNotCorrectException();
        }
    }



    //Delete the user by his id and verify the password of that user
    @DeleteMapping("/Users/{id}")
    ResponseEntity<?> deleteUser(@PathVariable Long id,@RequestParam String password) {
        System.out.println("inside delete users method  ");
        User user = userRepository.findById(id) //
                .orElseThrow(() -> new UserNotFoundException(id));

        if(user.getPassword().equals(password)) {
           userRepository.deleteById(id);
           taskRepository.deleteAllByUser_Id(id);

            return ResponseEntity.noContent().build();

        }
        else{
         throw new PasswordNotCorrectException();

        }

 }



    //return all tasks
    @GetMapping("/Tasks")
    public CollectionModel<EntityModel<Task>> returnAllTasks() {
        System.out.println("inside get tasks method  ");
        List<EntityModel<Task>> tasks = taskRepository.findAll().stream() //
                .map(taskModelAssembler::toModel) //
                .collect(Collectors.toList());

        return CollectionModel.of(tasks, linkTo(methodOn(TaskManagerController.class).returnAllTasks()).withSelfRel());
    }



    //return a task by its id
    @GetMapping("/Tasks/{id}")
    public EntityModel<Task> returnTask(@PathVariable Long id) {
        System.out.println("inside get task method  ");
        Task task = taskRepository.findById(id) //
                .orElseThrow(() -> new UserNotFoundException(id));

        return taskModelAssembler.toModel( task);
    }



    @PostMapping("/createTask")
    ResponseEntity<?> CreateTask(@RequestBody Task task, @RequestParam Long id,@RequestParam String password ){
        User user = userRepository.findById(id).orElseThrow(()->new UserNotFoundException(id));
        if (user.getPassword().equals(password)){
            EntityModel<Task> newtask = taskModelAssembler.toModel(taskRepository.save(task));
            task.setUser(user);
            //notice that we have to add the task to the user object and add the user object to the task so that jpa can create load the table correctly for us
            user.addTask(task);
            userRepository.save(user);
            return ResponseEntity.created(newtask.getRequiredLink(IanaLinkRelations.SELF).toUri()).body(newtask);
        }else{
            throw new PasswordNotCorrectException();
        }
    }



    // edit the task from the owner user only so we make authentication first
    @PutMapping("/Tasks/{id}")
    ResponseEntity<?> edTask(@RequestBody Task editTask, @PathVariable Long id,@RequestParam Long userId,@RequestParam String password) {
        System.out.println("inside put tasks method  ");
        User updatedUser = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(id));
        Task updatedTask= taskRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        if (updatedUser.getPassword().equals(password)){
            updatedTask.setId(editTask.getId());
            updatedTask.setDescription(editTask.getDescription());
            updatedTask.setCompleted(editTask.isCompleted());
            EntityModel<Task> TaskentityModel = taskModelAssembler.toModel(updatedTask);
            // the correct way is that we must give a request param for each of the attributes and change what required
            return ResponseEntity //
                    .created(TaskentityModel.getRequiredLink(IanaLinkRelations.SELF).toUri()) //
                    .body(TaskentityModel);
            // return ResponseEntity.ok(userModelAssembler.toModel(userRepository.save(updatedUser)));
        }else {
            throw new PasswordNotCorrectException();
        }

    }



    // delete a task from the owner only
    @DeleteMapping("/Tasks/{id}")
    ResponseEntity<?> deleteTask(@PathVariable Long id,@RequestParam Long userId, @RequestParam String password) {
        System.out.println("inside delete tasks method  ");
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(id));
        Task task= taskRepository.findById(id).orElseThrow(() -> new TaskNotFoundException(id));
        if(user.getPassword().equals(password) ) {
        taskRepository.deleteById(id);

            return ResponseEntity.noContent().build();

        }
        else{
            throw new PasswordNotCorrectException();

        }

    }

}




