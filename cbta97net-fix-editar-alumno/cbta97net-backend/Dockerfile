# Stage 1: Build Java Application with Maven
FROM maven:3.9-eclipse-temurin-21-alpine AS build
WORKDIR /app

# Copiar archivos pom y resolver dependencias
COPY pom.xml .
COPY src ./src

# Compilar y empaquetar sin ejecutar tests
RUN mvn clean package -DskipTests

# Stage 2: Run Application
FROM ibm-semeru-runtimes:open-21-jdk
WORKDIR /app

COPY --from=build /app/target/cbta97net-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]