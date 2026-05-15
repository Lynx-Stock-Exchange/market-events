package lynx.team2.marketevent.controller;

import lynx.team2.marketevent.simulation.seed.EventsSeed;
import lynx.team2.marketevent.simulation.seed.SeedLoader;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Receives dynamic event config from the REST API gateway and updates the
 * in-memory seed so auto-trigger and headline selection use the new definitions.
 */
@RestController
@RequestMapping("/api/v1/internal/events")
public class AdminEventsConfigController {

    private final SeedLoader seedLoader;

    public AdminEventsConfigController(SeedLoader seedLoader) {
        this.seedLoader = seedLoader;
    }

    @PostMapping("/config")
    public ResponseEntity<Map<String, Object>> updateConfig(@RequestBody EventsSeed config) {
        seedLoader.setEvents(config);
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "Event config updated.");
        response.put("definitions", config.getDefinitions() == null ? 0 : config.getDefinitions().size());
        response.put("headline_groups", config.getHeadlines() == null ? 0 : config.getHeadlines().size());
        return ResponseEntity.ok(response);
    }
}
