package lynx.team2.marketevent.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lynx.team2.marketevent.model.dto.EventTriggerRequest;
import lynx.team2.marketevent.model.enums.EventScope;
import lynx.team2.marketevent.model.enums.EventType;
import lynx.team2.marketevent.model.enums.TriggeredBy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Random;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventAutoScheduler {

    private final MarketEventService marketEventService;
    private final Random random = new Random();

    @Value("${market.simulation.event-probability-percent}")
    private int probabilityPercent;

    @Value("${market.simulation.magnitude-min}")
    private double magnitudeMin;

    @Value("${market.simulation.magnitude-max}")
    private double magnitudeMax;

    @Value("${market.simulation.duration-min-ticks}")
    private int durationMin;

    @Value("${market.simulation.duration-max-ticks}")
    private int durationMax;

    @Value("${market.simulation.available-sectors}")
    private List<String> availableSectors;

    @Value("${market.simulation.available-stocks}")
    private List<String> availableStocks;

    // Weights per event type, matching spec seed format. Order matches EventType enum values.
    private static final Map<EventType, Double> EVENT_WEIGHTS = Map.of(
            EventType.BULL_RUN,     1.0,
            EventType.BEAR_CRASH,   1.0,
            EventType.SECTOR_BOOM,  1.5,
            EventType.SECTOR_SLUMP, 1.5,
            EventType.STOCK_SHOCK,  2.0
    );

    @Scheduled(fixedRateString = "${market.simulation.auto-scheduler-rate}")
    public void rollTheDiceForRandomEvent() {
        int diceRoll = random.nextInt(100);

        if (diceRoll < probabilityPercent) {
            log.info("The dice rolled in our favor! Generating an automatic market event...");
            EventTriggerRequest randomRequest = generateRandomRequest();

            try {
                marketEventService.triggerEvent(randomRequest, TriggeredBy.SYSTEM);
                log.info("Automatic event successfully dispatched to the Service.");
            } catch (Exception e) {
                log.error("Failed to dispatch automatic event: {}", e.getMessage());
            }
        }
    }

    private EventTriggerRequest generateRandomRequest() {
        EventTriggerRequest request = new EventTriggerRequest();

        EventScope randomScope = EventScope.values()[random.nextInt(EventScope.values().length)];
        request.setEvent_type(pickWeightedEventType());
        request.setScope(randomScope);

        String target = switch (randomScope) {
            case MARKET -> null;
            case SECTOR -> availableSectors.get(random.nextInt(availableSectors.size()));
            case STOCK -> availableStocks.get(random.nextInt(availableStocks.size()));
        };
        request.setTarget(target);

        double rawMagnitude = magnitudeMin + (random.nextDouble() * (magnitudeMax - magnitudeMin));
        request.setMagnitude(Math.round(rawMagnitude * 10.0) / 10.0);

        request.setDuration_ticks(durationMin + random.nextInt((durationMax - durationMin) + 1));

        return request;
    }

    private EventType pickWeightedEventType() {
        double totalWeight = EVENT_WEIGHTS.values().stream().mapToDouble(Double::doubleValue).sum();
        double roll = random.nextDouble() * totalWeight;

        double cumulative = 0.0;
        for (Map.Entry<EventType, Double> entry : EVENT_WEIGHTS.entrySet()) {
            cumulative += entry.getValue();
            if (roll < cumulative) {
                return entry.getKey();
            }
        }

        return EventType.STOCK_SHOCK;
    }
}
