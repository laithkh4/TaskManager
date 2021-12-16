package firsttask.taskmanager.Repositories;

import firsttask.taskmanager.domain.Tokens;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;


public interface TokenRepository extends JpaRepository<Tokens,String> {
    @Transactional
    void deleteAllByUserId(Long userId);
}
