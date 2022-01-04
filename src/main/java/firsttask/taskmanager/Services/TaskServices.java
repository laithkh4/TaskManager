package firsttask.taskmanager.Services;

import firsttask.taskmanager.Exceptions.DateNotAllowedException;
import firsttask.taskmanager.Exceptions.UserNotFoundException;
import firsttask.taskmanager.Repositories.TaskRepository;
import firsttask.taskmanager.Repositories.UserRepository;
import firsttask.taskmanager.domain.Task;
import firsttask.taskmanager.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class TaskServices {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    public TaskServices(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    public Page<Task> returnAllTasks(Optional<Integer> page,  Optional<String> sortBy,  Optional<String> sortDir) {
        User requestingUser= (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return taskRepository.findAllByUser_Id(requestingUser.getId(),
                PageRequest.of(page.orElse(0),5, Sort.Direction.fromString(sortDir.orElse("desc")), sortBy.orElse("id")));
    }
    public Task returnTask( Long id) throws AccessDeniedException {
        Task task = taskRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        User requestedUser = userRepository.findById(task.getUserId()).orElseThrow(() -> new UserNotFoundException(task.getUserId()));
        User requestingUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (requestedUser.getId().longValue()==requestingUser.getId().longValue() && requestedUser.getPassword().equals(requestingUser.getPassword())) {
            return  task;}
        else {
            throw new AccessDeniedException("You are not allowed to access this page!");
        }
    }
    public Task createTask( Task task) {
        User requestingUser= (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        checkDate(task,false);
        Task newTask = taskRepository.save(task);
        task.setUser(requestingUser);
        //notice that we have to add the task to the user object and add the user object to the task so that jpa can create load the table correctly for us
        requestingUser.addTask(task);
        userRepository.save(requestingUser);
        return newTask;
    }
    private void checkDate(Task task,boolean editedTask) {
        Date startDateToCheck = task.getStartDate();
        Date endDateToCheck = task.getEndDate();
        User requestingUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Task> tasks = taskRepository.findAllByUser_Id(requestingUser.getId());
        for(Task testTask : tasks){
            if(editedTask && task.getId().longValue() == testTask.getId().longValue())
                continue;
            if (startDateToCheck.after(testTask.getStartDate()) && startDateToCheck.before(testTask.getEndDate()) )
                throw new DateNotAllowedException();
            else if (endDateToCheck.after(testTask.getStartDate()) && endDateToCheck.before(testTask.getEndDate()))
                throw new DateNotAllowedException();
            else if (startDateToCheck.equals(testTask.getStartDate()) ||startDateToCheck.equals(testTask.getEndDate()) )
                throw new DateNotAllowedException();
            else if (endDateToCheck.equals(testTask.getStartDate()) ||endDateToCheck.equals(testTask.getEndDate()) )
                throw new DateNotAllowedException();
        }
    }
    public Task editOneTask( Task editTask, Long id) throws AccessDeniedException {
        Task task = taskRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        User requestedUser=userRepository.findById(task.getUserId()).orElseThrow(() -> new UserNotFoundException(task.getUserId()));
        User requestingUser= (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (requestedUser.getId().longValue()==requestingUser.getId().longValue() && requestedUser.getPassword().equals(requestingUser.getPassword())) {
            checkDate(task,true);
            task.setDescription(editTask.getDescription());
            task.setCompleted(editTask.isCompleted());
            task.setEndDate(editTask.getEndDate());
            task.setStartDate(editTask.getStartDate());
            taskRepository.save(task);
            return task;
        }
        else {
            throw new AccessDeniedException("You are not allowed to access this page!");
        }
    }
    public void  deleteTask( Long id) throws  AccessDeniedException{
        Task task = taskRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        User requestedUser=userRepository.findById(task.getUserId()).orElseThrow(() -> new UserNotFoundException(task.getUserId()));
        User requestingUser= (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (requestedUser.getId().longValue()==requestingUser.getId().longValue() && requestedUser.getPassword().equals(requestingUser.getPassword())) {
            if (taskRepository.existsById(id)) {

                taskRepository.deleteById(id);
            }
        }else {
            throw new AccessDeniedException("You are not allowed to access this page!");
        }

    }
}
