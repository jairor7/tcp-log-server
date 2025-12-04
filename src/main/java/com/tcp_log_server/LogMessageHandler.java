package com.tcp_log_server;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Component
public class LogMessageHandler {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    public void handleMessage(Message<?> message) {
        byte[] payload = (byte[]) message.getPayload();
        String timestamp = LocalDateTime.now().format(FORMATTER);

        // Convert bytes to string and print with timestamp
        String logMessage = new String(payload, java.nio.charset.StandardCharsets.UTF_8);
        System.out.println("[" + timestamp + "] " + logMessage);

        log.debug("Received log message from {}", message.getHeaders().get("ip_address"));
    }
}
