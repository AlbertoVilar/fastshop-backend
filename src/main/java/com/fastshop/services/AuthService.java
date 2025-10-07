package com.fastshop.services;

import com.fastshop.dto.AuthRequestDTO;
import com.fastshop.dto.AuthResponseDTO;
import com.fastshop.repositories.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.fastshop.entities.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    public AuthService(AuthenticationManager authenticationManager, TokenService tokenService, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public AuthResponseDTO authenticate(AuthRequestDTO request) {
        // Diagnóstico: verificar se senha em texto coincide com hash armazenado
        userRepository.findByUsername(request.getUsername()).ifPresentOrElse(user -> {
            boolean matches = passwordEncoder.matches(request.getPassword(), user.getPassword());
            logger.info("Auth diagnostic: username={}, passwordMatches={}", request.getUsername(), matches);
        }, () -> logger.warn("Auth diagnostic: username={} not found before authentication", request.getUsername()));

        // 1. Autenticar credenciais
        var authenticationToken = new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword());
        authenticationManager.authenticate(authenticationToken); // Lança exceção se inválidas

        // 2. Buscar o User completo no repositório
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado após autenticação: " + request.getUsername()));

        // 3. Gerar o token e retornar resposta
        return tokenService.generateToken(user);
    }
}
