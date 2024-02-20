package org.example.interceptor;

import jakarta.persistence.NoResultException;
import org.example.exception.DataNotFoundException;
import org.example.response.HttpResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class Interceptor {

    @ExceptionHandler({
            NoResultException.class
    })
    public ResponseEntity<HttpResponse> handleException(NoResultException ex){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        HttpResponse.sendErrorResponse(ex.getMessage(), true)
                );
    }
}
