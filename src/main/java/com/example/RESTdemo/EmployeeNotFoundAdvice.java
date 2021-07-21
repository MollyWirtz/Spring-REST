package com.example.RESTdemo;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class EmployeeNotFoundAdvice {

    @ResponseBody       // Advice is rendered straight into response body
    @ExceptionHandler(EmployeeNotFoundException.class)      // Only respond if this exception is thrown
    @ResponseStatus(HttpStatus.NOT_FOUND)       // Set HTTP code to 404 not found
    String employeeNotFoundHandler(EmployeeNotFoundException ex) {
        return ex.getMessage();
    }

}
