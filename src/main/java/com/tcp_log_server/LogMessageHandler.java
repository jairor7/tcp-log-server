package com.tcp_log_server;

import ch.qos.logback.classic.spi.ILoggingEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Slf4j
@Component
public class LogMessageHandler {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    public void handleMessage(Message<?> message) {
        Object payload = message.getPayload();
        String timestamp = LocalDateTime.now().format(FORMATTER);

        if (payload instanceof ILoggingEvent) {
            // Handle Logback LoggingEvent objects
            ILoggingEvent event = (ILoggingEvent) payload;

            LocalDateTime eventTime = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(event.getTimeStamp()),
                ZoneId.systemDefault()
            );
            String eventTimestamp = eventTime.format(FORMATTER);

            String logMessage = String.format("[%s] [%s] %s - %s",
                eventTimestamp,
                event.getLevel(),
                event.getLoggerName(),
                event.getFormattedMessage()
            );

            System.out.println("[" + timestamp + "] " + logMessage);

        } else if (payload instanceof byte[]) {
            // Handle plain text byte arrays
            String logMessage = new String((byte[]) payload, java.nio.charset.StandardCharsets.UTF_8);
            System.out.println("[" + timestamp + "] " + logMessage);

        } else if (payload instanceof String) {
            // Handle plain text strings
            System.out.println("[" + timestamp + "] " + payload);

        } else {
            log.warn("Unexpected payload type: {}", payload.getClass().getName());
            System.out.println("[" + timestamp + "] " + payload.toString());
        }

        log.debug("Received log message from {}", message.getHeaders().get("ip_address"));
    }
}
