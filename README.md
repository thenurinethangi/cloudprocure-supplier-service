# CloudProcure Supplier Service

## Submission identity

- Student Name: `<STUDENT_NAME>`
- Student Number: `<STUDENT_NUMBER>`
- Slack Handle: `<SLACK_HANDLE>`
- GCP Project ID: `<GCP_PROJECT_ID>`

## Role and stack

Java 25, Spring Boot 4.1.0, Spring Data MongoDB, Config/Eureka clients, Firestore, and Actuator. The service owns supplier profiles, embedded contacts/addresses, status/soft deletion, catalog offerings, indexes, and local-only idempotent sample data.

Public APIs are `/api/suppliers/**` and `/api/catalog-items/**`. Order uses `/internal/suppliers/{id}/active`; Gateway never exposes it.

## Build, test, and run

```powershell
.\mvnw.cmd clean test
.\mvnw.cmd clean package
$env:SPRING_PROFILES_ACTIVE='local'
$env:MONGODB_URI='<LOCAL_MONGODB_URI>'
$env:CONFIG_SERVER_URL='http://localhost:8888'
.\mvnw.cmd spring-boot:run
```

Default port/health: `8082`, `/actuator/health`. Use the same local `ACTIVITY_LOG_PATH` as the other business services. Production publishes independently to Firestore through ADC and never seeds sample data or uses shared filesystem state. No credential files belong in the repository.

The baseline has no authentication. Production actor identity cannot come from development headers; future authorization maps authenticated principals to the approved roles.
