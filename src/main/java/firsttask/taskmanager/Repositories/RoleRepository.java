package firsttask.taskmanager.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import firsttask.taskmanager.domain.Role;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role,Long> {

    Optional<Role> findByName(String name);
    void deleteAllByName(String name);
}
