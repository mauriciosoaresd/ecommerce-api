# Stage 1: Build
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /app

# Copies maven configuration files to solve dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copies code folder and generates JAR packate skipping tests
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Final execution image
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Creates non-root user to execute as security and good practice
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copies generated JAR file at build stage
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]