package lynx.team2.marketevent.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lynx.team2.marketevent.model.entity.MarketEvent;
import lynx.team2.marketevent.model.enums.EventScope;
import lynx.team2.marketevent.model.enums.EventStatus;
import lynx.team2.marketevent.model.enums.EventType;
import lynx.team2.marketevent.model.enums.TriggeredBy;

import java.time.LocalDateTime;

/**
 * REST response shape for a market event. Field names match spec §3.6 exactly
 * (snake_case on the wire, camelCase in Java).
 *
 * <p>{@code status} is a service-internal extension (not in §3.6) — exposed
 * because consumers of {@code GET /market/events} benefit from knowing whether
 * an event is still in effect.
 */
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MarketEventResponse {

    @JsonProperty("event_id")
    private String eventId;

    @JsonProperty("event_type")
    private EventType eventType;

    @JsonProperty("scope")
    private EventScope scope;

    @JsonProperty("target")
    private String target;

    @JsonProperty("magnitude")
    private Double magnitude;

    @JsonProperty("duration_ticks")
    private Integer durationTicks;

    @JsonProperty("headline")
    private String headline;

    @JsonProperty("triggered_at")
    private LocalDateTime triggeredAt;

    @JsonProperty("triggered_by")
    private TriggeredBy triggeredBy;

    @JsonProperty("status")
    private EventStatus status;

    public static MarketEventResponse fromEntity(MarketEvent event) {
        return MarketEventResponse.builder()
                .eventId(event.getEventId())
                .eventType(event.getEventType())
                .scope(event.getScope())
                .target(event.getTarget())
                .magnitude(event.getMagnitude())
                .durationTicks(event.getDurationTicks())
                .headline(event.getHeadline())
                .triggeredAt(event.getTriggeredAt())
                .triggeredBy(event.getTriggeredBy())
                .status(event.getStatus())
                .build();
    }
}
