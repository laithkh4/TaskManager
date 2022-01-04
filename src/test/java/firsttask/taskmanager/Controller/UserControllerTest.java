package firsttask.taskmanager.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import firsttask.taskmanager.Models.AuthenticationRequest;
import firsttask.taskmanager.Models.AuthenticationResponse;
import firsttask.taskmanager.Repositories.UserRepository;
import firsttask.taskmanager.Security.JWTSecurity.JwtUtil;
import firsttask.taskmanager.Security.UserDetailsServiceImpl;
import firsttask.taskmanager.Services.UserServices;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(UserController.class)
class UserControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper mapper;
    @MockBean
    UserServices userServices;
    @MockBean
    UserDetailsServiceImpl userDetailsService;
    @MockBean
    AuthenticationManager authenticationManager;
    @MockBean
    JwtUtil jwtUtil;
    @MockBean
    UserRepository userRepository;

    @Test
    void createNewUser() throws Exception {
        User user = new User((long)1,"Laith","password","laithmosheer@gmail.com",22);
        String JsonUser= mapper.writeValueAsString(user);
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                                                    .post("/register")
                                                    .accept(MediaType.APPLICATION_JSON).content(JsonUser)
                                                    .contentType(MediaType.APPLICATION_JSON);
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response= mvcResult.getResponse();
        assertEquals(HttpStatus.OK.value(),response.getStatus());
    }


    @Test
    void createAuthenticationToken() throws Exception {
        User user = new User((long)1,"Laith","password","laithmosheer@gmail.com",22);
        AuthenticationResponse resp= new AuthenticationResponse();
        resp.setJwt(jwtUtil.generateToken(user));
        String JsonToken= mapper.writeValueAsString(resp);
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/login")
                .accept(MediaType.APPLICATION_JSON).content(JsonToken)
                .contentType(MediaType.APPLICATION_JSON);
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response= mvcResult.getResponse();
        assertEquals(HttpStatus.OK.value(),response.getStatus());
    }


    @WithMockUser
    @Test
    void logOut() throws Exception{

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/user/logout")
                .contentType(MediaType.APPLICATION_JSON);
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response= mvcResult.getResponse();
        assertEquals(HttpStatus.OK.value(),response.getStatus());
    }

    @WithMockUser
    @Test
    void logOutAll() throws Exception{
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/user/logoutall")
                .contentType(MediaType.APPLICATION_JSON);
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response= mvcResult.getResponse();
        assertEquals(HttpStatus.OK.value(),response.getStatus());
    }

    @WithMockUser
    @Test
    void returnUser() throws Exception{
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/user")
                .contentType(MediaType.APPLICATION_JSON);
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response= mvcResult.getResponse();
        assertEquals(HttpStatus.OK.value(),response.getStatus());
    }

    @WithMockUser
    @Test
    void edUser() throws Exception{
        User user = new User((long)1,"Laith","password","Laith@gmail.com",22);
        String JsonUser= mapper.writeValueAsString(user);
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .put("/user")
                .accept(MediaType.APPLICATION_JSON).content(JsonUser)
                .contentType(MediaType.APPLICATION_JSON);
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response= mvcResult.getResponse();
        assertEquals(HttpStatus.OK.value(),response.getStatus());
    }

    @WithMockUser
    @Test
    void deleteUser() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete("/user")
                .contentType(MediaType.APPLICATION_JSON);
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response= mvcResult.getResponse();
        assertEquals(HttpStatus.OK.value(),response.getStatus());
    }
}