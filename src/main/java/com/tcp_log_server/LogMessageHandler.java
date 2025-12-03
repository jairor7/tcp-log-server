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

        // Handle plain text messages (byte arrays or strings)
        if (payload instanceof byte[]) {
            String logMessage = new String((byte[]) payload, java.nio.charset.StandardCharsets.UTF_8);
            System.out.println("[" + timestamp + "] " + logMessage);

        } else if (payload instanceof String) {
            System.out.println("[" + timestamp + "] " + payload);

        } else {
            log.warn("Unexpected payload type: {}", payload.getClass().getName());
            System.out.println("[" + timestamp + "] " + payload.toString());
        }

        // OPTION: Handle Logback LoggingEvent objects (Java serialized objects)
        // Uncomment the code below if you want to handle Java serialized Logback events
        // if (payload instanceof ILoggingEvent) {
        //     ILoggingEvent event = (ILoggingEvent) payload;
        //
        //     LocalDateTime eventTime = LocalDateTime.ofInstant(
        //         Instant.ofEpochMilli(event.getTimeStamp()),
        //         ZoneId.systemDefault()
        //     );
        //     String eventTimestamp = eventTime.format(FORMATTER);
        //
        //     String logMessage = String.format("[%s] [%s] %s - %s",
        //         eventTimestamp,
        //         event.getLevel(),
        //         event.getLoggerName(),
        //         event.getFormattedMessage()
        //     );
        //
        //     System.out.println("[" + timestamp + "] " + logMessage);
        // }

        log.debug("Received log message from {}", message.getHeaders().get("ip_address"));
    }
}
