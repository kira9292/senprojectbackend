FROM openjdk:21-slim

WORKDIR /app

# Add a volume pointing to /tmp
VOLUME /tmp

# Expose the port your app runs on
EXPOSE 8081

# Copy the jar file built by Maven
COPY target/*.jar app.jar

# Set environment variables for JHipster Registry


# Run the jar file with proper configuration
ENTRYPOINT ["java","-Djava.security.egd=file:/prod/./urandom","-jar","/app/app.jar"]
