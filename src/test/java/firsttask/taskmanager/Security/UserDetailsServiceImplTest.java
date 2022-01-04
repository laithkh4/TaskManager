package firsttask.taskmanager.Security;

import firsttask.taskmanager.Repositories.UserRepository;
import firsttask.taskmanager.domain.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {
    @Mock
    UserRepository userRepository;
    @InjectMocks
    UserDetailsServiceImpl userDetailsService;
    @Test
    void loadUserByUsernameSuccess() {
        User user = new User((long)12,"Laith","password","laithmosheer@gmail.com",22);
        when(userRepository.findByEmail(user.getUsername())).thenReturn(Optional.of(user));
        when(userRepository.findByEmail(user.getUsername())).thenReturn(Optional.of(user));
        assertEquals(userDetailsService.loadUserByUsername(user.getUsername()), user);
    }
    @Test
    void loadUserByUsernameFail() {

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class,()->userDetailsService.loadUserByUsername(anyString()));
    }
}