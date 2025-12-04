package com.tcp_log_server;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

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
    void testHandleByteArrayMessage() {
        // Given
        String testMessage = "Test log message from byte array";
        byte[] payload = testMessage.getBytes();
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
    void testHandleStringMessage() {
        // Given
        String testMessage = "Test log message as String";
        Message<String> message = MessageBuilder.withPayload(testMessage).build();

        // When
        handler.handleMessage(message);

        // Then
        String output = outputStreamCaptor.toString();
        assertTrue(output.contains(testMessage), "Output should contain the test message");
        assertTrue(output.matches(".*\\[\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}\\.\\d{3}\\].*"),
                   "Output should contain timestamp");
    }

    @Test
    void testHandleUnexpectedPayloadType() {
        // Given
        Integer unexpectedPayload = 12345;
        Message<Integer> message = MessageBuilder.withPayload(unexpectedPayload).build();

        // When
        handler.handleMessage(message);

        // Then
        String output = outputStreamCaptor.toString();
        assertTrue(output.contains("12345"), "Output should contain the payload toString value");
    }

    @Test
    void testHandleJsonMessage() {
        // Given
        String jsonMessage = "{\"level\":\"INFO\",\"message\":\"Payment processed\"}";
        Message<String> message = MessageBuilder.withPayload(jsonMessage).build();

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
        Message<String> message = MessageBuilder
                .withPayload(testMessage)
                .setHeader("ip_address", "192.168.1.100")
                .build();

        // When
        handler.handleMessage(message);

        // Then
        String output = outputStreamCaptor.toString();
        assertTrue(output.contains(testMessage), "Output should contain the test message");
    }
}
