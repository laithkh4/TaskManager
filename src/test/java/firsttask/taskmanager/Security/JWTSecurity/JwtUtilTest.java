package firsttask.taskmanager.Security.JWTSecurity;

import firsttask.taskmanager.Repositories.TokenRepository;
import firsttask.taskmanager.domain.User;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtUtilTest {

    @Mock
    TokenRepository tokenRepository;
    @Mock
    Claims claims;
    @InjectMocks
    JwtUtil jwtUtil;
    @Test
    void isTokenInDBSuccess() {
        User user = new User((long)1,"Laith","password","laithmosheer@gmail.com",22);
        when(tokenRepository.existsById(anyString())).thenReturn(true);
        assertEquals(true, jwtUtil.isTokenInDB(anyString(),user));
    }

    @Test
    void isTokenInDBFail() {
        User user = new User((long)1,"Laith","password","laithmosheer@gmail.com",22);
        when(tokenRepository.existsById(anyString())).thenReturn(false);
        assertEquals(false,jwtUtil.isTokenInDB(anyString(),user));
    }



    @Test
    void generateToken() {
        User user = new User((long)1,"Laith","password","laithmosheer@gmail.com",22);
        String Token = jwtUtil.generateToken(user);
        assertEquals(Token,jwtUtil.generateToken(user));
    }

    @Test
    void validateTokenSuccess() {
        User user = new User((long)1,"Laith","password","laithmosheer@gmail.com",22);
        String Token = jwtUtil.generateToken(user);
        when(tokenRepository.existsById(Token)).thenReturn(true);
        assertEquals(true,jwtUtil.validateToken(Token,user));
    }

    @Test
    void validateTokenFail() {
        User user = new User((long)1,"Laith","password","laithmosheer@gmail.com",22);
        String Token = jwtUtil.generateToken(user);
        when(tokenRepository.existsById(Token)).thenReturn(false);
        assertEquals(false,jwtUtil.validateToken(Token,user));
    }
}