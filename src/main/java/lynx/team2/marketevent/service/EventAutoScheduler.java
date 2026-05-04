package lynx.team2.marketevent.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lynx.team2.marketevent.model.dto.EventTriggerRequest;
import lynx.team2.marketevent.model.enums.TriggeredBy;
import lynx.team2.marketevent.simulation.seed.EventDefinition;
import lynx.team2.marketevent.simulation.seed.EventsSeed;
import lynx.team2.marketevent.simulation.seed.SeedLoader;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Spec §7.2 — automatic event triggering.
 *
 * <p>Every tick (at {@code market.simulation.tick-interval-ms}) the scheduler
 * rolls a die. If the roll is below {@code auto_trigger_probability_per_tick}
 * (from the seed file), it picks a definition weighted by {@code weight} and
 * triggers it as {@link TriggeredBy#SYSTEM}.
 *
 * <p>This polls at the tick rate as a stand-in for true tick-driven behaviour;
 * once the price-simulation engine is integrated, this scheduler should be
 * replaced by a Kafka listener on the engine's tick stream so that ticks pause
 * during market closure (spec §6.4).
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EventAutoScheduler {

    private final MarketEventService marketEventService;
    private final SeedLoader seedLoader;

    @Scheduled(fixedRateString = "${market.simulation.tick-interval-ms:1000}")
    public void rollOnTick() {
        EventsSeed events = seedLoader.events();
        if (!Boolean.TRUE.equals(events.getAutoTriggerEnabled())) {
            return;
        }
        double probability = events.getAutoTriggerProbabilityPerTick() == null
                ? 0.0
                : events.getAutoTriggerProbabilityPerTick();

        if (probability <= 0.0 || ThreadLocalRandom.current().nextDouble() >= probability) {
            return;
        }

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
        if (definitions == null || definitions.isEmpty()) {
            return null;
        }

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
            if (roll < cumulative) {
                return def;
            }
        }
        return definitions.get(definitions.size() - 1);
    }
}
