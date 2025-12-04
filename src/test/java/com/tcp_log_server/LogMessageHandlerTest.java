package com.tcp_log_server;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class LogMessageHandlerTest {

    private LogMessageHandler handler;
    private ByteArrayOutputStream outputStreamCaptor;

    @BeforeEach
    void setUp() {
        handler = new LogMessageHandler();
        outputStreamCaptor = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStreamCaptor));
    }

    @Test
    void testHandleSimpleTextMessage() {
        // Given
        String testMessage = "Test log message from byte array";
        byte[] payload = testMessage.getBytes(StandardCharsets.UTF_8);
        Message<byte[]> message = MessageBuilder.withPayload(payload).build();

        // When
        handler.handleMessage(message);

        // Then
        String output = outputStreamCaptor.toString();
        assertTrue(output.contains(testMessage), "Output should contain the test message");
        assertTrue(output.matches(".*\\[\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}\\.\\d{3}\\].*"),
                   "Output should contain timestamp");
    }

    @Test
    void testHandleJsonMessage() {
        // Given
        String jsonMessage = "{\"level\":\"INFO\",\"message\":\"Payment processed\"}";
        byte[] payload = jsonMessage.getBytes(StandardCharsets.UTF_8);
        Message<byte[]> message = MessageBuilder.withPayload(payload).build();

        // When
        handler.handleMessage(message);

        // Then
        String output = outputStreamCaptor.toString();
        assertTrue(output.contains(jsonMessage), "Output should contain the JSON message");
    }

    @Test
    void testHandleMessageWithIpAddress() {
        // Given
        String testMessage = "Test message with IP";
        byte[] payload = testMessage.getBytes(StandardCharsets.UTF_8);
        Message<byte[]> message = MessageBuilder
                .withPayload(payload)
                .setHeader("ip_address", "192.168.1.100")
                .build();

        // When
        handler.handleMessage(message);

        // Then
        String output = outputStreamCaptor.toString();
        assertTrue(output.contains(testMessage), "Output should contain the test message");
    }

    @Test
    void testHandleMultilineMessage() {
        // Given
        String multilineMessage = "Line 1\nLine 2\nLine 3";
        byte[] payload = multilineMessage.getBytes(StandardCharsets.UTF_8);
        Message<byte[]> message = MessageBuilder.withPayload(payload).build();

        // When
        handler.handleMessage(message);

        // Then
        String output = outputStreamCaptor.toString();
        assertTrue(output.contains("Line 1"), "Output should contain Line 1");
        assertTrue(output.contains("Line 2"), "Output should contain Line 2");
        assertTrue(output.contains("Line 3"), "Output should contain Line 3");
    }

    @Test
    void testHandleUtf8Message() {
        // Given
        String utf8Message = "Mensaje con acentos: áéíóú ñ";
        byte[] payload = utf8Message.getBytes(StandardCharsets.UTF_8);
        Message<byte[]> message = MessageBuilder.withPayload(payload).build();

        // When
        handler.handleMessage(message);

        // Then
        String output = outputStreamCaptor.toString();
        assertTrue(output.contains(utf8Message), "Output should contain UTF-8 characters");
    }
}
