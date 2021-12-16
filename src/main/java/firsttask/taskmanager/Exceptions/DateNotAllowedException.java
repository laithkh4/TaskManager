package firsttask.taskmanager.Exceptions;

public class DateNotAllowedException extends RuntimeException{
    public DateNotAllowedException() {
        super("There another task between this time, Please check and try again!");
    }
}
