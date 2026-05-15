package lynx.team2.marketevent.simulation.seed;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.Collections;

/**
 * Loads the simulation seed file (spec §9) at startup and exposes the
 * {@code events} block to the rest of the service.
 *
 * <p>Path is configurable via {@code market.simulation.seed-file}
 * (default: {@code classpath:seed.json}).
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SeedLoader {

    private final ObjectMapper objectMapper;

    @Value("${market.simulation.seed-file:classpath:seed.json}")
    private String seedFilePath;

    private volatile EventsSeed events = new EventsSeed(false, 0.0, Collections.emptyList(), Collections.emptyMap());

    @PostConstruct
    public void load() {
        Resource resource = resolve(seedFilePath);
        if (!resource.exists()) {
            log.error("Seed file {} not found — auto-trigger and headlines will be empty.", seedFilePath);
            return;
        }
        try (InputStream in = resource.getInputStream()) {
            Seed seed = objectMapper.readValue(in, Seed.class);
            if (seed.getEvents() != null) {
                this.events = seed.getEvents();
            }
            log.info("Loaded seed from {}: {} definitions, {} headline groups, auto-trigger={}",
                    seedFilePath,
                    events.getDefinitions() == null ? 0 : events.getDefinitions().size(),
                    events.getHeadlines() == null ? 0 : events.getHeadlines().size(),
                    Boolean.TRUE.equals(events.getAutoTriggerEnabled()));
        } catch (Exception e) {
            log.error("Failed to parse seed file {} — auto-trigger and headlines will be empty.", seedFilePath, e);
        }
    }

    public EventsSeed events() {
        return events;
    }

    public void setEvents(EventsSeed newEvents) {
        this.events = newEvents;
        log.info("Event config updated dynamically: {} definitions, {} headline groups, auto-trigger={}",
                newEvents.getDefinitions() == null ? 0 : newEvents.getDefinitions().size(),
                newEvents.getHeadlines() == null ? 0 : newEvents.getHeadlines().size(),
                Boolean.TRUE.equals(newEvents.getAutoTriggerEnabled()));
    }

    private Resource resolve(String path) {
        if (path.startsWith("classpath:")) {
            return new ClassPathResource(path.substring("classpath:".length()));
        }
        // file:/abs/path/seed.json or /abs/path/seed.json
        return new org.springframework.core.io.FileSystemResource(
                path.startsWith("file:") ? path.substring("file:".length()) : path);
    }
}
