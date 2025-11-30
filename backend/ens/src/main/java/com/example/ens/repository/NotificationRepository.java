// NotificationRepository.java
package com.example.ens.repository;

import com.example.ens.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByStatus(String status); // "active" / "resolved"
}
