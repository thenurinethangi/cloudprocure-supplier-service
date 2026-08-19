package com.cloudprocure.supplier.activity;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("test")
public class InMemoryActivityPublisher implements ActivityPublisher {
    @Override public void publish(ActivityEvent event) { }
}
