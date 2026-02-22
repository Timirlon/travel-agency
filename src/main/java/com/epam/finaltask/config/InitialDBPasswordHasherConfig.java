package com.epam.finaltask.config;

import com.epam.finaltask.repository.UserRepository;
import com.epam.finaltask.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class InitialDBPasswordHasherConfig implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public InitialDBPasswordHasherConfig(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        userRepository.findUserByUsername("admin").ifPresent(admin -> {
            String rawPassword = admin.getPassword();
            if (!rawPassword.startsWith("$2a$")) {
                admin.setPassword(passwordEncoder.encode(rawPassword));
                userRepository.save(admin);
                System.out.println("Admin password was encoded on startup.");
            }
        });
    }
}
