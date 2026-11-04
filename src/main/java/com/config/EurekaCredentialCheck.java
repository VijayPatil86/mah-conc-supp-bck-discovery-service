package com.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class EurekaCredentialCheck implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(EurekaCredentialCheck.class);

    private static final String PASSWORD_PROPERTY = "spring.security.user.password";

    private final Environment env;
    private final int minLength;

    public EurekaCredentialCheck(Environment env,
                                 @Value("${app.min-password-length:6}") int minLength) {
        this.env = env;
        this.minLength = minLength;
    }

    @Override
    public void run(ApplicationArguments args) {
        // the value Spring Security really uses: command line > environment variable > yaml
        String effective = env.getProperty(PASSWORD_PROPERTY);
        // the environment variable, read on its own
        String fromEnvVar = System.getenv("EUREKA_PASSWORD");

        if (effective == null || effective.isBlank()) {
            throw new IllegalStateException(
                "No Eureka password is set. Set the EUREKA_PASSWORD environment variable "
                + "or pass --" + PASSWORD_PROPERTY + "=...");
        }
        if (effective.length() < minLength) {
            throw new IllegalStateException(
                "The Eureka password must be at least " + minLength + " characters "
                + "(it is " + effective.length() + ")");
        }

        // a command-line value silently beats the environment variable, which confuses people
        if (fromEnvVar != null && !fromEnvVar.isBlank() && !fromEnvVar.equals(effective)) {
            log.warn("EUREKA_PASSWORD and --{} are both set and differ. "
                   + "The command-line value is being used.", PASSWORD_PROPERTY);
        }

        // report the source and length only, never the value
        String source = (fromEnvVar != null && fromEnvVar.equals(effective))
                ? "EUREKA_PASSWORD environment variable"
                : "command line or configuration file";
        log.info("Eureka password accepted (source: {}, length: {})", source, effective.length());
    }
}