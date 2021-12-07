package firsttask.taskmanager.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import firsttask.taskmanager.domain.Role;
public interface RoleRepository extends JpaRepository<Role,Long> {
}
