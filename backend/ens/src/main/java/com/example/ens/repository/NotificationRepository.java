package com.example.ens.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.ens.model.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long> {}
