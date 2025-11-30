package com.example.ens.service;

import com.example.ens.model.Notification;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * Сервис для:
 *  1) Логирования событий в logs/user-events.json
 *  2) Широковещательной отправки событий в WebSocket для live-карты
 */
@Service
public class NotificationEvents {

    private static final String LOG_FILE = "logs/user-events.json";

    private final ObjectMapper mapper;
    private final SimpMessagingTemplate ws;

    public NotificationEvents(SimpMessagingTemplate ws) {
        this.ws = ws;

        this.mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

        File dir = new File("logs");
        if (!dir.exists()) dir.mkdirs();
    }

    /**
     * Логирование события в JSON-файл
     */
    public void log(String type, String email, Map<String, Object> details) {
        try (FileWriter writer = new FileWriter(LOG_FILE, true)) {

            Map<String, Object> entry = Map.of(
                    "timestamp", LocalDateTime.now().toString(),
                    "type", type,
                    "email", email,
                    "details", details
            );

            writer.write(mapper.writeValueAsString(entry) + "\n");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * WebSocket широковещательная рассылка о создании / изменении / удалении уведомлений
     */
    public void broadcast(String eventType, Notification notification) {
        Map<String, Object> payload = Map.of(
                "event", eventType,
                "notification", notification
        );

        ws.convertAndSend("/topic/notifications", payload);
    }
}
