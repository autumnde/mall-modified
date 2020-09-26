package cn.zhang.mallmodified.exception;

import cn.zhang.mallmodified.common.api.ServerResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author autum
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = BindException.class)
    public ServerResponse<Map<String, String>> handleValidationExceptions(BindException ex) {
        log.info("处理异常 BindException");
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach((error) -> {
            String fieldName = error.getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ServerResponse.createByErrorMessage(errors.toString());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value=ConstraintViolationException.class)
    public ServerResponse handleConstraintViolationException(ConstraintViolationException ex){
        log.info("处理异常 ConstraintViolationException");
        return ServerResponse.createByErrorMessage(ex.getMessage());
    }
}