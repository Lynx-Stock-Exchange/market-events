package lynx.team2.marketevent.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lynx.team2.marketevent.model.enums.EventType;
import lynx.team2.marketevent.simulation.seed.SeedLoader;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Picks a random headline for a given event type, drawing from the headlines
 * loaded by {@link SeedLoader} (spec §7.3 / §9 — {@code events.headlines}).
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class HeadlineSelector {

    private static final String FALLBACK_HEADLINE = "Unexpected market event detected!";

    private final SeedLoader seedLoader;

    public String getRandomHeadline(EventType eventType) {
        Map<EventType, List<String>> headlines = seedLoader.events().getHeadlines();
        List<String> available = headlines == null ? null : headlines.get(eventType);

        if (available == null || available.isEmpty()) {
            log.warn("No headlines configured for event type {} — using fallback.", eventType);
            return FALLBACK_HEADLINE;
        }
        return available.get(ThreadLocalRandom.current().nextInt(available.size()));
    }
}
