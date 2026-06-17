package com.popcorn.soundcloudclone.common.config;

import com.popcorn.soundcloudclone.features.users.entity.User;
import com.popcorn.soundcloudclone.features.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${app.admin.username:super_admin}")
    private String adminUsername;

    @Value("${app.admin.email:super_admin@soundcloud.local}")
    private String adminEmail;

    @Value("${app.admin.password:super_admin123}")
    private String adminPassword;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.findByUsername(adminUsername).isEmpty()) {
            log.info("Creating default admin account...");
            User admin = User.builder()
                    .username(adminUsername)
                    .email(adminEmail)
                    .password(passwordEncoder.encode(adminPassword))
                    .firstName("System")
                    .lastName("Admin")
                    .stageName("Administrator")
                    .role(User.Role.ADMIN)
                    .active(true)
                    .createdAt(LocalDateTime.now())
                    .build();
            userRepository.save(admin);
            log.info("Admin account created successfully. Username: {}, Password: {}", adminUsername, adminPassword);
        } else {
            log.info("Admin account already exists. Skipping creation.");
        }
    }
}
