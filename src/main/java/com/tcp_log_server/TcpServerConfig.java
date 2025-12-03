package com.tcp_log_server;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.ip.tcp.TcpReceivingChannelAdapter;
import org.springframework.integration.ip.tcp.connection.AbstractServerConnectionFactory;
import org.springframework.integration.ip.tcp.connection.TcpNetServerConnectionFactory;
import org.springframework.integration.ip.tcp.serializer.ByteArrayCrLfSerializer;
import org.springframework.messaging.MessageChannel;

@Slf4j
@Configuration
public class TcpServerConfig {

    @Value("${tcp.server.port}")
    private int port;

    @Value("${tcp.server.backlog:100}")
    private int backlog;

    @Bean
    public AbstractServerConnectionFactory serverConnectionFactory() {
        TcpNetServerConnectionFactory connectionFactory = new TcpNetServerConnectionFactory(port);
        connectionFactory.setBacklog(backlog);

        // Configure deserializer for plain text messages with CRLF delimiters
        ByteArrayCrLfSerializer serializer = new ByteArrayCrLfSerializer();
        connectionFactory.setDeserializer(serializer);
        connectionFactory.setSerializer(serializer);

        // OPTION: Use this configuration for Java serialized objects (Logback SocketAppender)
        // Uncomment the lines below and comment the ByteArrayCrLfSerializer lines above
        // LogbackDeserializer deserializer = new LogbackDeserializer();
        // DefaultSerializer serializer = new DefaultSerializer();
        // connectionFactory.setDeserializer(deserializer);
        // connectionFactory.setSerializer(serializer);

        // Set single-use to false to reuse connections
        connectionFactory.setSingleUse(false);

        log.info("TCP Server configured on port {} for plain text messages", port);
        return connectionFactory;
    }

    @Bean
    public TcpReceivingChannelAdapter tcpReceivingChannelAdapter(
            AbstractServerConnectionFactory serverConnectionFactory,
            MessageChannel tcpInputChannel) {
        TcpReceivingChannelAdapter adapter = new TcpReceivingChannelAdapter();
        adapter.setConnectionFactory(serverConnectionFactory);
        adapter.setOutputChannel(tcpInputChannel);
        log.info("TCP Receiving Channel Adapter initialized");
        return adapter;
    }

    @Bean
    public MessageChannel tcpInputChannel() {
        return new DirectChannel();
    }

    @Bean
    @ServiceActivator(inputChannel = "tcpInputChannel")
    public LogMessageHandler logMessageHandler() {
        return new LogMessageHandler();
    }
}
