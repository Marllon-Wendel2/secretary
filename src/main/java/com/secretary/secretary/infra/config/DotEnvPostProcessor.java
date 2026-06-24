package com.secretary.secretary.infra.config;

import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.util.HashMap;
import java.util.Map;

public class DotEnvPostProcessor implements EnvironmentPostProcessor {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        try {
            Path envPath = resolveEnvPath();
            if (envPath == null || !Files.exists(envPath)) {
                System.err.println("[.env] Arquivo .env não encontrado em: " + System.getProperty("user.dir"));
                return;
            }

            Map<String, Object> props = new HashMap<>();
            Files.readAllLines(envPath).stream()
                    .map(String::trim)
                    .filter(line -> !line.isEmpty() && !line.startsWith("#"))
                    .forEach(line -> {
                        int idx = line.indexOf('=');
                        if (idx > 0) {
                            String key = line.substring(0, idx).trim();
                            String value = line.substring(idx + 1).trim();
                            if (System.getenv(key) == null) {
                                props.put(key, value);
                            }
                        }
                    });

            if (!props.isEmpty()) {
                environment.getPropertySources().addFirst(new MapPropertySource("dotenv", props));
                System.out.println("[.env] Carregado de: " + envPath.toAbsolutePath() + " | Chaves: " + props.keySet());
            }
        } catch (Exception e) {
            System.err.println("[.env] Erro ao carregar .env: " + e.getMessage());
        }
    }

    private Path resolveEnvPath() {
        String userDir = System.getProperty("user.dir");
        Path[] candidates = {
            Path.of(userDir, ".env"),
            Path.of(userDir, "secretary", ".env"),
            Path.of(userDir, "..", ".env"),
        };
        for (Path candidate : candidates) {
            if (Files.exists(candidate)) {
                return candidate;
            }
        }
        return Path.of(userDir, ".env");
    }
}
