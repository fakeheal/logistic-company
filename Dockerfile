# maybe use one image for building and one for running
FROM --platform=linux/amd64 amazoncorretto:21-alpine3.20-jdk  AS builder

# Set the working directory inside the container
WORKDIR /app

# Copy Gradle wrapper and necessary files
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY src src

# Ensure the Gradle wrapper has execution permission
RUN chmod +x ./gradlew

RUN ls -la

# Build the application using Gradle
RUN ./gradlew clean build --stacktrace --info -x test

# Stage 2: Run the application
FROM --platform=linux/amd64 amazoncorretto:21-alpine3.20-jdk

# Set the working directory
WORKDIR /app

# Copy the built JAR file from the builder stage
COPY --from=builder /app/build/libs/*.jar app.jar

# Expose the default port (change if your app uses a different port)
EXPOSE 8080

# Run the Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]