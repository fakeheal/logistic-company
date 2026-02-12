# Logistic Company

A web application for managing the operations of a logistics company -- accepting, tracking, and delivering shipments.

## Tech Stack

**Backend:** Java 21, Spring Boot 3.3.5, Spring Security, Spring Data JPA, Hibernate, MySQL, Lombok, ModelMapper

**Frontend:** Thymeleaf (server-side rendering), Tailwind CSS 3, Flowbite UI components

**Build & Tooling:** Gradle 8.1, Node.js 20 (via gradle-node-plugin for Tailwind/Rollup), Docker

**Testing:** JUnit 5, Mockito, Spring Boot Test, H2 (in-memory DB for tests)

## Features

### Public
- User registration and login (email/password with BCrypt)
- Shipment tracking by tracking number

### Client
- Register shipments (choose sender/recipient, delivery to office or address)
- View sent and received shipments with status history
- Pay for shipments online
- Cancel unpaid shipments

### Employee -- Coordinator
- Register shipments on behalf of clients (in-office)
- Confirm POS payments
- View, search, and filter all shipments
- Assign shipments to delivery employees
- Update shipment statuses
- Manage clients (create, edit, anonymize)

### Employee -- Admin
- Everything a Coordinator can do, plus:
- Employee management (create, edit, delete)
- Office management (create, edit, delete)
- Revenue reports by date range
- Shipment statistics
- Reference data management (countries, cities)

### Employee -- Deliveryman
- View assigned and undelivered shipments
- Update delivery status

### Shipment Pricing
Calculated dynamically based on weight and delivery type:
- Base price to office: 3.00 EUR
- Base price to address: 5.00 EUR
- Per kilogram: 1.00 EUR

## Prerequisites

- Java 21
- MySQL database
- Node.js 20+ (or let the Gradle plugin download it)

## Database Setup

Set the following environment variables before running:

```bash
# Linux / macOS
export LOGISTIC_COMPANY_DB_URL=jdbc:mysql://localhost:3306/logistic_company
export LOGISTIC_COMPANY_DB_USERNAME=your_username
export LOGISTIC_COMPANY_DB_PASSWORD=your_password
```

```cmd
# Windows
setx LOGISTIC_COMPANY_DB_URL jdbc:mysql://localhost:3306/logistic_company
setx LOGISTIC_COMPANY_DB_USERNAME your_username
setx LOGISTIC_COMPANY_DB_PASSWORD your_password
```

Hibernate will auto-create/update the schema (`ddl-auto=update`).

## Running the Application

```bash
./gradlew npmInstall tailwindcss rollup bootRun
```

The app starts at `http://localhost:8080`.

## Running Tests

```bash
./gradlew test
```

Tests use an in-memory H2 database and do not require MySQL. Unit tests cover the service layer (ShipmentService, ClientService, EmployeeService, UserService, OfficeService, AddressService, CityService, CountryService).
