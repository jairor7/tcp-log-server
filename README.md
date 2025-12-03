# TCP Log Server

Servidor de logs que recibe mensajes a través del protocolo TCP. Construido con Spring Boot 3.5.7 y Java 17.

## Descripción del Proyecto

Este proyecto implementa un servidor TCP centralizado para recolectar logs de aplicaciones remotas. El servidor:

- Escucha conexiones TCP en el puerto **9999**
- Recibe y procesa mensajes de log en diferentes formatos:
  - Objetos serializados de Logback (ILoggingEvent)
  - Texto plano en bytes o strings
- Imprime los logs recibidos en consola con formato timestamp
- Soporta conexiones persistentes y reutilizables

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

## Probando el Servidor

### Usando el Script de Prueba (PowerShell)

El proyecto incluye un script PowerShell que envía logs de prueba al servidor:

```powershell
.\send-test-logs.ps1
```

Este script envía 4 mensajes de prueba:
- INFO: User login successful
- ERROR: Database connection failed
- WARN: Cache miss
- DEBUG: Processing request

### Enviando Logs Manualmente con Telnet

```bash
telnet localhost 9999
```

Luego escribe cualquier mensaje y presiona Enter.

### Enviando Logs desde PowerShell

```powershell
$client = New-Object System.Net.Sockets.TcpClient('localhost', 9999)
$stream = $client.GetStream()
$encoding = [System.Text.Encoding]::UTF8
$message = "Mi mensaje de log`r`n"
$bytes = $encoding.GetBytes($message)
$stream.Write($bytes, 0, $bytes.Length)
$stream.Flush()
$stream.Close()
$client.Close()
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