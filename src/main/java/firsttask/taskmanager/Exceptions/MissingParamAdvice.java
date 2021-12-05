package firsttask.taskmanager.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class MissingParamAdvice {
    @ResponseBody
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleMissingParams(MissingServletRequestParameterException ex) {
        String name = ex.getParameterName();
       return "Please Enter the "+name+", then try again!";
    }
}
