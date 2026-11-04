package com.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.netflix.eureka.server.event.EurekaInstanceCanceledEvent;
import org.springframework.cloud.netflix.eureka.server.event.EurekaInstanceRegisteredEvent;
import org.springframework.cloud.netflix.eureka.server.event.EurekaRegistryAvailableEvent;
import org.springframework.cloud.netflix.eureka.server.event.EurekaServerStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class RegistryEventLogger {

    private static final Logger log = LoggerFactory.getLogger("registry");

    @EventListener
    public void onRegistered(EurekaInstanceRegisteredEvent e) {
        log.info("REGISTERED app={} instanceId={} host={} port={} renewal={}",
                e.getInstanceInfo().getAppName(),
                e.getInstanceInfo().getInstanceId(),
                e.getInstanceInfo().getHostName(),
                e.getInstanceInfo().getPort(),
                e.isReplication() ? "replicated" : "direct");
    }

    @EventListener
    public void onCanceled(EurekaInstanceCanceledEvent e) {
        // fires for a clean shutdown and for an expired lease; the WARN line from
        // AbstractInstanceRegistry ("expired lease") marks the expired case
        log.warn("CANCELED app={} instanceId={}", e.getAppName(), e.getServerId());
    }

    @EventListener
    public void onRegistryAvailable(EurekaRegistryAvailableEvent e) {
        log.info("REGISTRY_AVAILABLE");
    }

    @EventListener
    public void onServerStarted(EurekaServerStartedEvent e) {
        log.info("EUREKA_SERVER_STARTED");
    }
}