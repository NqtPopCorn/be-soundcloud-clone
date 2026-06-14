package com.popcorn.soundcloudclone.common.config;

import com.popcorn.soundcloudclone.features.users.entity.User;
import com.popcorn.soundcloudclone.features.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.findByUsername("super_admin").isEmpty()) {
            log.info("Creating default admin account...");
            User admin = User.builder()
                    .username("super_admin")
                    .email("super_admin@soundcloud.local")
                    .password(passwordEncoder.encode("super_admin123"))
                    .firstName("System")
                    .lastName("Admin")
                    .stageName("Administrator")
                    .role(User.Role.ADMIN)
                    .active(true)
                    .createdAt(LocalDateTime.now())
                    .build();
            userRepository.save(admin);
            log.info("Admin account created successfully. Username: super_admin, Password: super_admin123");
        } else {
            log.info("Admin account already exists. Skipping creation.");
        }
    }
}
