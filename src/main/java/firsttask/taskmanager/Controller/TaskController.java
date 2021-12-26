package firsttask.taskmanager.Controller;


import firsttask.taskmanager.Services.TaskServices;
import firsttask.taskmanager.domain.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.Optional;


@RestController// don't ever forget this annotation
@RequestMapping("/user")
public class TaskController {
    private static  final Logger LOGGER = LoggerFactory.getLogger(TaskController.class);
    private TaskServices taskServices;
    public TaskController(TaskServices taskServices) {
        this.taskServices = taskServices;
    }


    @GetMapping("/tasks")
    public Page<Task> returnAllTasks(@RequestParam Optional<Integer> page, @RequestParam Optional<String> sortBy,@RequestParam Optional<String> sortDir) {
        LOGGER.info("A get all tasks  request initialized ");
        LOGGER.trace("retrieve all tasks ");
        return taskServices.returnAllTasks(page,sortBy,sortDir);
    }
    @GetMapping("/tasks/{id}")
    public Task returnTask(@PathVariable Long id) throws  AccessDeniedException {
        LOGGER.info("A get task request initialized ");
            LOGGER.trace("retrieve task with id "+ id );
            return  taskServices.returnTask(id);
    }
    @PostMapping("/tasks")
    public Task createTask(@RequestBody Task task) {
        LOGGER.info("A create task request initialized ");
        LOGGER.trace("Creating new  task");
        return taskServices.createTask(task);
    }
    @PutMapping("/tasks/{id}")
    public Task edTask(@RequestBody Task editTask, @PathVariable Long id) throws AccessDeniedException {
        LOGGER.info("A Update task request initialized ");
        LOGGER.trace("Updating a task to a user with id : " + id );
            return taskServices.editOneTask(editTask,id);


    }
    @DeleteMapping("/tasks/{id}")
    public void  deleteTask(@PathVariable Long id) throws IOException {
        LOGGER.info("A delete task  request initialized ");
            LOGGER.trace("Redirecting to the Tasks page after deleting task with id : " + id);

          taskServices.deleteTask(id);



    }
}
