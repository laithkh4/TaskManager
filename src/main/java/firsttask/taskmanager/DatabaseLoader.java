package firsttask.taskmanager;

import firsttask.taskmanager.Repositories.RoleRepository;
import firsttask.taskmanager.Repositories.UserRepository;
import firsttask.taskmanager.domain.Role;
import firsttask.taskmanager.domain.User;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

@Component
public class DatabaseLoader implements CommandLineRunner {


    private UserRepository userRepository;
    private RoleRepository roleRepository;

    private Map<String, User> users = new HashMap<>();

    public DatabaseLoader( UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(String... args) {

        // add users and roles
        addUsersAndRoles();

    }

    private void addUsersAndRoles() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String secret = "{bcrypt}" + encoder.encode("password");

        Role userRole = new Role("ROLE_USER");
        roleRepository.save(userRole);
        Role adminRole = new Role("ROLE_ADMIN");
        roleRepository.save(adminRole);

        User user = new User("Laith",secret,"laithmosheer@gmail.com",22);//("user@gmail.com",secret,true,"Joe","User","joedirt");
        user.addRole(userRole);
        userRepository.save(user);
        users.put("user@gmail.com",user);

        User admin =new User("ahmad",secret,"ahmad@gmail.com",23);
        //admin.setAlias("joeadmin");
        admin.addRole(adminRole);
        userRepository.save(admin);
        users.put("admin@gmail.com",admin);

        User master =new User("mohammad",secret,"mohammad@gmail.com",24);
        master.addRoles(new HashSet<>(Arrays.asList(userRole,adminRole)));
        userRepository.save(master);
        users.put("super@gmail.com",master);
    }

}