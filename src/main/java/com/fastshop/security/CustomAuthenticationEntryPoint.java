package com.fastshop.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fastshop.exceptions.StandardError;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;
import java.time.LocalDateTime;

public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json");

        StandardError err = new StandardError();
        err.setTimestamp(LocalDateTime.now().toString());
        err.setStatus(HttpStatus.UNAUTHORIZED.value());
        err.setError("Credenciais inválidas");
        err.setMessage("Usuário ou senha incorretos");
        err.setPath(request.getRequestURI());

        response.getWriter().write(objectMapper.writeValueAsString(err));
    }
}