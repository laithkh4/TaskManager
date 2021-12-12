package firsttask.taskmanager.domain;


import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@NoArgsConstructor
@RequiredArgsConstructor
//@Getter
//@Setter
//@ToString
@EqualsAndHashCode
public class User implements UserDetails {// this interface used to hook the user class with the spring security

    @Id
    @GeneratedValue
    private Long id;

    @NonNull
    private String name;

    @NonNull
    @Column(nullable = false, length = 100)
    private String password;

    @NonNull
    @Size(min = 8, max = 50)
    @Column(nullable = false, unique = true)
    private String email;

    @NonNull
    private int age;

    @OneToMany(mappedBy = "user",fetch = FetchType.EAGER)
    private List<Task> Tasks = new ArrayList<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;//We removed the role authorities
    }

    //user detail methods
    @Override
    public String getUsername() {
        return email;
    }
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", age=" + age +
                '}';
    }
    public String getName() {
        return name;
    }
    @Override
    public String getPassword() {
        return password;
    }
    public String getEmail() {
        return email;
    }
    public int getAge() {
        return age;
    }
    public List<Task> getTasks() {
        return Tasks;
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public void setAge(int age) {
        this.age = age;
    }
    public void setTasks(List<Task> tasks) {
        Tasks = tasks;
    }
    public void addTask(Task task){
        Tasks.add(task);

    }
}
