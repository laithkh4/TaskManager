package firsttask.taskmanager.Exceptions;

public class UserAlreadyExistException extends RuntimeException{
    public UserAlreadyExistException() {
        super("This email is already registered please try with a different email!");
    }
}
