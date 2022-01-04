package firsttask.taskmanager.Services;

import firsttask.taskmanager.Exceptions.DateNotAllowedException;
import firsttask.taskmanager.Repositories.TaskRepository;
import firsttask.taskmanager.Repositories.UserRepository;
import firsttask.taskmanager.domain.Task;
import firsttask.taskmanager.domain.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServicesTest {
    @Mock
    private  TaskRepository taskRepository;
    @Mock
    private  UserRepository userRepository;
    @InjectMocks
    private TaskServices taskServices;

    @Test
    void returnAllTasks() {
    List<Task> tasks= new ArrayList<>();
        for(int i=0;i<2;i++) {
            Task task= new Task("description",false,new Date(),new Date());
            tasks.add(task);
        }
        Page<Task> pages= new PageImpl<>(tasks,  PageRequest.of(0, 2), tasks.size());

        User user = new User((long)12,"Laith","password","laithmosheer@gmail.com",22);
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(user);
        Optional<Integer> page = Optional.of(1);
        Optional<String> sortBy=Optional.of("id");
        Optional<String> sortDir=Optional.of("desc");
        when( taskRepository.findAllByUser_Id(user.getId(),
                PageRequest.of(page.orElse(0),5, Sort.Direction.fromString(sortDir.orElse("desc")), sortBy.orElse("id"))))
                .thenReturn(pages);
        assertEquals(pages, taskServices.returnAllTasks(page,sortBy,sortDir));
    }
    @Test
    void returnTaskSuccess() throws AccessDeniedException {
        User user = new User((long)12,"Laith","password","laithmosheer@gmail.com",22);
        Task task = new Task("Testing the application",false, new Date(System.currentTimeMillis() - 1000 * 60 * 60 * 10) , new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 2));
        task.setUser(user);
        when(taskRepository.findById(task.getId())).thenReturn(Optional.of(task));
        when(userRepository.findById(task.getUserId())).thenReturn(Optional.of(user));

        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(user);

        assertEquals(task,taskServices.returnTask(task.getId()));

    }
    @Test
    void returnTaskFail() throws AccessDeniedException {

            User user = new User((long) 12, "Laith", "password", "laithmosheer@gmail.com", 22);
            Task task = new Task("Testing the application", false, new Date(System.currentTimeMillis() - 1000 * 60 * 60 * 10), new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 2));
            task.setUser(user);
            when(taskRepository.findById(task.getId())).thenReturn(Optional.of(task));
            User user2 = new User((long) 13, "Ahmad", "password", "Ahmad@gmail.com", 22);
            when(userRepository.findById(task.getUserId())).thenReturn(Optional.of(user2));
            Authentication authentication = Mockito.mock(Authentication.class);
            SecurityContext securityContext = Mockito.mock(SecurityContext.class);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            SecurityContextHolder.setContext(securityContext);
            when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(user);
            assertThrows(AccessDeniedException.class, ()-> taskServices.returnTask(task.getId()));
    }
    @Test
    void createTaskSuccess() {
        User user = new User((long)12,"Laith","password","laithmosheer@gmail.com",22);
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(user);
        Task task = new Task("Testing the application",false, new Date(System.currentTimeMillis() - 1000 * 60 * 60 * 10) , new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 2));
        //task.setUser(user);
        when(taskRepository.save(task)).thenReturn(task);
        assertEquals(task,taskServices.createTask(task));
    }
    @Test
    void createTaskFail() {
        User user = new User((long)12,"Laith","password","laithmosheer@gmail.com",22);
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(user);
        Task task = new Task("Testing the application",false, new Date(System.currentTimeMillis() - 1000 * 60 * 60 * 10) , new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 2));
        List<Task> tasks = new ArrayList<>();
            Task ta= new Task("description",false,new Date(System.currentTimeMillis() - 1000 * 60 * 60 * 11 ) , new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 3));
            tasks.add(task);
        when(taskRepository.findAllByUser_Id(user.getId())).thenReturn(tasks);
        assertThrows(DateNotAllowedException.class,()->taskServices.createTask(task));
    }
    @Test
    void editOneTaskSuccess() throws AccessDeniedException {
        User user = new User((long)12,"Laith","password","laithmosheer@gmail.com",22);
        Task task = new Task("Testing the application",false, new Date(System.currentTimeMillis() - 1000 * 60 * 60 * 10) , new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 2));
       task.setUser(user);
       user.addTask(task);
        Authentication authentication = Mockito.mock(Authentication.class);
        when(userRepository.findById(task.getUserId())).thenReturn(Optional.of(user));
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(user);
        when(taskRepository.findById(task.getId())).thenReturn(Optional.of(task));
        assertEquals(task,taskServices.editOneTask(task,task.getId()));
    }
    @Test
    void editOneTaskFail ()  {
        User user = new User((long)12,"Laith","password","laithmosheer@gmail.com",22);
        Task task = new Task("Testing the application",false, new Date(System.currentTimeMillis() - 1000 * 60 * 60 * 10) , new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 2));
        task.setUser(user);
        user.addTask(task);

        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        User user2 = new User((long)13,"Ahmad","password","Ahmad@gmail.com",22);
        when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(user2);

        when(userRepository.findById(task.getUserId())).thenReturn(Optional.of(user));
        when(taskRepository.findById(task.getId())).thenReturn(Optional.of(task));
        assertThrows(AccessDeniedException.class,()->taskServices.editOneTask(task,task.getId()));
    }

    @Test
    void deleteTaskSuccess() throws AccessDeniedException {

        User user = new User((long)12,"Laith","password","laithmosheer@gmail.com",22);
        Task task = new Task("Testing the application",false, new Date(System.currentTimeMillis() - 1000 * 60 * 60 * 10) , new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 2));
        task.setId((long)1);
        task.setUser(user);

        user.addTask(task);
        when(taskRepository.findById(task.getId())).thenReturn(Optional.of(task));
        Authentication authentication = mock(Authentication.class);
        when(userRepository.findById(task.getUserId())).thenReturn(Optional.of(user));
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(user);
      //   when(taskRepository.findById(task.getId())).thenReturn(Optional.of(task));
        when(taskRepository.existsById(anyLong())).thenReturn(true);
        taskServices.deleteTask((long)1);

        verify(taskRepository,times(1)).deleteById((long)1);
    }
    @Test
    void deleteTaskFail() throws AccessDeniedException {

        User user = new User((long)12,"Laith","password","laithmosheer@gmail.com",22);
        Task task = new Task("Testing the application",false, new Date(System.currentTimeMillis() - 1000 * 60 * 60 * 10) , new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 2));
        task.setId((long)1);
        task.setUser(user);
        user.addTask(task);
        when(taskRepository.findById(task.getId())).thenReturn(Optional.of(task));
        User user2 = new User((long)14,"ahmad","password","ahmad@gmail.com",22);
        Authentication authentication = mock(Authentication.class);
        when(userRepository.findById(task.getUserId())).thenReturn(Optional.of(user));
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(user2);
        assertThrows(AccessDeniedException.class,()->taskServices.deleteTask((long)1));
    }
}