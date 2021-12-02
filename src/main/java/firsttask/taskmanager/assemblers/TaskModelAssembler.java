package firsttask.taskmanager.assemblers;


import firsttask.taskmanager.Controller.TaskManagerController;
import firsttask.taskmanager.domain.Task;
import firsttask.taskmanager.domain.User;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class TaskModelAssembler implements RepresentationModelAssembler<Task, EntityModel<Task>> {

    @Override
    public EntityModel<Task> toModel(Task task) {

        return  EntityModel.of(task,  //
                linkTo(methodOn(TaskManagerController.class).returnTask(task.getId())).withSelfRel(),
                linkTo(methodOn(TaskManagerController.class).returnAllTasks()).withRel("Tasks"));
    }
}