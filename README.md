To run the application, make sure you have the following software installed:
- JDK 21
- Docker Desktop or Docker Engine for running a PostgreSQL container
    - Alternatively, you can use a local installation of PostgreSQL (version 18.3)

Steps:
- Make sure that Docker is running.
- Run the command to instantiate a PostgreSQL container:

```bash
docker run -d \
  --name postgres \
  -v postgres_data:/var/lib/postgresql \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=password \
  -e POSTGRES_DB=taskmanagementservice \
  -p 5433:5432 \
  postgres:latest
```
- Build the project with the following command:
    - For Windows:
      ```bash
      .\mvnw clean install -DskipTests
      ```
    - For Linux and MacOS:
      ```bash
      ./mvnw clean install -DskipTests
      ```
- Run the migration
```bash
.\mvnw flyway:migrate or ./mvnw flyway:migrate
```
- Run the application:
```bash
.\mvnw spring-boot:run or ./mvnw spring-boot:run
```
- To run all the tests:
```bash
.\mvnw clean test or ./mvnw clean test
```