package firsttask.taskmanager.Logic;

import firsttask.taskmanager.Models.AuthenticationRequest;
import firsttask.taskmanager.Models.AuthenticationResponse;
import firsttask.taskmanager.Repositories.TaskRepository;
import firsttask.taskmanager.Repositories.TokenRepository;
import firsttask.taskmanager.Repositories.UserRepository;
import firsttask.taskmanager.Security.JWTSecurity.JwtUtil;
import firsttask.taskmanager.Security.UserDetailsServiceImpl;
import firsttask.taskmanager.domain.Tokens;
import firsttask.taskmanager.domain.User;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerLogicTest {
    @Mock
    private  UserDetailsServiceImpl userDetailsService;
    @Mock
    private  JwtUtil jwtTokenUtil;
    @Mock
    private  TaskRepository taskRepository;
    @Mock
    private  UserRepository userRepository;
    @Mock
    private  TokenRepository tokenRepository;
    @InjectMocks
    private UserControllerLogic userControllerLogic;
    @Test
    void createNewUser() {
        User user = new User((long)1,"Laith","password","laithmosheer@gmail.com",22);
        when(userRepository.save(user)).thenReturn(user);
        assertEquals(userControllerLogic.createNewUser(user),user);
    }

    @Test
    void createAuthenticationToken() {
        AuthenticationRequest authenticationRequest = new AuthenticationRequest("laithmosheer@gamil.com","password");
        User user = new User((long)1,"Laith","password","laithmosheer@gmail.com",22);
        when(userDetailsService.loadUserByUsername(authenticationRequest.getUsername())).thenReturn(user);
        String tok= jwtTokenUtil.generateToken(user);
        when(jwtTokenUtil.generateToken(any())).thenReturn(tok);
        Tokens tokens= new Tokens();
        tokens.setJwtToken(tok);
        tokens.setUser(user);
        user.addToken(tokens);
        when(tokenRepository.save(tokens)).thenReturn(tokens);
        when(userRepository.save(user)).thenReturn(user);
        AuthenticationResponse authenticationResponse= new AuthenticationResponse();
        authenticationResponse.setJwt(tok);
        assertEquals(userControllerLogic.createAuthenticationToken(authenticationRequest).getJwt(),authenticationResponse.getJwt());
    }

    @WithMockUser
    @Test
    void editOneUser() {
        User user = new User((long)12,"Laith","password","laithmosheer@gmail.com",22);
        /*Because when we are testing we didn't create a context, so we have to mock the context our self */
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(user);
        when(   userRepository.save(user)).thenReturn(user);
        assertEquals(userControllerLogic.editOneUser(user),user);
    }

    @Test()
    void deleteUser() throws IOException {
        User user = new User((long)12,"Laith","password","laithmosheer@gmail.com",22);
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(user);
        when(userRepository.existsById(anyLong())).thenReturn(true);
        userControllerLogic.deleteUser();
        verify( tokenRepository,times(1)).deleteAllByUserId(user.getId());
        verify( taskRepository,times(1)).deleteAllByUser_Id(user.getId());
        verify( userRepository,times(1)).deleteById(user.getId());

    }





  @Test
    void logOut() throws Exception {
      HttpServletRequest  request = Mockito.mock(HttpServletRequest.class);

      when(request.getHeader("Authorization")).thenReturn("Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJMYWl0aEBnbWFpbC5jb20iLCJleHAiOjE2NDAxOTg4MDcsImlhdCI6MTY0MDE2MjgwN30.FuPicrgmhbyp5kdoR1ls0q0eiUv1Py-fsFSC0jIhMs8");
        final String authorizationHeader = request.getHeader("Authorization");
        String jwt = authorizationHeader.substring(7);
        userControllerLogic.logOut(request);
        verify( tokenRepository,times(1)).deleteById(jwt);
    }

    @Test
    void logOutAll() {
        User user = new User((long)12,"Laith","password","laithmosheer@gmail.com",22);
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(user);
        userControllerLogic.logOutAll();
        verify( tokenRepository,times(1)).deleteAllByUserId(user.getId());
    }
}