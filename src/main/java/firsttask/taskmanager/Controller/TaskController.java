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

public class TaskController {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final UserModelAssembler userModelAssembler;
    private final TaskModelAssembler taskModelAssembler;
    private static Logger logger = LoggerFactory.getLogger(TaskController.class);


    public TaskController(TaskRepository taskRepository, UserRepository userRepository,UserModelAssembler UserAssembler, TaskModelAssembler TaskAssembler) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.userModelAssembler=UserAssembler;
        this.taskModelAssembler=TaskAssembler;
        logger.trace("creating Repositories and assemblers ");
    }

    //return all tasks
    @GetMapping("/Tasks")
    public CollectionModel<EntityModel<Task>> returnAllTasks() {
        logger.info("A get all tasks  request initialized ");
        List<EntityModel<Task>> tasks = taskRepository.findAll().stream() //
                .map(taskModelAssembler::toModel) //
                .collect(Collectors.toList());
        logger.trace("retrieve all tasks ");
        return CollectionModel.of(tasks, linkTo(methodOn(TaskController.class).returnAllTasks()).withSelfRel());
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



    @PostMapping("/tasks")
    ResponseEntity<?> CreateTask(@RequestBody Task task, @RequestParam Long id ){
        logger.info("A create task request initialized ");
        User user = userRepository.findById(id).orElseThrow(()->new UserNotFoundException(id));

            EntityModel<Task> newtask = taskModelAssembler.toModel(taskRepository.save(task));
            task.setUser(user);
            //notice that we have to add the task to the user object and add the user object to the task so that jpa can create load the table correctly for us
            user.addTask(task);
            userRepository.save(user);
            logger.trace("Creating new  task"+newtask);
            return ResponseEntity.created(newtask.getRequiredLink(IanaLinkRelations.SELF).toUri()).body(newtask);

    }



    // edit the task from the owner user only so we make authentication first
    @PutMapping("/Tasks/{id}")
    ResponseEntity<?> edTask(@RequestBody Task editTask, @PathVariable Long id) {
        logger.info("A Update task request initialized ");
      /*  User updatedUser = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(id));*/
        Task updatedTask= taskRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));


            updatedTask.setId(editTask.getId());
            updatedTask.setDescription(editTask.getDescription());
            updatedTask.setCompleted(editTask.isCompleted());
            EntityModel<Task> taskEntityModel = taskModelAssembler.toModel(updatedTask);
            // the correct way is that we must give a request param for each of the attributes and change what required
            logger.trace("Updating a task to a user with id : "+ id +" The task : "+taskEntityModel);
            return ResponseEntity //
                    .created(taskEntityModel.getRequiredLink(IanaLinkRelations.SELF).toUri()) //
                    .body(taskEntityModel);


    }



    // delete a task from the owner only
    @DeleteMapping("/Tasks/{id}")
    void  deleteTask(@PathVariable Long id, @RequestParam Long userId, HttpServletResponse response) throws IOException {
        logger.info("A delete task  request initialized ");
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(id));
        Task task = taskRepository.findById(id).orElseThrow(() -> new TaskNotFoundException(id));

            taskRepository.deleteById(id);
            logger.trace("Redirecting to the Tasks page after deleting task with id : " +id);
            response.sendRedirect("");



    }

}
