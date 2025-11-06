package com.example.ens;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import com.example.ens.model.User;
import com.example.ens.model.Notification;
import com.example.ens.repository.UserRepository;
import com.example.ens.repository.NotificationRepository;
import org.springframework.boot.CommandLineRunner;

@SpringBootApplication
public class EnsApplication {

    public static void main(String[] args) {
        SpringApplication.run(EnsApplication.class, args);
    }

    // ✅ Добавляем тестовые данные при старте
    @Bean
    CommandLineRunner initData(UserRepository users, NotificationRepository notifications) {
        return args -> {
            User u1 = users.save(new User("Иван Иванов", "ivan@mail.com", "+79999999999", "admin"));
            notifications.save(new Notification("Тестовое уведомление", "Система ENS запущена", 55.7558, 37.6176, u1));
            System.out.println("✅ Тестовые данные добавлены!");
        };
    }
}
