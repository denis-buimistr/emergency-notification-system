package com.example.ens.controller;

import com.example.ens.model.User;
import com.example.ens.repository.UserRepository;
import com.example.ens.security.AuthUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api/avatar")
public class AvatarController {

    private final UserRepository userRepository;

    public AvatarController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping("/upload")
    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file) throws IOException {

        User user = AuthUtil.getUser(userRepository);

        // создаем папку если ее нет
        File folder = new File("uploads");
        if (!folder.exists()) folder.mkdirs();

        // формируем имя файла
        String filename = UUID.randomUUID() + "-" + file.getOriginalFilename();
        File dest = new File(folder, filename);

        file.transferTo(dest);

        // сохраняем URL в базу
        String avatarUrl = "/uploads/" + filename;
        user.setAvatarUrl(avatarUrl);
        userRepository.save(user);

        return ResponseEntity.ok(avatarUrl);
    }
}
