package firsttask.taskmanager.Repositories;

import firsttask.taskmanager.domain.Role;
import firsttask.taskmanager.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByEmail(String email);
    List<User> findAllByRolesContaining(Role role);
}
