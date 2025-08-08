FROM openjdk:21-slim

# Install curl for health checks
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

WORKDIR /app

# Add a volume pointing to /tmp
VOLUME /tmp

# Create non-root user for security
RUN groupadd -r appuser && useradd -r -g appuser appuser

# Expose the port your app runs on
EXPOSE 8081

# Copy the jar file built by Maven
COPY target/*.jar app.jar

# Change ownership to non-root user
RUN chown -R appuser:appuser /app

# Switch to non-root user
USER appuser

# Set JVM options for production
ENV JAVA_OPTS="-Xmx512m -Xms256m -XX:+UseG1GC -XX:+UseStringDeduplication -Djava.security.egd=file:/dev/./urandom"

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8081/management/health || exit 1

# Run the jar file with proper configuration
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
