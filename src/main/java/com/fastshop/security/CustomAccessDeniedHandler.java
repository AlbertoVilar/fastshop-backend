package com.fastshop.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fastshop.exceptions.StandardError;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;
import java.time.LocalDateTime;

public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType("application/json");

        StandardError err = new StandardError();
        err.setTimestamp(LocalDateTime.now().toString());
        err.setStatus(HttpStatus.FORBIDDEN.value());
        err.setError("Acesso negado");
        err.setMessage("Você não possui permissão para acessar este recurso");
        err.setPath(request.getRequestURI());

        response.getWriter().write(objectMapper.writeValueAsString(err));
    }
}