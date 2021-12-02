package firsttask.taskmanager.Exceptions;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class PasswordNotCorrectAdvice {
    @ResponseBody
    @ExceptionHandler(PasswordNotCorrectException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String PasswordNotCorrectHandler(PasswordNotCorrectException ex) {
        return ex.getMessage();
    }
}
