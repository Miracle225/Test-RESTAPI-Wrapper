# GraalJS REST API

This project provides a REST API to execute JavaScript code using the GraalJS engine.

## Build and Run

### Prerequisites

- Java 17
- Maven

### Build

To build the project, run:

```bash
mvn clean install
```

### Run

To run the project, use:

```bash
mvn spring-boot:run
```

The application will start on http://localhost:8080.

## API Endpoints
#### Execute Script
```bash
POST /scripts/execute
```

Request Body: JavaScript code as plain text.

Optional Query Parameter: blocking (boolean, default: false)

Response: Script execution details.

#### List Scripts
```bash
GET /scripts
```

Optional Query Parameters: status (string), order (string, values: asc or desc)

Response: List of scripts.

#### Get Script Details
```bash 
GET /scripts/{id}
```

Response: Script details.

#### Stop Script
```bash
POST /scripts/{id}/stop
```

Response: None.

#### Remove Script
```bash
DELETE /scripts/{id}
```
Response: None.