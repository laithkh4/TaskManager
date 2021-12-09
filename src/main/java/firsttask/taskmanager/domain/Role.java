package firsttask.taskmanager.domain;


import lombok.*;

import javax.persistence.*;
import java.util.Collection;

@Entity
@NoArgsConstructor
@RequiredArgsConstructor
//@Getter
//@Setter
//@ToString
@EqualsAndHashCode
public
class Role {

    @Id
    @GeneratedValue
    private Long id;

    @NonNull
    @Column(nullable = false, unique = true)
    private String name;

    @ManyToMany( mappedBy = "roles")
    private Collection<User> users;

    @Override
    public String toString() {
        return "Role{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUsers(Collection<User> users) {
        this.users = users;
    }
    /*when the role class had to string or get users method the application always enter an infinite loop of calls where the tostring of rolls call the
    * tostring of users and then the tostring of users call tostring of role and it keep looping and same thing happen with the getters
    *
    * To solve this i had to exclude the getter of users in the roll class this way preventing the looping !*/
}