package lynx.team2.marketevent.service;


import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Slf4j
@Component
@RequiredArgsConstructor
public class HeadlineSelector {

    private final ObjectMapper objectMapper;
    private Map<String, List<String>> headlines = new HashMap<>();
    private final Random random = new Random();

    @PostConstruct
    public void loadHeadlinesFromFile() {
        try {
            InputStream inputStream = new ClassPathResource("event_headlines.json").getInputStream();

            Map<String, Map<String, List<String>>> rootNode = objectMapper.readValue(
                    inputStream,
                    new TypeReference<Map<String, Map<String, List<String>>>>() {}
            );

            headlines = rootNode.get("event_headlines");

            log.info("Successfully loaded market event headlines from JSON!");
        } catch (Exception e) {
            log.error("Severe error: Could not load event_headlines.json file", e);
        }
    }

    public String getRandomHeadline(String eventType) {
        List<String> availableHeadlines = headlines.get(eventType);

        if (availableHeadlines == null || availableHeadlines.isEmpty()) {
            return "Unexpected market event detected!";
        }

        int randomIndex = random.nextInt(availableHeadlines.size());
        return availableHeadlines.get(randomIndex);
    }
}