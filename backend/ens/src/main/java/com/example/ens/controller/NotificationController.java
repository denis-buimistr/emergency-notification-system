// NotificationController.java
package com.example.ens.controller;

import com.example.ens.model.Notification;
import com.example.ens.repository.NotificationRepository;
import com.example.ens.service.AlertMailService;
import com.example.ens.service.NotificationEvents;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationRepository repository;
    private final NotificationEvents events;
    private final AlertMailService alertMailService;

    public NotificationController(NotificationRepository repository,
                                  NotificationEvents events,
                                  AlertMailService alertMailService) {
        this.repository = repository;
        this.events = events;
        this.alertMailService = alertMailService;
    }

    @GetMapping
    public List<Notification> getAll() {
        return repository.findAll();
    }

    @GetMapping("/active")
    public List<Notification> getActive() {
        return repository.findByStatus("active");
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Notification notification) {

        if (notification.getStatus() == null || notification.getStatus().isBlank()) {
            notification.setStatus("active");
        }

        Notification saved = repository.save(notification);

        // отправка email всем пользователям в фоне (многопоточность)
        alertMailService.sendAlertToAll(
                notification.getType(),
                notification.getMessage(),
                notification.getLatitude(),
                notification.getLongitude()
        );

        // WebSocket
        events.broadcast("CREATED", saved);

        return ResponseEntity.ok(saved);
    }

    @PutMapping("/{id}/resolve")
    public ResponseEntity<?> resolve(@PathVariable Long id) {
        Notification n = repository.findById(id).orElseThrow();
        n.setStatus("resolved");
        Notification saved = repository.save(n);

        events.broadcast("RESOLVED", saved);

        return ResponseEntity.ok(saved);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        Notification n = repository.findById(id).orElseThrow();
        repository.delete(n);

        events.broadcast("DELETED", n);

        return ResponseEntity.noContent().build();
    }
}
