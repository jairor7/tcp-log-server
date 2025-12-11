# Opciones de Serialización TCP

Este documento explica las diferentes estrategias para recibir mensajes TCP en el servidor.

## Configuración Actual

El servidor está configurado para recibir **objetos serializados de Logback** usando `LogbackDeserializer`.

## Opciones Disponibles

### 1. ByteArrayLengthHeaderSerializer (Sin delimitador - Por tamaño)

**Uso:** Cuando quieres enviar mensajes **sin delimitador CRLF**, basándote en la longitud del mensaje.

**Cómo funciona:**
- El cliente envía primero 4 bytes (int) indicando la longitud del mensaje
- Luego envía exactamente esa cantidad de bytes
- El servidor lee primero los 4 bytes, luego lee exactamente ese número de bytes

**Configuración en TcpServerConfig:**

```java
import org.springframework.integration.ip.tcp.serializer.ByteArrayLengthHeaderSerializer;

@Bean
public AbstractServerConnectionFactory serverConnectionFactory() {
    TcpNetServerConnectionFactory connectionFactory = new TcpNetServerConnectionFactory(port);
    connectionFactory.setBacklog(backlog);

    // Usar header de longitud (4 bytes) + datos
    ByteArrayLengthHeaderSerializer serializer = new ByteArrayLengthHeaderSerializer();
    serializer.setMaxMessageSize(maxMessageSize); // Máximo tamaño de mensaje
    connectionFactory.setDeserializer(serializer);
    connectionFactory.setSerializer(serializer);

    connectionFactory.setSingleUse(false);
    log.info("TCP Server configured for length-header messages");
    return connectionFactory;
}
```

**Ejemplo de cliente (Java):**

```java
Socket socket = new Socket("localhost", 9999);
OutputStream out = socket.getOutputStream();

String message = "Este es mi mensaje";
byte[] messageBytes = message.getBytes(StandardCharsets.UTF_8);

// Enviar primero la longitud (4 bytes - int)
ByteBuffer buffer = ByteBuffer.allocate(4 + messageBytes.length);
buffer.putInt(messageBytes.length);
buffer.put(messageBytes);

out.write(buffer.array());
out.flush();
```

**Ventajas:**
- ✅ No necesita delimitador CRLF
- ✅ Soporta cualquier contenido binario
- ✅ Conoce el tamaño exacto a leer (eficiente)
- ✅ Ideal para mensajes de tamaño variable

**Desventajas:**
- ❌ El cliente debe enviar el header de longitud
- ❌ No compatible con clientes que no implementen este protocolo

---

### 2. ByteArrayCrLfSerializer (Delimitador CRLF)

**Uso:** Para mensajes de texto terminados con `\r\n`

**Configuración:**

```java
import org.springframework.integration.ip.tcp.serializer.ByteArrayCrLfSerializer;

ByteArrayCrLfSerializer serializer = new ByteArrayCrLfSerializer();
serializer.setMaxMessageSize(65536); // 64KB
connectionFactory.setDeserializer(serializer);
connectionFactory.setSerializer(serializer);
```

**Ventajas:**
- ✅ Simple de implementar en clientes
- ✅ Compatible con texto plano
- ✅ Estándar para muchos protocolos

**Desventajas:**
- ❌ Requiere delimitador `\r\n` al final
- ❌ No puede contener `\r\n` en medio del mensaje
- ❌ Error "CRLF not found" si excede maxMessageSize

---

### 3. ByteArrayLfSerializer (Solo LF como delimitador)

**Uso:** Para mensajes terminados solo con `\n` (estilo Unix)

**Configuración:**

```java
import org.springframework.integration.ip.tcp.serializer.ByteArrayLfSerializer;

ByteArrayLfSerializer serializer = new ByteArrayLfSerializer();
serializer.setMaxMessageSize(65536);
connectionFactory.setDeserializer(serializer);
connectionFactory.setSerializer(serializer);
```

**Ventajas:**
- ✅ Más simple que CRLF (solo `\n`)
- ✅ Compatible con sistemas Unix/Linux

**Desventajas:**
- ❌ Similar a CRLF pero solo con `\n`

---

### 4. ByteArrayStxEtxSerializer (STX/ETX)

**Uso:** Para mensajes que empiezan con byte STX (0x02) y terminan con ETX (0x03)

**Configuración:**

```java
import org.springframework.integration.ip.tcp.serializer.ByteArrayStxEtxSerializer;

ByteArrayStxEtxSerializer serializer = new ByteArrayStxEtxSerializer();
serializer.setMaxMessageSize(65536);
connectionFactory.setDeserializer(serializer);
connectionFactory.setSerializer(serializer);
```

**Formato del mensaje:**
```
[STX=0x02][datos del mensaje][ETX=0x03]
```

**Ventajas:**
- ✅ Protocolo estándar de comunicaciones
- ✅ Delimitadores claros de inicio y fin

**Desventajas:**
- ❌ No puede contener bytes 0x02 o 0x03 en los datos

---

### 5. ByteArrayRawSerializer (Sin delimitador - Cierre de socket)

**Uso:** Lee hasta que el socket se cierra. **Solo para un mensaje por conexión**.

**Configuración:**

```java
import org.springframework.integration.ip.tcp.serializer.ByteArrayRawSerializer;

ByteArrayRawSerializer serializer = new ByteArrayRawSerializer();
connectionFactory.setDeserializer(serializer);
connectionFactory.setSerializer(serializer);
connectionFactory.setSingleUse(true); // IMPORTANTE: Una conexión por mensaje
```

**Ventajas:**
- ✅ No necesita delimitador
- ✅ Soporta cualquier contenido

**Desventajas:**
- ❌ Solo un mensaje por conexión
- ❌ Requiere cerrar y reabrir conexión para cada mensaje
- ❌ Ineficiente para múltiples mensajes

---

### 6. LogbackDeserializer (Objetos Java serializados)

**Uso:** Para recibir objetos ILoggingEvent de Logback SocketAppender (configuración actual)

**Configuración:**

```java
LogbackDeserializer deserializer = new LogbackDeserializer();
DefaultSerializer serializer = new DefaultSerializer();
connectionFactory.setDeserializer(deserializer);
connectionFactory.setSerializer(serializer);
```

**Ventajas:**
- ✅ Deserializa objetos Java automáticamente
- ✅ Compatible con Logback SocketAppender
- ✅ Maneja información estructurada (nivel, timestamp, logger, etc.)

**Desventajas:**
- ❌ Solo funciona con clientes Java que serialicen objetos
- ❌ No compatible con texto plano

---

## Resumen: ¿Cuál usar?

| Escenario | Serializer Recomendado |
|-----------|------------------------|
| Logs desde Logback SocketAppender | `LogbackDeserializer` (actual) |
| Mensajes de texto con `\r\n` | `ByteArrayCrLfSerializer` |
| Mensajes de texto con `\n` | `ByteArrayLfSerializer` |
| Mensajes binarios de tamaño variable | `ByteArrayLengthHeaderSerializer` |
| Protocolo STX/ETX | `ByteArrayStxEtxSerializer` |
| Un solo mensaje por conexión | `ByteArrayRawSerializer` |

---

## Cambiar la Configuración

Para cambiar el serializer, edita `src/main/java/com/tcp_log_server/TcpServerConfig.java`:

1. Comenta la configuración actual
2. Descomenta o agrega la configuración deseada
3. Actualiza el import correspondiente
4. Reinicia el servidor

### Ejemplo: Cambiar a Length Header

```java
import org.springframework.integration.ip.tcp.serializer.ByteArrayLengthHeaderSerializer;

@Bean
public AbstractServerConnectionFactory serverConnectionFactory() {
    TcpNetServerConnectionFactory connectionFactory = new TcpNetServerConnectionFactory(port);
    connectionFactory.setBacklog(backlog);

    // Configuración para mensajes con header de longitud
    ByteArrayLengthHeaderSerializer serializer = new ByteArrayLengthHeaderSerializer();
    serializer.setMaxMessageSize(maxMessageSize);
    connectionFactory.setDeserializer(serializer);
    connectionFactory.setSerializer(serializer);

    connectionFactory.setSingleUse(false);
    log.info("TCP Server configured for length-header messages (max: {} bytes)", maxMessageSize);
    return connectionFactory;
}
```

También actualiza `LogMessageHandler` para procesar `byte[]` en lugar de `ILoggingEvent`.
