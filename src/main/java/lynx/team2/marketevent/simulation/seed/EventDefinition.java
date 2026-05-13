package lynx.team2.marketevent.simulation.seed;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lynx.team2.marketevent.model.enums.EventScope;
import lynx.team2.marketevent.model.enums.EventType;

/**
 * One concrete event template defined in the seed file (spec §9).
 * Each definition has a fixed type/scope/target/magnitude/duration plus a weight
 * used by the auto-trigger to pick definitions probabilistically.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class EventDefinition {

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

    @JsonProperty("weight")
    private Double weight;
}
