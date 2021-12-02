package firsttask.taskmanager.Repositories;

import firsttask.taskmanager.domain.Task;
import firsttask.taskmanager.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Long> {
}
