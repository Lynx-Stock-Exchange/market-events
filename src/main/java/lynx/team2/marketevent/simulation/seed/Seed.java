package lynx.team2.marketevent.simulation.seed;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Top-level seed file (spec §9).
 *
 * <p>Only the {@code events} block is consumed by this microservice; the
 * {@code exchange}, {@code stocks}, and {@code options} sections belong to
 * other services and are deliberately ignored here ({@code @JsonIgnoreProperties}).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Seed {

    @JsonProperty("events")
    private EventsSeed events;
}
