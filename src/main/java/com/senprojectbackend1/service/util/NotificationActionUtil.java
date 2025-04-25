package com.senprojectbackend1.service.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Utilitaire pour la création des actions de notification.
 * Cette classe standardise le format JSON des actions associées aux notifications.
 */
@Component
public class NotificationActionUtil {

    private static final Logger LOG = LoggerFactory.getLogger(NotificationActionUtil.class);
    private final ObjectMapper objectMapper;

    public NotificationActionUtil(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Crée une action de type API à partir des paramètres fournis.
     *
     * @param label Le libellé du bouton d'action
     * @param endpoint Le endpoint de l'API
     * @param method La méthode HTTP (GET, POST, PUT, DELETE)
     * @param payload Le payload à envoyer (peut être null)
     * @return Map représentant l'action
     */
    public Map<String, Object> createApiAction(String label, String endpoint, String method, Map<String, Object> payload) {
        Map<String, Object> action = new HashMap<>();
        action.put("type", "api");
        action.put("label", label);
        action.put("endpoint", endpoint);
        action.put("method", method);
        if (payload != null && !payload.isEmpty()) {
            action.put("payload", payload);
        }
        return action;
    }

    /**
     * Crée une action de redirection à partir des paramètres fournis.
     *
     * @param label Le libellé du bouton d'action
     * @param url L'URL de redirection
     * @return Map représentant l'action
     */
    public Map<String, Object> createRedirectAction(String label, String url) {
        Map<String, Object> action = new HashMap<>();
        action.put("type", "redirect");
        action.put("label", label);
        action.put("url", url);
        return action;
    }

    /**
     * Crée un tableau d'actions et le convertit en JSON.
     *
     * @param actions La liste des actions à inclure
     * @return String JSON représentant le tableau d'actions
     */
    public String createActionsJson(List<Map<String, Object>> actions) {
        try {
            return objectMapper.writeValueAsString(actions);
        } catch (Exception e) {
            LOG.error("Erreur lors de la conversion des actions en JSON", e);
            return "[]";
        }
    }

    /**
     * Crée un tableau d'actions à partir de paires clé-valeur où la clé est le label du bouton.
     * Cette méthode est fournie pour la compatibilité avec l'ancienne implémentation.
     *
     * @param actionsMap Map contenant les actions (label -> paramètres)
     * @param baseEndpoint L'endpoint de base à utiliser
     * @return String JSON représentant le tableau d'actions
     */
    public String createActions(Map<String, Map<String, Object>> actionsMap, String baseEndpoint) {
        List<Map<String, Object>> actionsList = new ArrayList<>();

        for (Map.Entry<String, Map<String, Object>> entry : actionsMap.entrySet()) {
            String label = entry.getKey();
            Map<String, Object> params = entry.getValue();

            String method = (String) params.remove("method");
            if (method == null) {
                method = "GET"; // Par défaut
            }

            Map<String, Object> action = createApiAction(label, baseEndpoint, method, params);
            actionsList.add(action);
        }

        return createActionsJson(actionsList);
    }

    /**
     * Version simplifiée de createActions pour les cas avec peu de paramètres.
     *
     * @param actionsMap Map contenant les actions (label -> paramètres)
     * @param baseEndpoint L'endpoint de base à utiliser
     * @return String JSON représentant le tableau d'actions
     */
    public String createCompactActions(Map<String, Map<String, Object>> actionsMap, String baseEndpoint) {
        return createActions(actionsMap, baseEndpoint);
    }
}
