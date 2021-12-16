package firsttask.taskmanager.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

public class DateNotAllowedAdvice {
    @ResponseBody
    @ExceptionHandler(PasswordNotCorrectException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String DateNotAllowedHandler(DateNotAllowedException ex) {
        return ex.getMessage();
    }
}
