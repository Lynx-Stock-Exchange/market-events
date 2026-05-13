package lynx.team2.marketevent.simulation;

import java.time.LocalDateTime;

/**
 * Source of simulated market time and tick numbers.
 *
 * <p>Spec §6.1: a tick is 1 simulated minute. Simulated time advances at
 * {@code speed_multiplier × tick_interval} per real-time second.
 *
 * <p>This interface is the integration seam for the price-simulation engine.
 * The default implementation ({@link SystemMarketClock}) approximates
 * simulated time as wall-clock time, which is correct only when no engine
 * is running. Once the engine exists, swap in an implementation that reads
 * its authoritative clock (Redis pub/sub, REST call to {@code /market/status},
 * Kafka tick stream, etc.).
 */
public interface MarketClock {

    /**
     * @return the current simulated market time
     *         (engine-driven once integrated, wall-clock until then).
     */
    LocalDateTime nowSimulated();

    /**
     * @return the number of simulation ticks elapsed since the simulation started.
     *         Useful for tick-driven expiration and per-tick auto-trigger probabilities.
     */
    long currentTick();
}
