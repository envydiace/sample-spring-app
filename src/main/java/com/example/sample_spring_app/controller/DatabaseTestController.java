package com.example.sample_spring_app.controller;

import com.example.sample_spring_app.dto.CreateTestItemRequest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/db")
public class DatabaseTestController {

    private final JdbcTemplate jdbcTemplate;

    public DatabaseTestController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Confirms that the application can connect to PostgreSQL.
     */
    @GetMapping("/health")
    public Map<String, Object> checkDatabaseConnection() {
        Map<String, Object> result = new LinkedHashMap<>();

        String databaseName = jdbcTemplate.queryForObject(
                "SELECT current_database()",
                String.class
        );

        String databaseUser = jdbcTemplate.queryForObject(
                "SELECT current_user",
                String.class
        );

        Integer version = jdbcTemplate.queryForObject(
                "SELECT current_setting('server_version_num')::integer",
                Integer.class
        );

        result.put("connected", true);
        result.put("databaseName", databaseName);
        result.put("databaseUser", databaseUser);
        result.put("serverVersionNumber", version);

        return result;
    }

    /**
     * Creates the test table if it does not already exist.
     */
    @PostMapping("/init")
    public Map<String, Object> initializeDatabase() {
        jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS test_items (
                    id BIGSERIAL PRIMARY KEY,
                    name VARCHAR(255) NOT NULL,
                    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
                )
                """);

        return Map.of(
                "initialized", true,
                "message", "test_items table is ready"
        );
    }

    /**
     * Inserts a record into the managed database.
     */
    @PostMapping("/items")
    public Map<String, Object> createItem(
            @RequestBody CreateTestItemRequest request
    ) {
        if (request.getName() == null || request.getName().isBlank()) {
            throw new IllegalArgumentException("Name is required");
        }

        jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS test_items (
                    id BIGSERIAL PRIMARY KEY,
                    name VARCHAR(255) NOT NULL,
                    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
                )
                """);

        Map<String, Object> item = jdbcTemplate.queryForMap(
                """
                INSERT INTO test_items (name)
                VALUES (?)
                RETURNING id, name, created_at
                """,
                request.getName().trim()
        );

        return item;
    }

    /**
     * Reads all records from the managed database.
     */
    @GetMapping("/items")
    public List<Map<String, Object>> getItems() {
        jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS test_items (
                    id BIGSERIAL PRIMARY KEY,
                    name VARCHAR(255) NOT NULL,
                    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
                )
                """);

        return jdbcTemplate.queryForList("""
                SELECT id, name, created_at
                FROM test_items
                ORDER BY id
                """);
    }

    /**
     * Deletes all test records.
     */
    @DeleteMapping("/items")
    public Map<String, Object> deleteAllItems() {
        int deletedRows = jdbcTemplate.update(
                "DELETE FROM test_items"
        );

        return Map.of(
                "deletedRows", deletedRows
        );
    }
}
