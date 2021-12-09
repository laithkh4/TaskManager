package firsttask.taskmanager.Controller;


import firsttask.taskmanager.Repositories.RoleRepository;
import firsttask.taskmanager.Repositories.UserRepository;
import firsttask.taskmanager.domain.Role;
import firsttask.taskmanager.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;


import javax.management.relation.RoleNotFoundException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@RestController

public class RoleController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private RoleRepository roleRepository;
    private UserRepository userRepository;

    public RoleController(RoleRepository roleRepository, UserRepository userRepository) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/users/roles")
    List<Role> getAllRoles(){
        logger.info("Retrieving all the roles in the database request initialized!");
        return roleRepository.findAll();
    }
    @PostMapping("/users/roles")
    Role addNewRole(@RequestBody Role newRole){
        logger.info("Creating new role request initialized!");
          roleRepository.save(newRole);
          logger.trace("Creating new role "+newRole.getName());
          return newRole;
        }

    @PutMapping("/users/roles/{role}")
    Role editRole(@RequestBody Role editRole,@PathVariable String role ) throws RoleNotFoundException {
        logger.info("Editing Role request initialized !");
        Role updatedRole = roleRepository.findByName(role).orElseThrow(()->new RoleNotFoundException());
        updatedRole.setName(editRole.getName());
        return updatedRole;
    }
    @DeleteMapping("users/roles/{role}")
    void deleteRole(@PathVariable String role, HttpServletResponse response) throws IOException, RoleNotFoundException {
        Role DeletedRole = roleRepository.findByName(role).orElseThrow(()->new RoleNotFoundException());
        List<User> users = userRepository.findAllByRolesContaining(DeletedRole);
        for(User user:users){
            user.deleteRole("role");
        }
        roleRepository.deleteAllByName(role);

        response.sendRedirect("");
    }


}
