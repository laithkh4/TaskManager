package firsttask.taskmanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;



@SpringBootApplication
public class TaskManagerApplication {

    public static void main(String[] args) {

        System.out.println("This app is running ");
        SpringApplication.run(TaskManagerApplication.class, args);
    }

}
