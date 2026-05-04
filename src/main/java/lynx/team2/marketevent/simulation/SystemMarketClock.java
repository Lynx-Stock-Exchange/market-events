package lynx.team2.marketevent.simulation;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;

/**
 * Default {@link MarketClock} for development and stand-alone runs.
 *
 * <p>Returns wall-clock time and counts ticks from service start at the
 * configured tick interval. This is a placeholder until the price-simulation
 * engine is integrated — at that point this bean should be replaced by an
 * impl that reads the engine's authoritative clock.
 */
@Component
public class SystemMarketClock implements MarketClock {

    private final long tickIntervalMs;
    private final Instant startedAt;

    public SystemMarketClock(@Value("${market.simulation.tick-interval-ms:1000}") long tickIntervalMs) {
        this.tickIntervalMs = tickIntervalMs;
        this.startedAt = Instant.now();
    }

    @Override
    public LocalDateTime nowSimulated() {
        return LocalDateTime.now();
    }

    @Override
    public long currentTick() {
        return Duration.between(startedAt, Instant.now()).toMillis() / tickIntervalMs;
    }
}
