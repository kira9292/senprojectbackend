FROM quay.io/keycloak/keycloak:26.2.3

# Copie des fichiers custom dans l'image
COPY src/main/docker/realm-config/ /opt/keycloak/data/import/
COPY src/main/docker/realm-config/keycloak-health-check.sh /opt/keycloak/health-check.sh
COPY src/main/docker/realm-config/keycloak-theme-sen-project.jar /opt/keycloak/providers/keycloak-theme-sen-project.jar


# Lancement de Keycloak avec import du realm et thème custom activé
ENTRYPOINT ["/opt/keycloak/bin/kc.sh"]
CMD ["start", "--import-realm", "--optimized"]
