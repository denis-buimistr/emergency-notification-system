package com.example.ens.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 🔹 Основные поля
    private String message;
    private String type;
    private double latitude;
    private double longitude;
    private String status;
    private LocalDateTime timestamp;

    // 🔹 Связь с пользователем (многие уведомления — один пользователь)
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = true)
    @JsonIgnore
    private User user;

    // 🔹 Конструктор по умолчанию (нужен для JPA)
    public Notification() {}

    // 🔹 Конструктор с параметрами
    public Notification(String message, String type, double latitude, double longitude, User user) {
        this.message = message;
        this.type = type;
        this.latitude = latitude;
        this.longitude = longitude;
        this.user = user;
        this.status = "active";
        this.timestamp = LocalDateTime.now();
    }

    // 🔹 Getters и Setters
    public Long getId() { return id; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}
