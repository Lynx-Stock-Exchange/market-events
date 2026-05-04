package lynx.team2.marketevent.model.enums;

/**
 * Market event taxonomy.
 *
 * <p>Per spec §7.1, each event type has a fixed scope:
 * <ul>
 *   <li>{@link #BULL_RUN}, {@link #BEAR_CRASH} — market-wide</li>
 *   <li>{@link #SECTOR_BOOM}, {@link #SECTOR_SLUMP} — sector-scoped</li>
 *   <li>{@link #STOCK_SHOCK} — single stock</li>
 * </ul>
 * The mapping is exposed via {@link #requiredScope()} so it can be enforced once,
 * both for admin-triggered and system-triggered events.
 */
public enum EventType {
    BULL_RUN(EventScope.MARKET),
    BEAR_CRASH(EventScope.MARKET),
    SECTOR_BOOM(EventScope.SECTOR),
    SECTOR_SLUMP(EventScope.SECTOR),
    STOCK_SHOCK(EventScope.STOCK);

    private final EventScope requiredScope;

    EventType(EventScope requiredScope) {
        this.requiredScope = requiredScope;
    }

    public EventScope requiredScope() {
        return requiredScope;
    }
}
