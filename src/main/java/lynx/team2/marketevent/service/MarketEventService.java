package lynx.team2.marketevent.service;


import lynx.team2.marketevent.model.dto.EventTriggerRequest;
import lynx.team2.marketevent.model.entity.MarketEvent;
import lynx.team2.marketevent.model.enums.TriggeredBy;

import java.util.List;

public interface MarketEventService {
    /**
     * Validates, persists, and broadcasts a new market event.
     *
     * @return the persisted {@link MarketEvent}, including the generated event_id and triggered_at.
     * @throws lynx.team2.marketevent.exception.InvalidEventException  if the request violates §7.1 rules
     * @throws lynx.team2.marketevent.exception.EventConflictException if a market-wide event is already active
     * @throws lynx.team2.marketevent.exception.EventPublishException  if the broadcast to Kafka fails (transaction is rolled back)
     */
    MarketEvent triggerEvent(EventTriggerRequest request, TriggeredBy triggeredBy);

    /**
     * @return the 50 most recently triggered events, ordered by triggered_at descending.
     *         Includes both ACTIVE and EXPIRED events (consumers filter by the
     *         {@code status} field on the response DTO).
     */
    List<MarketEvent> getRecentEvents();
}
