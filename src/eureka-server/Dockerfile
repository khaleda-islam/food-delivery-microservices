# Multi-stage build for Eureka Server
FROM maven:3.9-eclipse-temurin-21 AS build

# Set working directory
WORKDIR /app

# Copy pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:21-jre-alpine

# Install wget for health checks
RUN apk add --no-cache wget

WORKDIR /app

# Copy the jar from build stage
COPY --from=build /app/target/*.jar app.jar

# Expose Eureka Server port
EXPOSE 8761

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
