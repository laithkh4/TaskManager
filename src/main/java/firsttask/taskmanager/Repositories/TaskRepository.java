package firsttask.taskmanager.Repositories;

import firsttask.taskmanager.domain.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task,Long> {
    void deleteAllByUser_Id(Long id);

}
