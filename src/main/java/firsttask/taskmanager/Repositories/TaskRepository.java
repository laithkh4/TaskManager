package firsttask.taskmanager.Repositories;

import firsttask.taskmanager.domain.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task,Long> {
    void deleteAllByUser_Id(Long id);
    Page<Task> findAllByUser_Id(Long id, Pageable pageable);
    List<Task> findAllByUser_Id(Long id);

}
