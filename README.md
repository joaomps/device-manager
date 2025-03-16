# Device Management API

A REST API for persisting and managing device resources with comprehensive CRUD operations and
domain-specific validations.

## Overview

This application provides a complete solution for device management through a RESTful API. It allows
creating, updating, fetching, and deleting device resources while enforcing domain-specific
validation rules.

## Features

- Create new devices
- Fully or partially update existing devices
- Fetch a single device by ID
- Fetch all devices
- Filter devices by brand
- Filter devices by state (available, in-use, inactive)
- Delete devices
- Check applicaton health provided by spring actuator

## Domain Model

The core entity in this application is the Device, which has the following properties:

- **Id**: Unique identifier
- **Name**: Device name
- **Brand**: Device manufacturer
- **State**: Current status (available, in-use, inactive)
- **Creation time**: When the device was created

## Domain Validations

- Creation time cannot be updated
- Name and brand properties cannot be updated if the device is in use
- Devices in "in-use" state cannot be deleted

## Tech Stack

- Java 21
- Spring Boot 3.4.x
- Spring Data JPA
- PostgreSQL (for persistence)
- Maven
- Docker \& Docker Compose
- Springdoc OpenAPI (for API documentation)

## How to Run

### Prerequisites

- Java 21
- Maven 3.9+
- Docker and Docker Compose

### Running with Docker

1. Build the application:

```bash
./mvnw clean package -DskipTests
```

2. Start the containers:

```bash
docker compose up
```

3. The API will be available at: http://localhost:8080/api/v1/devices
4. API documentation can be accessed at: http://localhost:8080/swagger-ui.html

## API Endpoints

| Method | Endpoint                      | Description               |
|:-------|:------------------------------|:--------------------------|
| POST   | /api/v1/devices               | Create a new device       |
| GET    | /api/v1/devices               | Fetch all devices         |
| GET    | /api/v1/devices/{id}          | Fetch a device by ID      |
| GET    | /api/v1/devices?brand={brand} | Fetch devices by brand    |
| GET    | /api/v1/devices?state={state} | Fetch devices by state    |
| PUT    | /api/v1/devices/{id}          | Fully update a device     |
| PATCH  | /api/v1/devices/{id}          | Partially update a device |
| DELETE | /api/v1/devices/{id}          | Delete a device           |

## Future Improvements

- Add pagination for endpoints returning multiple devices
- Implement caching for frequently accessed data
- Add authentication and authorization
- Implement rate limiting
- Add monitoring and health check endpoints
