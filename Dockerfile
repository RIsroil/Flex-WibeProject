# =====================
# Build stage
# =====================
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY . .
RUN mvn clean package -DskipTests

# =====================
# Run stage
# =====================
FROM eclipse-temurin:21-jdk
WORKDIR /app

# Copy jar from build stage
COPY --from=build /app/target/*.jar app.jar

# Expose container port (Spring Boot runs on 8080)
EXPOSE 8080

# Run the app with production profile
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=production"]
