package firsttask.taskmanager.Controller;

import firsttask.taskmanager.Exceptions.GeneralException;
import firsttask.taskmanager.Exceptions.UserNotFoundException;
import firsttask.taskmanager.Repositories.TaskRepository;
import firsttask.taskmanager.Repositories.UserRepository;
import firsttask.taskmanager.domain.Task;
import firsttask.taskmanager.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;


@RestController// don't ever forget this annotation
public class TaskController {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    private static  final Logger logger = LoggerFactory.getLogger(TaskController.class);


    public TaskController(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        logger.trace("creating Repositories and assemblers ");
    }

    //return all tasks
    @GetMapping("/tasks")
    public List<Task> returnAllTasks() throws GeneralException{
        logger.info("A get all tasks  request initialized ");
        List<Task> tasks = taskRepository.findAll();
        logger.trace("retrieve all tasks ");
        return tasks;
    }



    //return a task by its id
    @GetMapping("/tasks/{id}")
    public Task returnTask(@PathVariable Long id) throws GeneralException{
        logger.info("A get task request initialized ");
        Task task = taskRepository.findById(id) //
                .orElseThrow(() -> new UserNotFoundException(id));
        logger.trace("retrieve task with id "+ id );
        return  task;
    }



    @PostMapping("/tasks")
    Task CreateTask(@RequestBody Task task, @RequestParam Long id ) throws GeneralException{

        logger.info("A create task request initialized ");
        User user = userRepository.findById(id).orElseThrow(()->new UserNotFoundException(id));

            Task newTask = taskRepository.save(task);
          //  task.setUser(user);
            //notice that we have to add the task to the user object and add the user object to the task so that jpa can create load the table correctly for us
            user.addTask(task);
            userRepository.save(user);
            logger.trace("Creating new  task"+newTask);
            return newTask;

    }



    // edit the task from the owner user only so we make authentication first
    @PutMapping("/tasks/{id}")
    Task edTask(@RequestBody Task editTask, @PathVariable Long id) throws GeneralException{
        logger.info("A Update task request initialized ");
        Task updatedTask= taskRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
            updatedTask.setDescription(editTask.getDescription());
            updatedTask.setCompleted(editTask.isCompleted());
            // the correct way is that we must give a request param for each of the attributes and change what required
            logger.trace("Updating a task to a user with id : "+ id +" The task : "+updatedTask);
            return updatedTask;


    }



    // delete a task from the owner only
    @DeleteMapping("/tasks/{id}")
    void  deleteTask(@PathVariable Long id,  HttpServletResponse response) throws IOException,GeneralException {
        logger.info("A delete task  request initialized ");

        if (taskRepository.existsById(id)) {
            taskRepository.deleteById(id);
        }


            logger.trace("Redirecting to the Tasks page after deleting task with id : " +id);
            response.sendRedirect("");



    }

}
