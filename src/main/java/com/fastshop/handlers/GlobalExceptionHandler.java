package com.fastshop.handlers;

import com.fastshop.exceptions.DatabaseException;
import com.fastshop.exceptions.ResourceNotFoundException;
import com.fastshop.exceptions.StandardError;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.OffsetDateTime;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.dao.DataIntegrityViolationException;
import java.util.List;
import java.util.stream.Collectors;
import com.fastshop.exceptions.FieldMessage;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<StandardError> handleResourceNotFound(
            ResourceNotFoundException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        StandardError err = new StandardError();
        err.setTimestamp(OffsetDateTime.now().toString());
        err.setStatus(status.value());
        err.setError("Recurso não encontrado");
        err.setMessage(ex.getMessage());
        err.setPath(request.getRequestURI());
        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<StandardError> methodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
        StandardError err = new StandardError();
        err.setTimestamp(OffsetDateTime.now().toString());
        err.setStatus(status.value());
        err.setError("Recursos inválidos");
        err.setMessage("Erro de validação nos campos");
        err.setPath(request.getRequestURI());
        // Monta lista de erros de campo
        List<FieldMessage> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> new FieldMessage(fieldError.getField(), fieldError.getDefaultMessage()))
                .collect(Collectors.toList());
        err.setErrors(errors); // Adiciona os erros de campo ao StandardError
        return ResponseEntity.status(status).body(err);
    }
    
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<StandardError> handleDataIntegrityViolation(DataIntegrityViolationException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST; // Could be CONFLICT depending on semantics
        StandardError err = new StandardError();
        err.setTimestamp(OffsetDateTime.now().toString());
        err.setStatus(status.value());
        err.setError("Violação de integridade de dados");
        String message = ex.getMostSpecificCause() != null ? ex.getMostSpecificCause().getMessage() : ex.getMessage();
        err.setMessage(message);
        err.setPath(request.getRequestURI());
        return ResponseEntity.status(status).body(err);
    }
    @ExceptionHandler(DatabaseException.class)
    public ResponseEntity<StandardError> handleDatabase(DatabaseException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST; // ou CONFLICT
        StandardError err = new StandardError();
        err.setTimestamp(OffsetDateTime.now().toString());
        err.setStatus(status.value());
        err.setError("Erro de integridade de banco de dados");
        err.setMessage(ex.getMessage());
        err.setPath(request.getRequestURI());
        return ResponseEntity.status(status).body(err);
    }
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<StandardError> handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        StandardError err = new StandardError();
        err.setTimestamp(OffsetDateTime.now().toString());
        err.setStatus(status.value());
        err.setError("Requisição inválida");
        err.setMessage(ex.getMessage());
        err.setPath(request.getRequestURI());
        return ResponseEntity.status(status).body(err);
    }
    @ExceptionHandler(org.springframework.http.converter.HttpMessageNotReadableException.class)
    public ResponseEntity<StandardError> handleHttpMessageNotReadable(org.springframework.http.converter.HttpMessageNotReadableException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        StandardError err = new StandardError();
        err.setTimestamp(OffsetDateTime.now().toString());
        err.setStatus(status.value());
        err.setError("Erro de leitura do corpo da requisição");
        err.setMessage("JSON malformado ou campos inválidos: " + ex.getMostSpecificCause().getMessage());
        err.setPath(request.getRequestURI());
        return ResponseEntity.status(status).body(err);
    }

    // Credenciais inválidas na autenticação
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<StandardError> handleBadCredentials(BadCredentialsException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        StandardError err = new StandardError();
        err.setTimestamp(OffsetDateTime.now().toString());
        err.setStatus(status.value());
        err.setError("Credenciais inválidas");
        err.setMessage("Usuário ou senha incorretos");
        err.setPath(request.getRequestURI());
        return ResponseEntity.status(status).body(err);
    }

    // Fallback para outras AuthenticationException
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<StandardError> handleAuthentication(AuthenticationException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        StandardError err = new StandardError();
        err.setTimestamp(OffsetDateTime.now().toString());
        err.setStatus(status.value());
        err.setError("Falha na autenticação");
        err.setMessage("Usuário ou senha incorretos");
        err.setPath(request.getRequestURI());
        return ResponseEntity.status(status).body(err);
    }

}
