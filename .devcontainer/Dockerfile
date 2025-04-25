# Utilisation de l'image Java pour JHipster

FROM mcr.microsoft.com/vscode/devcontainers/java

# Installer Maven
ARG INSTALL_MAVEN="true"
ARG MAVEN_VERSION="3.9.6"
RUN if [ "${INSTALL_MAVEN}" = "true" ]; then \
    su vscode -c "umask 0002 && . /usr/local/sdkman/bin/sdkman-init.sh && sdk install maven \"${MAVEN_VERSION}\""; \
fi

# Définition du répertoire de travail
WORKDIR /app

# Copier uniquement les fichiers essentiels pour éviter une reconstruction complète à chaque modification
COPY ../pom.xml .
COPY ../src/ src/
COPY ../.jhipster .jhipster/


# Installer les dépendances et construire l'application
RUN mvn clean install -DskipTests

# Installer Docker CLI pour interagir avec docker-compose
RUN apt-get update && apt-get install -y docker.io docker-compose

# Exposer le port de l'application
EXPOSE 8080

# Lancer l'application avec docker-compose
CMD ["docker-compose", "-f", "src/docker/services.yml",  "up"]
