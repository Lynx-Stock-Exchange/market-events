package lynx.team2.marketevent.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lynx.team2.marketevent.model.dto.EventTriggerRequest;
import lynx.team2.marketevent.model.enums.EventScope;
import lynx.team2.marketevent.model.enums.EventType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventAutoScheduler {

    private final MarketEventService marketEventService;
    private final Random random = new Random();

    @Value("${market.simulation.available-sectors}")
    private List<String> availableSectors;

    @Value("${market.simulation.available-stocks}")
    private List<String> availableStocks;

    @Scheduled(fixedRate = 60000)
    public void rollTheDiceForRandomEvent() {
        int diceRoll = random.nextInt(100);

        if (diceRoll < 10) {
            log.info("The dice rolled in our favor! Generating an automatic market event...");
            EventTriggerRequest randomRequest = generateRandomRequest();

            try {
                marketEventService.triggerEvent(randomRequest);
                log.info("Automatic event successfully dispatched to the Service.");
            } catch (Exception e) {
                log.error("Failed to dispatch automatic event: {}", e.getMessage());
            }
        }
    }

    private EventTriggerRequest generateRandomRequest() {
        EventTriggerRequest request = new EventTriggerRequest();

        EventScope randomScope = EventScope.values()[random.nextInt(EventScope.values().length)];
        EventType randomType = EventType.values()[random.nextInt(EventType.values().length)];

        request.setScope(randomScope);
        request.setEvent_type(randomType);

        String target = switch (randomScope) {
            case MARKET -> null;
            case SECTOR -> availableSectors.get(random.nextInt(availableSectors.size()));
            case STOCK -> availableStocks.get(random.nextInt(availableStocks.size()));
        };
        request.setTarget(target);

        double rawMagnitude = 1.0 + (random.nextDouble() * 2);
        request.setMagnitude(Math.round(rawMagnitude * 10.0) / 10.0);
        request.setDuration_ticks(10 + random.nextInt(31));

        return request;
    }
}