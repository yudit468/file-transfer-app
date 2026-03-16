# File Transfer Application

Production-ready file transfer application built with Java 8, Spring Boot, and Apache Camel.

## Architecture

The application monitors configured source directories for new files. When a file is detected, it:

1. Extracts file metadata (name, source path, destination path, timestamp)
2. Copies the file to the configured destination directory
3. Sends a POST request to a REST API with the transfer details
4. Processes the API response

Routes are created dynamically based on configuration, supporting N directory mappings.

## Technology Stack

- **Java 8**
- **Spring Boot 2.1.x**
- **Apache Camel 2.25.x** (Java DSL)
- **Jackson** (JSON processing)
- **Maven**
- **SLF4J** (logging)

## Project Structure

```
src/main/java/com/company/filetransfer
├── config
│   └── CamelRouteConfig.java          # Dynamic route creation
├── routes
│   └── FileTransferRoute.java         # Camel route definition
├── processor
│   ├── FileMetadataProcessor.java     # Step 2: Extract metadata
│   ├── FileCopyProcessor.java         # Step 3: Copy file
│   ├── ApiPayloadProcessor.java       # Step 4: Build REST payload
│   └── ApiResponseProcessor.java      # Step 6: Process API response
├── service
│   └── DirectoryConfigService.java    # Read directory config
├── model
│   └── FileTransferRequest.java       # REST payload model
└── Application.java                   # Entry point
```

## Configuration

All configuration is in `src/main/resources/application.yml`.

### Directory Mappings

Add as many source-destination pairs as needed:

```yaml
file:
  routes:
    - source: /data/path1
      destination: /mnt/server/path1
    - source: /data/path2
      destination: /mnt/server/path2
    - source: /data/path3
      destination: /mnt/server/path3
  error:
    directory: /data/error
```

### REST API Endpoint

Configure the API endpoint that receives file transfer notifications:

```yaml
api:
  endpoint:
    url: http4://your-api-host:port/api/file-transfer
```

> **Note:** Apache Camel 2.x uses the `http4` component scheme for HTTP calls.

### Error Directory

Files that fail processing are moved to the error directory:

```yaml
file:
  error:
    directory: /data/error
```

## REST API Payload

When a file is successfully copied, a POST request is sent with the following JSON payload:

```json
{
  "fileName": "fileA.txt",
  "sourcePath": "/data/path1",
  "destinationPath": "/mnt/server/path1",
  "timestamp": "2026-03-16T10:10:00"
}
```

## Build

```bash
mvn clean package
```

To skip tests during build:

```bash
mvn clean package -DskipTests
```

## Run

Run with a specific Spring profile using program arguments:

```bash
java -jar target/file-transfer-1.0.0.jar --spring.profiles.active=dev
```

Or using Maven:

```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"
```

### Prerequisites

- Source directories must exist and be readable
- Destination directories must be writable (created automatically if missing)
- The REST API endpoint must be accessible
- Error directory path must be writable

## Processing Flow

```
File appears in source directory
        │
        ▼
[FileMetadataProcessor] ─── Extract fileName, sourcePath, destinationPath, timestamp
        │
        ▼
[FileCopyProcessor] ─── Copy file from source to destination
        │
        ▼
[ApiPayloadProcessor] ─── Build JSON payload
        │
        ▼
[HTTP POST] ─── Send notification to REST API
        │
        ▼
[ApiResponseProcessor] ─── Log API response
        │
        ▼
    Complete
```

### Error Handling

If any step fails:
- The error is logged
- The file is moved to the configured error directory (`/data/error` by default)
- Processing continues for other files

## Logging

The application logs the following events:
- File detected in source directory
- Metadata extracted
- Copy started / completed
- API request sent
- API response received
- Errors with full stack traces

Configure log levels in `application.yml`:

```yaml
logging:
  level:
    com.company.filetransfer: DEBUG
    org.apache.camel: INFO
```

## Profile-Based Configuration

Create profile-specific configuration files:

- `application-dev.yml` — Development settings
- `application-staging.yml` — Staging settings
- `application-prod.yml` — Production settings

Activate via program argument:

```bash
java -jar target/file-transfer-1.0.0.jar --spring.profiles.active=prod
```
