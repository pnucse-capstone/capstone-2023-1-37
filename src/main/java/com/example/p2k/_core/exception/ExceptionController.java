package com.example.p2k._core.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class ExceptionController {

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<Object> handleValidationExceptions(final MethodArgumentNotValidException ex) {
        List<String> errors = new ArrayList<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String errorMessage = error.getDefaultMessage();
            errors.add(errorMessage);
        });
        return ResponseEntity.badRequest().body(errors.get(0));
    }

    // 400
    @ExceptionHandler({Exception400.class})
    public ResponseEntity<Object> handleException400(final Exception400 ex) {
        log.warn("error 400", ex);
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    //401
    @ExceptionHandler({ Exception401.class })
    public ResponseEntity<Object>  handleException401(final Exception401 ex) {
        log.warn("error 401", ex);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
    }

    //403
    @ExceptionHandler({ Exception403.class })
    public ResponseEntity<Object>  handleException403(final Exception403 ex) {
        log.warn("error 403", ex);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
    }

    //404
    @ExceptionHandler({ Exception404.class })
    public ResponseEntity<Object>  handleException404(final Exception404 ex) {
        log.warn("error 404", ex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    //500
    @ExceptionHandler({ Exception500.class })
    public ResponseEntity<Object>  handleException500(final Exception500 ex) {
        log.warn("error 500", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
    }

    @ExceptionHandler({ Exception.class })
    public ResponseEntity<Object> handleException(final Exception ex) {
        log.info(ex.getClass().getName());
        log.error("error", ex);
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
