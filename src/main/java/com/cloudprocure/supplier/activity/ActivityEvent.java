package com.cloudprocure.supplier.activity;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record ActivityEvent(UUID id, String service, String eventType, String entityType, String entityId,
                            String actor, String summary, Map<String, Object> metadata, Instant occurredAt) {
    public ActivityEvent { metadata = metadata == null ? Map.of() : Map.copyOf(metadata); }
}
