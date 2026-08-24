# CloudProcure Supplier Service

Supplier and catalog microservice for the **ProcureFlow Enterprise Procurement System**, developed for the ITS 2130 - Enterprise Cloud Architecture final project.

## Submission Information

- **Student Name:** Thenuri Nethangi Nanayakkara
- **Student ID:** 241711017
- **Module:** ITS 2130 - Enterprise Cloud Architecture
- **GCP Project ID:** `procureflow-eca`
- **Primary Region:** `us-central1`

## Parent Repository

This repository is included as a Git submodule of the CloudProcure Services super-repository:

https://github.com/thenurinethangi/cloudprocure-services

## Live Application

https://procureflow-frontend-7vni4yihhq-uc.a.run.app

## Purpose

The Supplier Service manages supplier and supplier catalog information.

Main responsibilities include:

- Supplier profiles
- Supplier status
- Supplier contacts
- Supplier categories
- Catalog items
- Catalog availability
- Supplier-related activity events

## Technology Stack

- Java 25
- Spring Boot 4.1.0
- Spring Data MongoDB
- MongoDB
- Google Firestore
- Spring Cloud Config Client
- Eureka Client
- Spring Boot Actuator

## API

Main API paths:

- `/api/suppliers`
- `/api/catalog-items`

Default application port:

`8082`

Health endpoint:

`/actuator/health`

## Data and Cloud Integration

Supplier and catalog data is stored in **MongoDB**.

Application activity and audit events are stored in **Google Firestore**.

Examples include:

- `SUPPLIER_CREATED`
- `CATALOG_ITEM_CREATED`

## GCP Deployment

The service is deployed to Google Compute Engine using:

- Managed Instance Group
- Multiple VM instances
- Multi-zone deployment
- Autoscaling
- Instance Template
- Health Check
- Custom VPC
- Cloud NAT
- Service Account
- PM2 process management

The MongoDB server runs inside the private project network.

The service loads configuration from Config Server and registers with Eureka.

## Setup / Getting Started

### Prerequisites

- Java 25
- MongoDB
- Maven Wrapper included with the project
- Config Server
- Eureka Server

### Build and Test

Windows:

```powershell
.\mvnw.cmd clean test
.\mvnw.cmd clean package
```

Linux/macOS:

```bash
./mvnw clean test
./mvnw clean package
```

### Run Locally

Windows PowerShell:

```powershell
$env:SPRING_PROFILES_ACTIVE="local"
$env:CONFIG_SERVER_URL="http://localhost:8888"

.\mvnw.cmd spring-boot:run
```

Default port:

```text
8082
```

Health endpoint:

```text
http://localhost:8082/actuator/health
```

Production MongoDB credentials and Google Cloud configuration are supplied externally and are not committed to Git.
