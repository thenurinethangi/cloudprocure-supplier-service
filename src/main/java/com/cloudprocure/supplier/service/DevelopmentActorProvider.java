package com.cloudprocure.supplier.service;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component @Profile("!prod & !gcp")
public class DevelopmentActorProvider implements ActorProvider {
    @Override public String currentActorEmail() {
        if (RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attributes) {
            String value = attributes.getRequest().getHeader("X-Actor-Email");
            if (value != null && !value.isBlank()) return value.trim().toLowerCase();
        }
        return "procurement.officer@example.com";
    }
}
