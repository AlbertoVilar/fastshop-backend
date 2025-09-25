package com.fastshop.handlers;

import com.fastshop.exceptions.DatabaseException;
import com.fastshop.exceptions.ResourceNotFoundException;
import com.fastshop.exceptions.StandardError;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<StandardError> handleResourceNotFound(
            ResourceNotFoundException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        StandardError err = new StandardError();
        err.setTimestamp(LocalDateTime.now().toString());
        err.setStatus(status.value());
        err.setError("Recurso não encontrado");
        err.setMessage(ex.getMessage());
        err.setPath(request.getRequestURI());
        return ResponseEntity.status(status).body(err);
    }
    @ExceptionHandler(DatabaseException.class)
    public ResponseEntity<StandardError> handleDatabase(DatabaseException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST; // ou CONFLICT
        StandardError err = new StandardError();
        err.setTimestamp(LocalDateTime.now().toString());
        err.setStatus(status.value());
        err.setError("Erro de integridade de banco de dados");
        err.setMessage(ex.getMessage());
        err.setPath(request.getRequestURI());
        return ResponseEntity.status(status).body(err);
    }

}
