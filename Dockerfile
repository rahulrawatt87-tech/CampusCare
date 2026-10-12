# =====================================================================
# CampusCare - Production Dockerfile for Render Deployment
# Multi-stage build with Eclipse Temurin OpenJDK 21
# =====================================================================

# Stage 1: Build application
FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /app

# Cache Maven dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy sources and package
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Runtime image
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Copy built artifacts and webapp resources
COPY --from=builder /app/target /app/target
COPY --from=builder /app/src/main/webapp /app/src/main/webapp
COPY --from=builder /app/pom.xml /app/pom.xml

# Environment defaults (Render overrides $PORT at runtime)
ENV PORT=8080
ENV SERVER_CONTEXT_PATH=""

EXPOSE 8080

# Run embedded Tomcat ServerLauncher
ENTRYPOINT ["sh", "-c", "java -cp 'target/classes:target/CampusCare/WEB-INF/lib/*:target/CampusCare/WEB-INF/classes' com.campuscare.ServerLauncher"]
