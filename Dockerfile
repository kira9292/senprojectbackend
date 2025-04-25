FROM openjdk:21-slim

WORKDIR /app

# Add a volume pointing to /tmp
VOLUME /tmp

# Expose the port your app runs on
EXPOSE 8081

# Copy the jar file built by Maven
COPY target/*.jar app.jar

# Set environment variables for JHipster Registry
ENV SPRING_PROFILES_ACTIVE=prod
ENV EUREKA_CLIENT_SERVICE_URL_DEFAULTZONE=http://admin:admin@jhipster-registry:8761/eureka/
ENV SPRING_CLOUD_CONFIG_URI=http://admin:admin@jhipster-registry:8761/config
ENV JHIPSTER_REGISTRY_PASSWORD=admin

# Run the jar file with proper configuration
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app/app.jar"]
