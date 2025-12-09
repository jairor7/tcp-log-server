# TCP Log Server

Servidor de logs que recibe mensajes a través del protocolo TCP. Construido con Spring Boot 3.5.7 y Java 17.

## Descripción del Proyecto

Este proyecto implementa un servidor TCP centralizado para recolectar logs de aplicaciones remotas. El servidor:

- Escucha conexiones TCP en el puerto **9999**
- Recibe y procesa mensajes de log de Logback SocketAppender
- Deserializa objetos Java ILoggingEvent enviados por clientes Logback
- Imprime los logs recibidos en consola con formato estructurado
- Soporta conexiones persistentes y reutilizables
- Maneja excepciones y stack traces

## Tecnologías Utilizadas

- **Java 17**
- **Spring Boot 3.5.7**
- **Spring Integration IP** - Para manejo de conexiones TCP
- **Logback Classic** - Para deserializar eventos de logging
- **Lombok** - Para reducir código boilerplate
- **Gradle** - Sistema de construcción

## Requisitos Previos

- Java 17 o superior
- Gradle (incluido wrapper en el proyecto)

## Construcción del Proyecto

### En Linux/Mac:

```bash
./gradlew build
```

### En Windows:

```bash
gradlew.bat build
```

El comando `build` compila el código, ejecuta los tests y genera el archivo JAR en `build/libs/`.

### Otros comandos útiles:

```bash
# Limpiar artefactos de compilación
./gradlew clean

# Compilar sin ejecutar tests
./gradlew build -x test

# Crear JAR ejecutable
./gradlew bootJar
```

## Ejecución del Servidor

### Opción 1: Usando Gradle

```bash
./gradlew bootRun
```

En Windows:
```bash
gradlew.bat bootRun
```

### Opción 2: Ejecutando el JAR

Después de construir el proyecto:

```bash
java -jar build/libs/tcp-log-server-0.0.1-SNAPSHOT.jar
```

El servidor iniciará y escuchará en el puerto **9999**.

## Configurando Clientes para Enviar Logs

### Configuración de Logback en el Proyecto Cliente

Para que tu aplicación envíe logs al servidor, configura Logback con SocketAppender.

**Paso 1:** Asegúrate de tener Logback en tu proyecto cliente:

```gradle
// build.gradle
implementation 'ch.qos.logback:logback-classic'
```

o en Maven:

```xml
<!-- pom.xml -->
<dependency>
    <groupId>ch.qos.logback</groupId>
    <artifactId>logback-classic</artifactId>
</dependency>
```

**Paso 2:** Crea o edita el archivo `logback.xml` o `logback-spring.xml` en `src/main/resources/`:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <!-- Appender para consola local -->
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>

    <!-- Appender TCP para enviar logs al servidor -->
    <appender name="TCP" class="ch.qos.logback.classic.net.SocketAppender">
        <remoteHost>localhost</remoteHost>
        <port>9999</port>
        <reconnectionDelay>30000</reconnectionDelay>
        <includeCallerData>false</includeCallerData>
    </appender>

    <!-- Logger root -->
    <root level="INFO">
        <appender-ref ref="CONSOLE" />
        <appender-ref ref="TCP" />
    </root>
</configuration>
```

**Paso 3:** Ejecuta tu aplicación cliente. Los logs se enviarán automáticamente al servidor TCP.

### Archivo de Ejemplo

El proyecto incluye un archivo de ejemplo completo en `logback-client-example.xml` con configuraciones adicionales.

### Formato de Salida del Servidor

El servidor imprimirá los logs recibidos con el siguiente formato:

```
[2025-12-09 15:30:45.123] [INFO] com.example.MyClass - User login successful
[2025-12-09 15:30:46.456] [ERROR] com.example.MyService - Database connection failed
  Exception: java.sql.SQLException: Connection timeout
```

## Configuración

La configuración se encuentra en `src/main/resources/application.properties`:

```properties
tcp.server.port=9999        # Puerto TCP del servidor
tcp.server.backlog=100      # Número de conexiones pendientes
```

## Estructura del Proyecto

```
src/
├── main/
│   ├── java/com/tcp_log_server/
│   │   ├── TcpLogServerApplication.java  # Clase principal
│   │   ├── TcpServerConfig.java          # Configuración del servidor TCP
│   │   ├── LogMessageHandler.java        # Procesador de mensajes
│   │   └── LogbackDeserializer.java      # Deserializador de Logback
│   └── resources/
│       └── application.properties        # Configuración
└── test/
    └── java/com/tcp_log_server/
        └── TcpLogServerApplicationTests.java
```

## Ejecutar Tests

```bash
./gradlew test
```

## Licencia

Este proyecto está licenciado bajo la Licencia MIT. Ver el archivo [LICENSE](LICENSE) para más detalles.