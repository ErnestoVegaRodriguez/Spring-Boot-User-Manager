# Stage 1: Build the application
FROM eclipse-temurin:25-jdk AS builder

# Set the working directory
WORKDIR /app

# Copy the application code
COPY . .

# Build the application using maven or gradle
RUN ./mvnw clean package -DskipTests

# Stage 2: Run the application
FROM  eclipse-temurin:25-jre

# Set the working directory
WORKDIR /app

# Copy the built application from the builder stage
COPY --from=builder /app/target/*.jar app.jar

# Expose the application port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]