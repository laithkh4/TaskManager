package firsttask.taskmanager.Controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(TaskController.class)
class TaskControllerTest {
    MockMvc mockMvc;
    @Test
    void returnAllTasks() {

    }

    @Test
    void returnTask() {
    }

    @Test
    void createTask() {
    }

    @Test
    void edTask() {
    }

    @Test
    void deleteTask() {
    }
}