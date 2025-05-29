package com.senprojectbackend1.config;

import com.senprojectbackend1.aop.logging.LoggingAspect;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import tech.jhipster.config.JHipsterConstants;

@Configuration
@EnableAspectJAutoProxy
public class LoggingAspectConfiguration {

    /**
     * Aspect de logging amélioré qui combine :
     * - Logs structurés JSON pour les API calls (REST Controllers)
     * - Logs classiques pour les services et repositories
     * - Gestion des exceptions avec format approprié selon le contexte
     * Actif en dev et prod pour avoir les logs dans Kibana
     */
    @Bean
    @Profile(JHipsterConstants.SPRING_PROFILE_DEVELOPMENT)
    public LoggingAspect loggingAspect(Environment env) {
        return new LoggingAspect(env);
    }
}
