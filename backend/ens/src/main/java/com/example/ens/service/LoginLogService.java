package com.example.ens.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class LoginLogService {

    // можно так оставить, файл будет лежать в корне модуля backend/ens
    // если хочешь в папку logs, сделай: private static final String LOG_FILE = "logs/login-log.json";
    private static final String LOG_FILE = "login-log.json";

    private final ObjectMapper mapper = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT);

     public void write(String email, String method) {

        Map<String, Object> entry = new HashMap<>();
        entry.put("email", email);
        entry.put("method", method);
        entry.put("time", LocalDateTime.now().toString());

        try {
            File file = new File(LOG_FILE);

            ArrayNode arrayNode;

            if (!file.exists() || file.length() == 0) {
                arrayNode = mapper.createArrayNode();
            } else {
                try {
                    var root = mapper.readTree(file);
                    if (root.isArray()) {
                        arrayNode = (ArrayNode) root;
                    } else {
                        arrayNode = mapper.createArrayNode(); // если не массив — создаём новый
                    }
                } catch (Exception e) {
                    arrayNode = mapper.createArrayNode(); // если битый JSON — создаём новый
                }
            }

            arrayNode.add(mapper.valueToTree(entry));
            mapper.writeValue(file, arrayNode);

        } catch (Exception e) {
            System.err.println("Ошибка записи логина: " + e.getMessage());
        }
    }
}


