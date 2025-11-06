package com.example.ens.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.ens.model.User;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
