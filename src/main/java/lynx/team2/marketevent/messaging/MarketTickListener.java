package lynx.team2.marketevent.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Consumer for the {@code market.ticks} Kafka topic — the tick stream from
 * the price-simulation engine (see architecture diagram).
 *
 * <p>For now this listener only stores the latest tick + market_time + is_open
 * so other components can read engine state. The actual wiring of:
 * <ul>
 *   <li>auto-trigger dice-roll → fire on each tick instead of fixed-rate polling</li>
 *   <li>event expiration → compare {@code current_tick} with stored {@code expires_at_tick}</li>
 *   <li>{@link lynx.team2.marketevent.simulation.MarketClock} → read engine clock instead of wall-clock</li>
 * </ul>
 * is deferred until the message schema is confirmed with the price-simulation team.
 *
 * <p><b>TODO(market-events):</b> after schema confirmation:
 * <ol>
 *   <li>Inject this listener into {@link lynx.team2.marketevent.service.EventAutoScheduler}
 *       and call its dice-roll on each tick (delete the {@code @Scheduled} fallback).</li>
 *   <li>Inject into {@link lynx.team2.marketevent.service.EventExpirationScheduler}
 *       and switch to tick-driven expiration.</li>
 *   <li>Replace {@link lynx.team2.marketevent.simulation.SystemMarketClock} with a
 *       {@code TickAwareMarketClock} backed by the values cached here.</li>
 * </ol>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MarketTickListener {

    private final ObjectMapper objectMapper;

    private final AtomicLong latestTick = new AtomicLong(-1);
    private final AtomicReference<String> latestMarketTime = new AtomicReference<>();
    private final AtomicReference<Boolean> latestIsOpen = new AtomicReference<>();

    @KafkaListener(
            topics = "${market.kafka.tick-topic:market.ticks}",
            groupId = "${market.kafka.tick-group:market-events-tick}"
    )
    public void onTick(String json) {
        try {
            TickEnvelope envelope = objectMapper.readValue(json, TickEnvelope.class);
            TickEnvelope.Payload payload = envelope == null ? null : envelope.getPayload();
            if (payload == null) {
                log.warn("Received tick with empty payload: {}", json);
                return;
            }
            if (payload.getTick() != null) {
                latestTick.set(payload.getTick());
            }
            if (payload.getMarketTime() != null) {
                latestMarketTime.set(payload.getMarketTime());
            }
            if (payload.getIsOpen() != null) {
                latestIsOpen.set(payload.getIsOpen());
            }
            log.debug("Tick received: tick={} market_time={} is_open={}",
                    payload.getTick(), payload.getMarketTime(), payload.getIsOpen());
        } catch (Exception e) {
            log.error("Failed to parse tick message: {}", json, e);
        }
    }

    /** @return the latest known tick number, or -1 if no tick has been received. */
    public long getLatestTick() {
        return latestTick.get();
    }

    /** @return the latest known simulated market time, or null if no tick has been received. */
    public String getLatestMarketTime() {
        return latestMarketTime.get();
    }

    /** @return whether the simulation reported "open" on the most recent tick. */
    public Boolean getLatestIsOpen() {
        return latestIsOpen.get();
    }
}
