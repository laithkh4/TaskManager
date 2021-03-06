package firsttask.taskmanager.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
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
/*@RequiredArgsConstructor*/
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
    private String password;
  //  @Column(nullable = false, length = 100)


    @NonNull
    @Size(min = 8, max = 50)
    @Column(nullable = false, unique = true)
    private String email;

    @NonNull
    private int age;

    @OneToMany(mappedBy = "user",fetch = FetchType.EAGER)
    private List<Task> Tasks = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Tokens> Tokens = new ArrayList<>();


    public User(Long id, @NonNull String name, @NonNull String password, @NonNull String email, @NonNull int age) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.email = email;
        this.age = age;
    }

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
   @JsonIgnore// To prevent the password from being  retrieved with the request(more secure)
    @JsonProperty(value = "user_password")
    // for now this two annotations make the json request in the testing making the value of the password equal to null
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
    public void addToken(Tokens token){
        Tokens.add(token);

    }
}
