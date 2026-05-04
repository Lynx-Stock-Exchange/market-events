package lynx.team2.marketevent.simulation.seed;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lynx.team2.marketevent.model.enums.EventType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Spec §9 — the {@code events} block of the seed file.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class EventsSeed {

    @JsonProperty("auto_trigger_enabled")
    @Builder.Default
    private Boolean autoTriggerEnabled = Boolean.FALSE;

    @JsonProperty("auto_trigger_probability_per_tick")
    @Builder.Default
    private Double autoTriggerProbabilityPerTick = 0.0;

    @JsonProperty("definitions")
    @Builder.Default
    private List<EventDefinition> definitions = new ArrayList<>();

    @JsonProperty("headlines")
    @Builder.Default
    private Map<EventType, List<String>> headlines = new HashMap<>();
}
