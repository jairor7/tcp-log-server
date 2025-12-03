# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a Spring Boot 3.5.7 application designed to receive logs via TCP protocol. The project uses:
- Java 17
- Gradle build system
- Spring Boot Starter (base framework)
- Lombok (for reducing boilerplate code)
- JUnit 5 for testing

**Important**: The package name is `com.tcp_log_server` (with underscores), not `com.tcp-log-server`. This is due to Java package naming restrictions.

## Build and Development Commands

### Build the project
```bash
./gradlew build
```
On Windows:
```bash
gradlew.bat build
```

### Run the application
```bash
./gradlew bootRun
```

### Run tests
```bash
./gradlew test
```

### Run a single test class
```bash
./gradlew test --tests "com.tcp_log_server.TcpLogServerApplicationTests"
```

### Run a specific test method
```bash
./gradlew test --tests "com.tcp_log_server.ClassName.methodName"
```

### Clean build artifacts
```bash
./gradlew clean
```

### Create bootable JAR
```bash
./gradlew bootJar
```
The JAR will be located in `build/libs/`

## Architecture

### Current State
The codebase is in its initial state with minimal implementation:
- `TcpLogServerApplication.java`: Main Spring Boot application entry point
- Basic Spring Boot configuration in `application.properties`
- Single context loading test

### Expected Architecture (to be implemented)
Since this is a TCP log server, future development will likely include:
- TCP server implementation (likely using Spring Integration TCP support or Netty)
- Log message parsing and processing components
- Storage/persistence layer for received logs
- Configuration for TCP port, buffer sizes, and connection handling

### Configuration
Application configuration is in `src/main/resources/application.properties`. Spring Boot auto-configuration is enabled via `@SpringBootApplication` annotation.
