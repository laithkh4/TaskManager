package firsttask.taskmanager.Controller;


import firsttask.taskmanager.Exceptions.UserNotFoundException;
import firsttask.taskmanager.Repositories.TaskRepository;
import firsttask.taskmanager.Repositories.UserRepository;
import firsttask.taskmanager.domain.Task;
import firsttask.taskmanager.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.List;


@RestController// don't ever forget this annotation
public class TaskController {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private static  final Logger LOGGER = LoggerFactory.getLogger(TaskController.class);
    public TaskController(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    //return all tasks
    @GetMapping("/tasks")
    public List<Task> returnAllTasks() {
        LOGGER.info("A get all tasks  request initialized ");
        User requestingUser= (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Task> tasks = taskRepository.findAllByUser_Id(requestingUser.getId());
        LOGGER.trace("retrieve all tasks ");
        return tasks;
    }



    //return a task by its id
    @GetMapping("/tasks/{id}")
    public Task returnTask(@PathVariable Long id) throws  AccessDeniedException {
        LOGGER.info("A get task request initialized ");
        Task task = taskRepository.findById(id) //
                .orElseThrow(() -> new UserNotFoundException(id));
        User requestedUser=userRepository.findById(task.getUserId()).orElseThrow(() -> new UserNotFoundException(task.getUserId()));
        User requestingUser= (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (requestedUser.getId().longValue()==requestingUser.getId().longValue() && requestedUser.getPassword().equals(requestingUser.getPassword())) {
            LOGGER.trace("retrieve task with id "+ id );
        return  task;}
        else {
            throw new AccessDeniedException("You are not allowed to access this page!");
        }
    }



    @PostMapping("/tasks")
    Task createTask(@RequestBody Task task) {
        LOGGER.info("A create task request initialized ");
        User requestingUser= (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Task newTask = taskRepository.save(task);
           task.setUser(requestingUser);
            //notice that we have to add the task to the user object and add the user object to the task so that jpa can create load the table correctly for us
            requestingUser.addTask(task);
            userRepository.save(requestingUser);
        LOGGER.trace("Creating new  task");
            return newTask;
    }



    // edit the task from the owner user only so we make authentication first
    @PutMapping("/tasks/{id}")
    Task edTask(@RequestBody Task editTask, @PathVariable Long id) throws AccessDeniedException {
        LOGGER.info("A Update task request initialized ");
        Task task = taskRepository.findById(id) //
                .orElseThrow(() -> new UserNotFoundException(id));
        User requestedUser=userRepository.findById(task.getUserId()).orElseThrow(() -> new UserNotFoundException(task.getUserId()));
        User requestingUser= (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (requestedUser.getId().longValue()==requestingUser.getId().longValue() && requestedUser.getPassword().equals(requestingUser.getPassword())) {

            Task updatedTask = taskRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
            updatedTask.setDescription(editTask.getDescription());
            updatedTask.setCompleted(editTask.isCompleted());
            LOGGER.trace("Updating a task to a user with id : " + id );
            return updatedTask;
        } else {
            throw new AccessDeniedException("You are not allowed to access this page!");
        }

    }



    // delete a task from the owner only
    @DeleteMapping("/tasks/{id}")
    void  deleteTask(@PathVariable Long id,  HttpServletResponse response) throws IOException {
        LOGGER.info("A delete task  request initialized ");
        Task task = taskRepository.findById(id) //
                .orElseThrow(() -> new UserNotFoundException(id));
        User requestedUser=userRepository.findById(task.getUserId()).orElseThrow(() -> new UserNotFoundException(task.getUserId()));
        User requestingUser= (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (requestedUser.getId().longValue()==requestingUser.getId().longValue() && requestedUser.getPassword().equals(requestingUser.getPassword())) {

            if (taskRepository.existsById(id)) {
                taskRepository.deleteById(id);
            }


            LOGGER.trace("Redirecting to the Tasks page after deleting task with id : " + id);
            response.sendRedirect("");

        }else {
            throw new AccessDeniedException("You are not allowed to access this page!");
        }

    }

}
