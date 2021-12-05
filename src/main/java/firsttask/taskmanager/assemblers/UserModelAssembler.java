package firsttask.taskmanager.assemblers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import firsttask.taskmanager.Controller.UserController;
import firsttask.taskmanager.domain.User;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

@Component
public class UserModelAssembler implements RepresentationModelAssembler<User, EntityModel<User>> {

    @Override
    public EntityModel<User> toModel(User user) {

        return  EntityModel.of(user,//
                linkTo(methodOn(UserController.class).returnUser(user.getId())).withSelfRel(),
                linkTo(methodOn(UserController.class).returnAllUsers()).withRel("Users"));

    }
}