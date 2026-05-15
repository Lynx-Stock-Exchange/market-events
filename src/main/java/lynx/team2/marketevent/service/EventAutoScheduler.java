package lynx.team2.marketevent.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lynx.team2.marketevent.model.dto.EventTriggerRequest;
import lynx.team2.marketevent.model.enums.TriggeredBy;
import lynx.team2.marketevent.simulation.seed.EventDefinition;
import lynx.team2.marketevent.simulation.seed.EventsSeed;
import lynx.team2.marketevent.simulation.seed.SeedLoader;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Spec §7.2 — automatic event triggering driven by the stock.prices topic.
 *
 * A stock.prices message is published by price-simulation on every tick for
 * every active stock, so the market is running whenever these messages flow.
 * We deduplicate by market_time so the probability roll fires exactly once
 * per tick regardless of how many stocks are listed.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EventAutoScheduler {

    private final MarketEventService marketEventService;
    private final SeedLoader seedLoader;
    private final ObjectMapper objectMapper;

    private final AtomicReference<String> lastProcessedMarketTime = new AtomicReference<>("");

    @KafkaListener(
            topics = "${market.kafka.price-topic:stock.prices}",
            groupId = "${market.kafka.price-group:market-events-price}"
    )
    public void onPriceUpdate(String json) {
        try {
            JsonNode node = objectMapper.readTree(json);
            JsonNode marketTimeNode = node.get("market_time");
            if (marketTimeNode == null || marketTimeNode.isNull()) return;

            String marketTime = marketTimeNode.asText();

            // Deduplicate: only roll once per unique market_time tick.
            // CAS from the previous value to the new one — only the first
            // stock.prices message for a given tick wins; all others skip.
            String prev = lastProcessedMarketTime.get();
            if (prev.equals(marketTime)) return;
            if (lastProcessedMarketTime.compareAndSet(prev, marketTime)) {
                rollDice();
            }
        } catch (Exception e) {
            log.error("Failed to process stock.prices message for auto-trigger: {}", e.getMessage());
        }
    }

    private void rollDice() {
        EventsSeed events = seedLoader.events();
        if (!Boolean.TRUE.equals(events.getAutoTriggerEnabled())) return;

        double probability = events.getAutoTriggerProbabilityPerTick() == null
                ? 0.0
                : events.getAutoTriggerProbabilityPerTick();

        if (probability <= 0.0 || ThreadLocalRandom.current().nextDouble() >= probability) return;

        EventDefinition picked = pickWeighted(events.getDefinitions());
        if (picked == null) {
            log.warn("Auto-trigger fired but no definitions are configured in the seed.");
            return;
        }

        EventTriggerRequest request = new EventTriggerRequest(
                picked.getEventType(),
                picked.getScope(),
                picked.getTarget(),
                picked.getMagnitude(),
                picked.getDurationTicks()
        );

        try {
            marketEventService.triggerEvent(request, TriggeredBy.SYSTEM);
            log.info("Auto-triggered event: type={} scope={} target={}",
                    picked.getEventType(), picked.getScope(), picked.getTarget());
        } catch (Exception e) {
            log.error("Failed to dispatch automatic event ({}): {}", picked.getEventType(), e.getMessage());
        }
    }

    private EventDefinition pickWeighted(List<EventDefinition> definitions) {
        if (definitions == null || definitions.isEmpty()) return null;

        double total = definitions.stream()
                .mapToDouble(d -> d.getWeight() == null ? 0.0 : d.getWeight())
                .sum();
        if (total <= 0.0) {
            return definitions.get(ThreadLocalRandom.current().nextInt(definitions.size()));
        }

        double roll = ThreadLocalRandom.current().nextDouble() * total;
        double cumulative = 0.0;
        for (EventDefinition def : definitions) {
            cumulative += def.getWeight() == null ? 0.0 : def.getWeight();
            if (roll < cumulative) return def;
        }
        return definitions.get(definitions.size() - 1);
    }
}
