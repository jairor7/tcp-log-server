package com.tcp_log_server;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.serializer.Deserializer;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

@Slf4j
public class LogbackDeserializer implements Deserializer<Object> {

    private volatile ObjectInputStream objectInputStream;

    @Override
    public Object deserialize(InputStream inputStream) throws IOException {
        try {
            if (objectInputStream == null) {
                objectInputStream = new ObjectInputStream(inputStream);
            }
            return objectInputStream.readObject();
        } catch (EOFException e) {
            // Client closed connection cleanly - this is normal, not an error
            log.debug("Connection closed by client (EOF reached)");
            objectInputStream = null; // Reset for next connection
            return null; // Signal end of stream
        } catch (ClassNotFoundException e) {
            throw new IOException("Failed to deserialize object", e);
        } catch (IOException e) {
            // If we get a stream corruption, try to recreate the ObjectInputStream
            if (e.getMessage() != null && e.getMessage().contains("invalid stream header")) {
                log.debug("Stream header error, recreating ObjectInputStream");
                try {
                    objectInputStream = new ObjectInputStream(inputStream);
                    return objectInputStream.readObject();
                } catch (ClassNotFoundException ex) {
                    throw new IOException("Failed to deserialize object after reset", ex);
                } catch (EOFException ex) {
                    log.debug("Connection closed during stream reset");
                    objectInputStream = null;
                    return null;
                }
            }
            // Log other IO exceptions at debug level
            log.debug("IOException during deserialization: {}", e.getMessage());
            objectInputStream = null; // Reset for next connection
            throw e;
        }
    }
}
