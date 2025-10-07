package com.fastshop.bootstrap;

import com.fastshop.repositories.UserRepository;
import com.fastshop.entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

@Component
public class PasswordResetRunner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(PasswordResetRunner.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public PasswordResetRunner(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        String resetFlag = System.getenv("RESET_ADMIN_PASSWORD");
        if (resetFlag == null || !"true".equalsIgnoreCase(resetFlag.trim())) {
            log.debug("PasswordResetRunner disabled (RESET_ADMIN_PASSWORD not true)");
            return;
        }

        String username = System.getenv("RESET_ADMIN_USERNAME");
        String plainPassword = System.getenv("RESET_ADMIN_PLAIN_PASSWORD");

        if (username == null || username.isBlank() || plainPassword == null || plainPassword.isBlank()) {
            log.warn("PasswordResetRunner missing env vars: RESET_ADMIN_USERNAME or RESET_ADMIN_PLAIN_PASSWORD");
            return;
        }

        Optional<User> optUser = userRepository.findByUsername(username);
        if (optUser.isEmpty()) {
            log.warn("PasswordResetRunner: user not found username={}", username);
            return;
        }

        User user = optUser.get();
        String newHash = passwordEncoder.encode(plainPassword);
        user.setPassword(newHash);
        userRepository.save(user);
        log.info("PasswordResetRunner: password reset for username={}", username);
    }
}