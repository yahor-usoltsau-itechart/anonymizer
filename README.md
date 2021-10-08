# Anonymizer

An anonymization service which takes a text as an input and replaces all occurrences of URLs, IDs and E-mails with fake data.

- URL - any valid URL but need to only replace the second level domain
- E-mail - any valid email address
- ID - any character sequence of number longer than 3 digits

## Tech Stack

- Kotlin JVM (Java 11)
- Gradle
- Spring Boot
- PostgreSQL + Flyway
- Docker + Testcontainers

## Getting Started

Clone the project:

```shell
git clone https://github.com/yahor-usoltsau-itechart/anonymizer.git
```

Go to the project directory:

```shell
cd anonymizer
```

Build the project:

```shell
./gradlew build
```

### Running Locally

Start PostgreSQL database:

```shell
docker-compose up -d
```

Start the application server with `local` profile:

```shell
java -jar build/libs/anonymizer-0.0.1-SNAPSHOT.jar --spring.profiles.active=local
```

By default, the server will run on port 8080.

### Running Tests

To run unit tests, use the following command:

```shell
./gradlew test
```

To run integration tests, use the following command:

```shell
./gradlew testIntegration
```

## Usage Example

```shell
curl -v -X POST http://localhost:8080/anonymize \
  -H 'Content-Type: application/json' \
  -d '{"text":"Test:\n- ID: 12345\n- E-mail: foo@bar.com\n- URL: https://www.example.co.uk/orders/12345"}'
```

```http
HTTP/1.1 200 
Content-Type: application/json

{"text":"Test:\n- ID: 21030\n- E-mail: clement.runte@yahoo.com\n- URL: https://www.armstrong.biz/orders/12345"}
```

## Environment Variables

- `SPRING_PROFILES_ACTIVE`
- `SPRING_DATASOURCE_URL`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`
- `SERVER_PORT`

See: [application.yml](src/main/resources/application.yml)
