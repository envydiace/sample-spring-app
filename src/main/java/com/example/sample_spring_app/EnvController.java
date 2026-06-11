package com.example.sample_spring_app;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
public class EnvController {

    @GetMapping("/env")
    public Map<String, String> getEnv() {
        Map<String, String> env = new LinkedHashMap<>();

        env.put("APP_MODE", System.getenv("APP_MODE"));
        env.put("SPRING_DATASOURCE_URL", System.getenv("SPRING_DATASOURCE_URL"));
        env.put("SPRING_DATASOURCE_USERNAME", System.getenv("SPRING_DATASOURCE_USERNAME"));

        String password = System.getenv("SPRING_DATASOURCE_PASSWORD");
        env.put("SPRING_DATASOURCE_PASSWORD", password == null ? null : "********");

        return env;
    }
}
