package com.example.ens.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import com.example.ens.model.Notification;
import com.example.ens.repository.NotificationRepository;
import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationRepository repository;

    @GetMapping
    public List<Notification> getAll() {
        return repository.findAll();
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Notification notification) {
        try {
            System.out.println("📩 Получено уведомление: " + notification.getMessage());

            Notification saved = repository.save(notification);

            System.out.println("✅ Уведомление сохранено с ID: " + saved.getId());
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            System.err.println("❌ Ошибка при создании уведомления: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body("Ошибка: " + e.getMessage());
        }
    }

    @PutMapping("/{id}/resolve")
    public Notification resolve(@PathVariable Long id) {
        Notification n = repository.findById(id).orElseThrow();
        n.setStatus("resolved");
        return repository.save(n);
    }
}
