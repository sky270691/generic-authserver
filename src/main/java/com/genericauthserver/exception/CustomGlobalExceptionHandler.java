package com.genericauthserver.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CustomGlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private final String DATE_TIME_FORMAT = "yyyy-MM-dd HH-mm-ss";

    public DateTimeFormatter getDtf() {
        return DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatus status,
                                                                  WebRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", status.value());
        body.put("exception","payload exception");
        body.put("timestamp", LocalDateTime.now().format(getDtf()));
        List<String> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(x->x.getDefaultMessage())
                .collect(Collectors.toList());
        body.put("errors",errors);
        return new ResponseEntity<>(body,headers,status);
    }


    // ==================================custom error handler=================================================

    @ExceptionHandler(UserException.class)
    public ResponseEntity<UserExceptionResponse> handleTestException (UserException e){
        UserExceptionResponse response = new UserExceptionResponse();
        response.setMessage(e.getMessage());
        response.setStatus(HttpStatus.BAD_REQUEST.getReasonPhrase());
        response.setTimestamp(LocalDateTime.now());
        return new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
    }
}
