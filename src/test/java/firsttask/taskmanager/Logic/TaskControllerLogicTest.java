package firsttask.taskmanager.Logic;

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
import org.springframework.security.test.context.support.WithMockUser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
@ExtendWith(MockitoExtension.class)
class TaskControllerLogicTest {
    @Mock
    private  TaskRepository taskRepository;
    @Mock
    private  UserRepository userRepository;

    @InjectMocks
    private TaskControllerLogic taskControllerLogic;

    @WithMockUser
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
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(user);
        Optional<Integer> page = Optional.of(1);
        Optional<String> sortBy=Optional.of("id");
        Optional<String> sortDir=Optional.of("desc");
        when( taskRepository.findAllByUser_Id(user.getId(),
                PageRequest.of(page.orElse(0),5, Sort.Direction.fromString(sortDir.orElse("desc")), sortBy.orElse("id"))))
                .thenReturn(pages);
        assertEquals(taskControllerLogic.returnAllTasks(page,sortBy,sortDir),pages);
    }
}