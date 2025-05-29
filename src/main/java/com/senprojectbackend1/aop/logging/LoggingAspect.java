package com.senprojectbackend1.aop.logging;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import tech.jhipster.config.JHipsterConstants;

/**
 * Aspect amélioré pour logger les services, repositories et API calls de manière structurée pour Kibana
 * Combine les fonctionnalités de l'ancien LoggingAspect JHipster avec des logs structurés
 */
@Aspect
public class LoggingAspect {

    private final Environment env;
    private final ObjectMapper objectMapper;
    private final boolean useJsonFormat;

    public LoggingAspect(Environment env) {
        this.env = env;
        this.objectMapper = new ObjectMapper();
        // Utiliser le format JSON en production ou si explicitement configuré
        this.useJsonFormat =
            env.acceptsProfiles(Profiles.of(JHipsterConstants.SPRING_PROFILE_PRODUCTION)) ||
            env.getProperty("jhipster.logging.use-json-format", Boolean.class, false);
    }

    /**
     * Pointcut qui correspond aux repositories, services et REST controllers
     */
    @Pointcut(
        "within(@org.springframework.stereotype.Repository *)" +
        " || within(@org.springframework.stereotype.Service *)" +
        " || within(@org.springframework.web.bind.annotation.RestController *)"
    )
    public void springBeanPointcut() {
        // Method is empty as this is just a Pointcut, the implementations are in the advices.
    }

    /**
     * Pointcut qui correspond aux packages principaux de l'application
     */
    @Pointcut(
        "within(com.senprojectbackend1.repository..*)" +
        " || within(com.senprojectbackend1.service..*)" +
        " || within(com.senprojectbackend1.web.rest..*)"
    )
    public void applicationPackagePointcut() {
        // Method is empty as this is just a Pointcut, the implementations are in the advices.
    }

    /**
     * Pointcut spécifique pour les contrôleurs REST (logs structurés)
     */
    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
    public void restControllerPointcut() {
        // Method is empty as this is just a Pointcut, the implementations are in the advices.
    }

    /**
     * Récupère le logger associé au JoinPoint
     */
    private Logger logger(JoinPoint joinPoint) {
        return LoggerFactory.getLogger(joinPoint.getSignature().getDeclaringTypeName());
    }

    /**
     * Advice pour les contrôleurs REST avec logs structurés pour Kibana
     */
    @Around("restControllerPointcut() && applicationPackagePointcut()")
    public Object logAroundRestControllers(ProceedingJoinPoint joinPoint) throws Throwable {
        Logger logger = logger(joinPoint);

        // Générer un ID de trace unique pour cette requête
        String traceId = generateTraceId();
        MDC.put("traceId", traceId);

        // Récupération des informations de la requête HTTP
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes != null ? attributes.getRequest() : null;

        if (logger.isDebugEnabled()) {
            if (useJsonFormat) {
                // Log structuré pour l'entrée
                Map<String, Object> logEntry = createStructuredLogEntry(joinPoint, request, "API_CALL_START");
                logEntry.put("traceId", traceId);
                try {
                    logger.debug("API_CALL_START {}", objectMapper.writeValueAsString(logEntry));
                } catch (Exception e) {
                    logger.debug(
                        "API Call Started: {}.{}() [{}]",
                        joinPoint.getSignature().getDeclaringTypeName(),
                        joinPoint.getSignature().getName(),
                        traceId
                    );
                }
            } else {
                logger.debug(
                    "Enter: {}() with argument[s] = {} [{}]",
                    joinPoint.getSignature().getName(),
                    Arrays.toString(joinPoint.getArgs()),
                    traceId
                );
            }
        }

        long startTime = System.currentTimeMillis();

        try {
            Object result = joinPoint.proceed();

            if (logger.isDebugEnabled()) {
                long executionTime = System.currentTimeMillis() - startTime;

                if (useJsonFormat) {
                    Map<String, Object> logExit = createStructuredLogEntry(joinPoint, request, "API_CALL_SUCCESS");
                    logExit.put("traceId", traceId);
                    logExit.put("executionTimeMs", executionTime);
                    logExit.put("success", true);

                    // Ajouter des métriques sur le résultat si c'est un Mono/Flux
                    if (result != null) {
                        logExit.put("resultType", result.getClass().getSimpleName());
                        if (result.toString().contains("Mono") || result.toString().contains("Flux")) {
                            logExit.put("isReactive", true);
                        }
                    }

                    try {
                        logger.debug("API_CALL_SUCCESS {}", objectMapper.writeValueAsString(logExit));
                    } catch (Exception e) {
                        logger.debug(
                            "API Call Completed: {}.{}() - {}ms [{}]",
                            joinPoint.getSignature().getDeclaringTypeName(),
                            joinPoint.getSignature().getName(),
                            executionTime,
                            traceId
                        );
                    }
                } else {
                    logger.debug(
                        "Exit: {}() with result = {} - {}ms [{}]",
                        joinPoint.getSignature().getName(),
                        result,
                        executionTime,
                        traceId
                    );
                }
            }

            return result;
        } catch (IllegalArgumentException e) {
            long executionTime = System.currentTimeMillis() - startTime;
            logError(joinPoint, request, e, executionTime, "IllegalArgumentException", logger, traceId);
            throw e;
        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            logError(joinPoint, request, e, executionTime, e.getClass().getSimpleName(), logger, traceId);
            throw e;
        } finally {
            MDC.remove("traceId");
        }
    }

    /**
     * Advice pour services et repositories
     */
    @Around("applicationPackagePointcut() && springBeanPointcut() && !restControllerPointcut()")
    public Object logAroundServicesAndRepositories(ProceedingJoinPoint joinPoint) throws Throwable {
        Logger log = logger(joinPoint);

        // Récupérer le traceId du contexte si disponible
        String traceId = MDC.get("traceId");
        if (traceId == null) {
            traceId = generateTraceId();
            MDC.put("traceId", traceId);
        }

        if (log.isDebugEnabled()) {
            if (useJsonFormat) {
                Map<String, Object> logEntry = new HashMap<>();
                logEntry.put("eventType", "SERVICE_CALL_START");
                logEntry.put("className", joinPoint.getSignature().getDeclaringTypeName());
                logEntry.put("methodName", joinPoint.getSignature().getName());
                logEntry.put("traceId", traceId);
                logEntry.put("timestamp", System.currentTimeMillis());
                logEntry.put("argsCount", joinPoint.getArgs() != null ? joinPoint.getArgs().length : 0);

                try {
                    log.debug("SERVICE_CALL_START {}", objectMapper.writeValueAsString(logEntry));
                } catch (Exception e) {
                    log.debug(
                        "Enter: {}() with argument[s] = {} [{}]",
                        joinPoint.getSignature().getName(),
                        Arrays.toString(joinPoint.getArgs()),
                        traceId
                    );
                }
            } else {
                log.debug(
                    "Enter: {}() with argument[s] = {} [{}]",
                    joinPoint.getSignature().getName(),
                    Arrays.toString(joinPoint.getArgs()),
                    traceId
                );
            }
        }

        long startTime = System.currentTimeMillis();

        try {
            Object result = joinPoint.proceed();

            if (log.isDebugEnabled()) {
                long executionTime = System.currentTimeMillis() - startTime;

                if (useJsonFormat) {
                    Map<String, Object> logExit = new HashMap<>();
                    logExit.put("eventType", "SERVICE_CALL_SUCCESS");
                    logExit.put("className", joinPoint.getSignature().getDeclaringTypeName());
                    logExit.put("methodName", joinPoint.getSignature().getName());
                    logExit.put("traceId", traceId);
                    logExit.put("timestamp", System.currentTimeMillis());
                    logExit.put("executionTimeMs", executionTime);
                    logExit.put("success", true);

                    if (result != null) {
                        logExit.put("resultType", result.getClass().getSimpleName());
                        if (result.toString().contains("Mono") || result.toString().contains("Flux")) {
                            logExit.put("isReactive", true);
                        }
                    }

                    try {
                        log.debug("SERVICE_CALL_SUCCESS {}", objectMapper.writeValueAsString(logExit));
                    } catch (Exception e) {
                        log.debug(
                            "Exit: {}() with result = {} - {}ms [{}]",
                            joinPoint.getSignature().getName(),
                            result,
                            executionTime,
                            traceId
                        );
                    }
                } else {
                    log.debug(
                        "Exit: {}() with result = {} - {}ms [{}]",
                        joinPoint.getSignature().getName(),
                        result,
                        executionTime,
                        traceId
                    );
                }
            }

            return result;
        } catch (IllegalArgumentException e) {
            long executionTime = System.currentTimeMillis() - startTime;

            if (useJsonFormat) {
                Map<String, Object> logError = new HashMap<>();
                logError.put("eventType", "SERVICE_CALL_ERROR");
                logError.put("className", joinPoint.getSignature().getDeclaringTypeName());
                logError.put("methodName", joinPoint.getSignature().getName());
                logError.put("traceId", traceId);
                logError.put("timestamp", System.currentTimeMillis());
                logError.put("executionTimeMs", executionTime);
                logError.put("success", false);
                logError.put("errorType", "IllegalArgumentException");
                logError.put("errorMessage", e.getMessage());

                try {
                    log.error("SERVICE_CALL_ERROR {}", objectMapper.writeValueAsString(logError));
                } catch (Exception jsonException) {
                    log.error(
                        "Illegal argument: {} in {}() - {}ms [{}]",
                        Arrays.toString(joinPoint.getArgs()),
                        joinPoint.getSignature().getName(),
                        executionTime,
                        traceId
                    );
                }
            } else {
                log.error(
                    "Illegal argument: {} in {}() - {}ms [{}]",
                    Arrays.toString(joinPoint.getArgs()),
                    joinPoint.getSignature().getName(),
                    executionTime,
                    traceId
                );
            }
            throw e;
        }
    }

    /**
     * Advice pour logger les exceptions
     */
    @AfterThrowing(pointcut = "applicationPackagePointcut() && springBeanPointcut()", throwing = "e")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable e) {
        Logger logger = logger(joinPoint);
        String traceId = MDC.get("traceId");
        if (traceId == null) {
            traceId = generateTraceId();
        }

        if (useJsonFormat) {
            Map<String, Object> logError = new HashMap<>();
            logError.put("eventType", isRestController(joinPoint) ? "API_CALL_EXCEPTION" : "SERVICE_CALL_EXCEPTION");
            logError.put("className", joinPoint.getSignature().getDeclaringTypeName());
            logError.put("methodName", joinPoint.getSignature().getName());
            logError.put("traceId", traceId);
            logError.put("timestamp", System.currentTimeMillis());
            logError.put("success", false);
            logError.put("exceptionType", e.getClass().getSimpleName());
            logError.put("exceptionMessage", e.getMessage());

            // Ajouter des infos HTTP si c'est un REST controller
            if (isRestController(joinPoint)) {
                ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                HttpServletRequest request = attributes != null ? attributes.getRequest() : null;
                if (request != null) {
                    logError.put("httpMethod", request.getMethod());
                    logError.put("requestUri", request.getRequestURI());
                }
            }

            try {
                logger.error("EXCEPTION_THROWN {}", objectMapper.writeValueAsString(logError));
            } catch (Exception jsonException) {
                fallbackExceptionLog(joinPoint, e, logger, traceId);
            }
        } else {
            fallbackExceptionLog(joinPoint, e, logger, traceId);
        }
    }

    /**
     * Log d'erreur avec format structuré
     */
    private void logError(
        JoinPoint joinPoint,
        HttpServletRequest request,
        Exception e,
        long executionTime,
        String errorType,
        Logger logger,
        String traceId
    ) {
        if (useJsonFormat) {
            Map<String, Object> logError = createStructuredLogEntry(joinPoint, request, "API_CALL_ERROR");
            logError.put("traceId", traceId);
            logError.put("executionTimeMs", executionTime);
            logError.put("success", false);
            logError.put("errorType", errorType);
            logError.put("errorMessage", e.getMessage());

            try {
                logger.error("API_CALL_ERROR {}", objectMapper.writeValueAsString(logError));
            } catch (Exception jsonException) {
                logger.error(
                    "API Call Failed: {}.{}() - {} after {}ms [{}]",
                    joinPoint.getSignature().getDeclaringTypeName(),
                    joinPoint.getSignature().getName(),
                    e.getMessage(),
                    executionTime,
                    traceId
                );
            }
        } else {
            logger.error(
                "API Call Failed: {}.{}() - {} after {}ms [{}]",
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(),
                e.getMessage(),
                executionTime,
                traceId
            );
        }
    }

    /**
     * Log d'exception au format classique (fallback)
     */
    private void fallbackExceptionLog(JoinPoint joinPoint, Throwable e, Logger logger, String traceId) {
        if (env.acceptsProfiles(Profiles.of(JHipsterConstants.SPRING_PROFILE_DEVELOPMENT))) {
            logger.error(
                "Exception in {}() with cause = '{}' and exception = '{}' [{}]",
                joinPoint.getSignature().getName(),
                e.getCause() != null ? e.getCause() : "NULL",
                e.getMessage(),
                traceId,
                e
            );
        } else {
            logger.error(
                "Exception in {}() with cause = {} [{}]",
                joinPoint.getSignature().getName(),
                e.getCause() != null ? String.valueOf(e.getCause()) : "NULL",
                traceId
            );
        }
    }

    /**
     * Vérifie si le JoinPoint correspond à un REST Controller
     */
    private boolean isRestController(JoinPoint joinPoint) {
        return joinPoint
            .getSignature()
            .getDeclaringType()
            .isAnnotationPresent(org.springframework.web.bind.annotation.RestController.class);
    }

    /**
     * Crée une entrée de log structurée pour les API calls
     */
    private Map<String, Object> createStructuredLogEntry(JoinPoint joinPoint, HttpServletRequest request, String eventType) {
        Map<String, Object> logEntry = new HashMap<>();

        // Informations sur la méthode
        logEntry.put("eventType", eventType);
        logEntry.put("className", joinPoint.getSignature().getDeclaringTypeName());
        logEntry.put("methodName", joinPoint.getSignature().getName());
        logEntry.put("timestamp", System.currentTimeMillis());

        // Informations sur la requête HTTP si disponible
        if (request != null) {
            logEntry.put("httpMethod", request.getMethod());
            logEntry.put("requestUri", request.getRequestURI());
            logEntry.put("remoteAddr", getClientIpAddress(request));
            logEntry.put("userAgent", request.getHeader("User-Agent"));
            logEntry.put("contentType", request.getContentType());

            // Paramètres de requête (sans les valeurs sensibles)
            Map<String, String[]> params = request.getParameterMap();
            if (!params.isEmpty()) {
                Map<String, Object> filteredParams = new HashMap<>();
                params.forEach((key, values) -> {
                    if (!isSensitiveParameter(key)) {
                        filteredParams.put(key, values.length == 1 ? values[0] : Arrays.toString(values));
                    } else {
                        filteredParams.put(key, "[FILTERED]");
                    }
                });
                logEntry.put("requestParams", filteredParams);
            }
        }

        // Arguments de la méthode (filtrage des données sensibles)
        Object[] args = joinPoint.getArgs();
        if (args != null && args.length > 0) {
            logEntry.put("methodArgsCount", args.length);
            // On ne log pas le contenu des arguments pour éviter les données sensibles
        }

        return logEntry;
    }

    /**
     * Récupère l'adresse IP réelle du client
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }

        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }

        return request.getRemoteAddr();
    }

    /**
     * Vérifie si un paramètre contient des données sensibles
     */
    private boolean isSensitiveParameter(String paramName) {
        String lowerParam = paramName.toLowerCase();
        return (
            lowerParam.contains("password") ||
            lowerParam.contains("token") ||
            lowerParam.contains("secret") ||
            lowerParam.contains("key") ||
            lowerParam.contains("auth")
        );
    }

    /**
     * Génère un ID de trace unique pour suivre les logs liés
     */
    private String generateTraceId() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }
}
