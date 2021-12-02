package firsttask.taskmanager.domain;


import com.sun.istack.NotNull;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


@Entity
@NoArgsConstructor
@RequiredArgsConstructor
@Getter@Setter
@ToString
@EqualsAndHashCode
public class User {
    @Id
    @GeneratedValue
    private Long id;
    @NonNull
    private String name;
    @NonNull
    private String password;
    @NonNull
    private String email;
    @NonNull
    private int age;

    @OneToMany(mappedBy = "user")
    private List<Task> Tasks=new ArrayList<>();

    public void addTask(Task newTask){
        Tasks.add(newTask);
    }


}
