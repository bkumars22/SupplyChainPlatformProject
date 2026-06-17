# ── Stage 1: Build ───────────────────────────────────────
FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /app

# Cache dependencies first
COPY pom.xml .
RUN mvn dependency:go-offline -q

# Build the WAR
COPY src ./src
RUN mvn package -DskipTests -Dmaven.test.skip=true -Dmaven.test.compile.skip=true -q

# ── Stage 2: Runtime ─────────────────────────────────────
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Add curl for healthcheck
RUN apk add --no-cache curl

# Copy WAR from builder
COPY --from=builder /app/target/pcm-0.0.1-SNAPSHOT.war app.war

# Create non-root user
RUN addgroup -S scip && adduser -S scip -G scip
USER scip

EXPOSE 8089

ENTRYPOINT ["java", \
  "-Xmx512m", \
  "-Xms256m", \
  "-Dspring.profiles.active=prod", \
  "-jar", "app.war"]
