package firsttask.taskmanager.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import firsttask.taskmanager.Repositories.UserRepository;
import firsttask.taskmanager.Security.JWTSecurity.JwtUtil;
import firsttask.taskmanager.Security.UserDetailsServiceImpl;
import firsttask.taskmanager.Services.TaskServices;
import firsttask.taskmanager.domain.Task;
import firsttask.taskmanager.domain.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(TaskController.class)
class TaskControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper mapper;
    @MockBean
    TaskServices taskServices;
    @MockBean
    UserDetailsServiceImpl userDetailsService;
    @MockBean
    AuthenticationManager authenticationManager;
    @MockBean
    JwtUtil jwtUtil;
    @MockBean
    UserRepository userRepository;

    @WithMockUser
    @Test
    void returnAllTasks() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/user/tasks")
                .contentType(MediaType.APPLICATION_JSON);
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response= mvcResult.getResponse();
        assertEquals(HttpStatus.OK.value(),response.getStatus());
    }

    @WithMockUser
    @Test
    void returnTask() throws Exception{
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/user/tasks/1")
                .contentType(MediaType.APPLICATION_JSON);
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response= mvcResult.getResponse();
        assertEquals(HttpStatus.OK.value(),response.getStatus());
    }

    @WithMockUser
    @Test
    void createTask() throws Exception{
        Task task = new Task();
        task.setCompleted(false);
        task.setDescription("testing the tasks controller");
        User user = new User((long)1,"Laith","password","laithmosheer@gmail.com",22);
        task.setUser(user);
        String JsonTask = mapper.writeValueAsString(task);
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/user/tasks")
                .accept(MediaType.APPLICATION_JSON).content(JsonTask)
                .contentType(MediaType.APPLICATION_JSON);
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response= mvcResult.getResponse();
        assertEquals(HttpStatus.OK.value(),response.getStatus());
    }

    @WithMockUser
    @Test
    void edTask() throws Exception{
        Task task = new Task();
        task.setCompleted(false);
        task.setDescription("testing the tasks controller");
        User user = new User((long)1,"Laith","password","laithmosheer@gmail.com",22);
        task.setUser(user);
        String JsonTask = mapper.writeValueAsString(task);
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .put("/user/tasks/1")
                .accept(MediaType.APPLICATION_JSON).content(JsonTask)
                .contentType(MediaType.APPLICATION_JSON);
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response= mvcResult.getResponse();
        assertEquals(HttpStatus.OK.value(),response.getStatus());
    }

    @WithMockUser
    @Test
    void deleteTask() throws Exception{
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete("/user/tasks/1")
                .contentType(MediaType.APPLICATION_JSON);
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response= mvcResult.getResponse();
        assertEquals(HttpStatus.OK.value(),response.getStatus());
    }
}