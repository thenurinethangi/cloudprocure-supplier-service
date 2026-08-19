package com.cloudprocure.supplier.activity;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

@Component
@Profile({"local", "demo"})
public class LocalJsonlActivityPublisher implements ActivityPublisher {
    private final Path file;
    private final ObjectMapper mapper;
    public LocalJsonlActivityPublisher(@Value("${cloudprocure.activity.file:../../local-dev/data/activity-events.jsonl}") Path file,
                                       ObjectMapper mapper) {
        this.file = file.toAbsolutePath().normalize();
        this.mapper = mapper;
    }
    @Override public void publish(ActivityEvent event) {
        try {
            if (file.getParent() != null) Files.createDirectories(file.getParent());
            byte[] bytes = mapper.writeValueAsBytes(event);
            try (FileChannel channel = FileChannel.open(file, StandardOpenOption.CREATE, StandardOpenOption.WRITE,
                    StandardOpenOption.APPEND); FileLock ignored = channel.lock()) {
                channel.write(ByteBuffer.wrap(bytes));
                channel.write(ByteBuffer.wrap(new byte[]{'\n'}));
                channel.force(true);
            }
        } catch (Exception exception) {
            throw new IllegalStateException("Could not append the local activity event", exception);
        }
    }
}
