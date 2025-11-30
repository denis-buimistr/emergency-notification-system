package com.example.ens;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.ens.model.User;
import com.example.ens.repository.UserRepository;


import org.springframework.boot.CommandLineRunner;
import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
public class EnsApplication {

    public static void main(String[] args) {

        // Загружаем переменные окружения
        Dotenv dotenv = Dotenv.load();
        System.setProperty("JWT_SECRET", dotenv.get("JWT_SECRET"));
        System.setProperty("GOOGLE_CLIENT_ID", dotenv.get("GOOGLE_CLIENT_ID"));
        System.setProperty("GOOGLE_CLIENT_SECRET", dotenv.get("GOOGLE_CLIENT_SECRET"));

        System.setProperty("MAIL_HOST", dotenv.get("MAIL_HOST"));
        System.setProperty("MAIL_PORT", dotenv.get("MAIL_PORT"));
        System.setProperty("MAIL_USERNAME", dotenv.get("MAIL_USERNAME"));
        System.setProperty("MAIL_PASSWORD", dotenv.get("MAIL_PASSWORD"));

        SpringApplication.run(EnsApplication.class, args);
    }

    /**
     * Создание администратора при первом запуске
     */
    @Bean
public CommandLineRunner initAdmin(UserRepository repo, PasswordEncoder encoder) {
    return args -> {

        User admin = repo.findByEmail("admin@ens.com")
                .orElseGet(() -> {
                    User u = new User();
                    u.setEmail("admin@ens.com");
                    u.setProvider("LOCAL");
                    u.setRole("ADMIN");
                    u.setFirstName("ENS");
                    u.setLastName("Administrator");
                    return u;
                });

        // НАСТРАИВАЕМ ПАРОЛЬ ВСЕГДА — даже если админ уже существовал
        admin.setPassword(encoder.encode("12345"));
        admin.setRole("ADMIN");
        admin.setProvider("LOCAL");

        repo.save(admin);

        System.out.println("✅ Админ синхронизирован: admin@ens.com / 12345");
    };
}

}
