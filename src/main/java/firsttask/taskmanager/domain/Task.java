package firsttask.taskmanager.domain;


import com.sun.istack.NotNull;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.Objects;

@Entity
@NoArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class Task {
    @Id
    @GeneratedValue
    private Long id;

    @NonNull
    private String description;
    @NonNull
    private boolean completed;
    @ManyToOne
    private User user;


}
