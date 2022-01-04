package firsttask.taskmanager.Services;

import firsttask.taskmanager.Exceptions.UserAlreadyExistException;
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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServicesTest {
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
    @Mock
    private AuthenticationManager authenticationManager;
    @InjectMocks
    private UserServices userServices;
    @Test
    void createNewUserSuccess() {
        User user = new User((long)1,"Laith","password","laithmosheer@gmail.com",22);
        when(userRepository.save(user)).thenReturn(user);
        assertEquals(user,userServices.createNewUser(user));
    }
    @Test
    void createNewUserFail() {
        User user = new User((long) 1, "Laith", "password", "laithmosheer@gmail.com", 22);
        when(userRepository.findByEmail(user.getUsername())).thenReturn(Optional.of(user));
       assertThrows(UserAlreadyExistException.class,()-> userServices.createNewUser(user));

/*        try {
            User user = new User((long) 1, "Laith", "password", "laithmosheer@gmail.com", 22);
            when(userRepository.findByEmail(user.getUsername())).thenReturn(Optional.of(user));
             userServices.createNewUser(user);
        }catch(UserAlreadyExistException ex){
            assertEquals(ex.getMessage(),"This email is already registered please try with a different email!");
        }*/
    }
    @Test
    void createAuthenticationTokenSuccess() {

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
        assertEquals(authenticationResponse.getJwt(),userServices.createAuthenticationToken(authenticationRequest).getJwt());
    }
    @Test
    void createAuthenticationTokenFail() {
        AuthenticationRequest authenticationRequest = new AuthenticationRequest("laithmosheer@gamil.com","password");
        when( authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(),authenticationRequest.getPassword()))).thenThrow(BadCredentialsException.class);
        assertThrows(BadCredentialsException.class,()->userServices.createAuthenticationToken(authenticationRequest));
    }
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
        assertEquals(userServices.editOneUser(user),user);
    }
    @Test()
    void deleteUser() throws IOException {
        User user = new User((long)12,"Laith","password","laithmosheer@gmail.com",22);
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(user);
        userServices.deleteUser();
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
        userServices.logOut(request);
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
        userServices.logOutAll();
        verify( tokenRepository,times(1)).deleteAllByUserId(user.getId());
    }

    @Test
    void returnUser() {
        User user = new User((long)12,"Laith","password","laithmosheer@gmail.com",22);
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(user);
        assertEquals(user,userServices.returnUser());
    }
}