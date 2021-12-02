package firsttask.taskmanager.Exceptions;

public class PasswordNotCorrectException extends RuntimeException  {

    public PasswordNotCorrectException() {
        super("Password not correct please try again with a valid password!");
    }
}
