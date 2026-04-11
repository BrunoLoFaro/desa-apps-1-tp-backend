# ── Stage 1: Build ────────────────────────────────────
FROM eclipse-temurin:17-jdk AS build
WORKDIR /app

ARG HTTP_PROXY
ARG HTTPS_PROXY
ARG NO_PROXY
ENV http_proxy=${HTTP_PROXY}
ENV https_proxy=${HTTPS_PROXY}
ENV no_proxy=${NO_PROXY}

# Cache Maven wrapper & dependencies first
COPY mvnw mvnw.cmd ./
COPY .mvn .mvn
COPY pom.xml .
RUN chmod +x mvnw && ./mvnw dependency:go-offline -B

# Build the application
COPY src ./src
RUN ./mvnw package -DskipTests -B

# ── Stage 2: Runtime ─────────────────────────────────
FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
