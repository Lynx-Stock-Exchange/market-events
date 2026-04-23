package lynx.team2.marketevent.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MarketEventPayload {

    @JsonProperty("event_id")
    private String eventId;

    // TODO: De schimbat in EventType dupa ce Luiza termina Fundatia
    @JsonProperty("event_type")
    private String eventType;

    @JsonProperty("headline")
    private String headline;

    // TODO: De schimbat in EventScope dupa ce Luiza termina Fundatia
    @JsonProperty("scope")
    private String scope;

    @JsonProperty("target")
    private String target;

    @JsonProperty("magnitude")
    private Double magnitude;

    @JsonProperty("duration_ticks")
    private Integer durationTicks;

    @JsonProperty("market_time")
    private String marketTime;
}