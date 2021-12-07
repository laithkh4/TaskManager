package firsttask.taskmanager.domain;


import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;
import javax.validation.constraints.Size;

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

    @OneToMany(mappedBy = "user")
    private List<Task> Tasks = new ArrayList<>();


    @ManyToMany(fetch = FetchType.EAGER)// fetch all the roles at once
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id")

    )
    private Set<Role> roles = new HashSet<>();


    public void addTask(Task newTask) {
        Tasks.add(newTask);
    }

    public void addRole(Role role){
        roles.add(role);
    }
    public void addRoles(Set<Role> roles){
        roles.forEach(this::addRole);
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        /*List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        for (Role role : roles) {
            authorities.add(new SimpleGrantedAuthority(role.getName()));
        }
        return authorities;*/
        return roles.stream().map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList());
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
                "name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", age=" + age +
                ", Tasks=" + Tasks +
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

    public Set<Role> getRoles() {
        return roles;
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

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }
}
