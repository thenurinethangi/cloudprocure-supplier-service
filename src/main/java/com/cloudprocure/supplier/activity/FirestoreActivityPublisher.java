package com.cloudprocure.supplier.activity;

import com.google.cloud.Timestamp;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
@Profile({"prod", "gcp"})
public class FirestoreActivityPublisher implements ActivityPublisher {
    private final Firestore firestore;
    public FirestoreActivityPublisher(@Value("${cloudprocure.activity.project-id}") String projectId,
                                      @Value("${cloudprocure.activity.database-id:(default)}") String databaseId) {
        firestore = FirestoreOptions.newBuilder().setProjectId(projectId).setDatabaseId(databaseId).build().getService();
    }
    @Override public void publish(ActivityEvent event) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("id", event.id().toString()); data.put("service", event.service());
        data.put("eventType", event.eventType()); data.put("entityType", event.entityType());
        data.put("entityId", event.entityId()); data.put("actor", event.actor());
        data.put("summary", event.summary()); data.put("metadata", event.metadata());
        data.put("occurredAt", Timestamp.ofTimeSecondsAndNanos(event.occurredAt().getEpochSecond(), event.occurredAt().getNano()));
        try { firestore.collection("activity_events").document(event.id().toString()).set(data).get(); }
        catch (Exception exception) { throw new IllegalStateException("Could not publish the Firestore activity event", exception); }
    }
}
