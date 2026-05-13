package lynx.team2.marketevent.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lynx.team2.marketevent.model.enums.EventScope;
import lynx.team2.marketevent.model.enums.EventType;

@Data
@Builder
public class MarketEventPayload {

    @JsonProperty("event_id")
    private String eventId;

    @JsonProperty("event_type")
    private EventType eventType;

    @JsonProperty("headline")
    private String headline;

    @JsonProperty("scope")
    private EventScope scope;

    @JsonProperty("target")
    private String target;

    @JsonProperty("magnitude")
    private Double magnitude;

    @JsonProperty("duration_ticks")
    private Integer durationTicks;

    @JsonProperty("market_time")
    private String marketTime;
}