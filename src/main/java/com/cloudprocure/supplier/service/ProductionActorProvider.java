package com.cloudprocure.supplier.service;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component @Profile({"prod", "gcp"})
public class ProductionActorProvider implements ActorProvider {
    @Override public String currentActorEmail() {
        if (RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attributes
                && attributes.getRequest().getUserPrincipal() != null) {
            return attributes.getRequest().getUserPrincipal().getName();
        }
        return "service@cloudprocure.internal";
    }
}
