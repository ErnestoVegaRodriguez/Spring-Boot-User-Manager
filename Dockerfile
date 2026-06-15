# Stage 1: Build
FROM eclipse-temurin:25-jdk AS builder
WORKDIR /app

# 1. Copiar el wrapper de Maven primero
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

# 2. Descargar dependencias (esta capa se cachea si pom.xml no cambia)
# dependency:go-offline le dice a Maven "descarga todo lo que necesitas ahora". 
# Así esa capa queda cacheada y los builds siguientes son mucho más rápidos.
RUN ./mvnw dependency:go-offline -q

# 3. Copiar el código fuente (esto sí cambia seguido)
COPY src ./src

# 4. Construir
RUN ./mvnw clean package -DskipTests

# Stage 2: Run the application
FROM  eclipse-temurin:25-jre AS runtime

# Set the working directory
WORKDIR /app

# Copy the built application from the builder stage
COPY --from=builder /app/target/*.jar app.jar

# Expose the application port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]