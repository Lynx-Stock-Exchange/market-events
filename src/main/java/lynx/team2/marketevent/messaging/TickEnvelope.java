package lynx.team2.marketevent.messaging;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Placeholder schema for messages on the {@code market.ticks} Kafka topic.
 *
 * <p><b>TODO(market-events):</b> confirm the topic name and message schema with the
 * price-simulation team. Best guess based on the spec §5.2 envelope shape:
 * <pre>
 *   {
 *     "type": "TICK",
 *     "payload": {
 *       "tick": 12345,
 *       "market_time": "2026-05-04T10:15:00",
 *       "is_open": true
 *     }
 *   }
 * </pre>
 *
 * <p>{@code @JsonIgnoreProperties(ignoreUnknown = true)} so unexpected fields
 * (very likely until the schema is finalised) don't fail deserialisation.
 */
@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TickEnvelope {

    private String type;
    private Payload payload;

    @Data
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Payload {

        private Long tick;

        @JsonProperty("market_time")
        private String marketTime;

        @JsonProperty("is_open")
        private Boolean isOpen;
    }
}
