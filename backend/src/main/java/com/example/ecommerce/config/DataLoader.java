package com.example.ecommerce.config;

import com.example.ecommerce.entity.Category;
import com.example.ecommerce.repository.CategoryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner initDatabase(CategoryRepository categoryRepository) {
        return args -> {
            if (categoryRepository.count() == 0) {
                Category electronics = new Category();
                electronics.setName("Electronics");
                electronics.setDescription("Electronic gadgets and devices");
                categoryRepository.save(electronics);

                Category books = new Category();
                books.setName("Books");
                books.setDescription("Printed and digital books");
                categoryRepository.save(books);

                Category clothing = new Category();
                clothing.setName("Clothing");
                clothing.setDescription("Apparel and accessories");
                categoryRepository.save(clothing);

                System.out.println("Preloaded 3 categories");
            }
        };
    }
}
