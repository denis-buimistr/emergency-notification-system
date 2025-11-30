package com.example.ens.service;

import com.example.ens.model.User;
import com.example.ens.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AlertMailService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    public void sendAlertToAll(String type, String message, double lat, double lng) {

        for (User user : userRepository.findAll()) {
            if (user.getEmail() == null) continue;

            String body = "Тип: " + type + "\n" +
                    "Сообщение: " + message + "\n" +
                    "Координаты: " + lat + ", " + lng + "\n" +
                    "Статус: active";

            emailService.send(
                    user.getEmail(),
                    "Новый алерт в ENS!",
                    body
            );
        }
    }
}
