# Stage 1: Build with Gradle Wrapper
FROM gradle:7.6.4-jdk17 AS builder
WORKDIR /app

# Copy wrapper first
COPY gradlew ./
COPY gradlew.bat ./
COPY gradle ./gradle

# Copy project build files
COPY settings.gradle.kts ./
COPY gradle.properties* ./
COPY app/build.gradle.kts app/

# Download dependencies
RUN ./gradlew build -x test --no-daemon || return 0

# Copy the rest of the source
COPY . .

# Build Spring Boot fat jar
RUN ./gradlew bootJar --no-daemon

# Stage 2: Run the jar
FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
