FROM quay.io/keycloak/keycloak:26.2.3

# Copie des fichiers custom dans l'image
COPY realm-config/ /opt/keycloak/data/import/
COPY realm-config/keycloak-health-check.sh /opt/keycloak/health-check.sh
COPY realm-config/keycloak-theme-sen-project.jar /opt/keycloak/providers/keycloak-theme-sen-project.jar


# Lancement de Keycloak avec import du realm et thème custom activé
ENTRYPOINT ["/opt/keycloak/bin/kc.sh"]
CMD ["start", "--import-realm", "--optimized"]
