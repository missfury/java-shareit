package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ShareItApplicationTest {
    @Test
    void testMainMethod() {
        String[] args = new String[0];
        ShareItServer.main(args);

        assertNotNull(SpringApplication.getShutdownHandlers());
    }
}
