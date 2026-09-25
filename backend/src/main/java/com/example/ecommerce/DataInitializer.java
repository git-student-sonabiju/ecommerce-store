package com.example.ecommerce;

import com.example.ecommerce.entity.Role;
import com.example.ecommerce.entity.RoleName;
import com.example.ecommerce.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initRole(RoleRepository roleRepository) {
        return args -> {
            if (roleRepository.count() == 0) {
                Role userRole = Role.builder()
                        .name(RoleName.ROLE_USER)
                        .build();
                Role adminRole = Role.builder()
                        .name(RoleName.ROLE_ADMIN)
                        .build();

                roleRepository.save(userRole);
                roleRepository.save(adminRole);
            }
        };
    }
}