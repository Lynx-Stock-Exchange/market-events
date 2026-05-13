package lynx.team2.marketevent.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lynx.team2.marketevent.model.enums.EventScope;
import lynx.team2.marketevent.model.enums.EventType;

/**
 * Body of {@code POST /api/v1/admin/events/trigger} (spec §4.5).
 * Wire format is snake_case (matched via {@code @JsonProperty}); Java fields
 * follow Java naming conventions.
 */
public class EventTriggerRequest {

    @NotNull
    @JsonProperty("event_type")
    private EventType eventType;

    @NotNull
    @JsonProperty("scope")
    private EventScope scope;

    @Size(max = 32)
    @JsonProperty("target")
    private String target;

    @NotNull
    @DecimalMin(value = "0.1")
    @DecimalMax(value = "10.0")
    @JsonProperty("magnitude")
    private Double magnitude;

    @NotNull
    @Min(1)
    @JsonProperty("duration_ticks")
    private Integer durationTicks;

    public EventTriggerRequest() {
    }

    public EventTriggerRequest(EventType eventType,
                               EventScope scope,
                               String target,
                               Double magnitude,
                               Integer durationTicks) {
        this.eventType = eventType;
        this.scope = scope;
        this.target = target;
        this.magnitude = magnitude;
        this.durationTicks = durationTicks;
    }

    public EventType getEventType() { return eventType; }
    public void setEventType(EventType eventType) { this.eventType = eventType; }

    public EventScope getScope() { return scope; }
    public void setScope(EventScope scope) { this.scope = scope; }

    public String getTarget() { return target; }
    public void setTarget(String target) { this.target = target; }

    public Double getMagnitude() { return magnitude; }
    public void setMagnitude(Double magnitude) { this.magnitude = magnitude; }

    public Integer getDurationTicks() { return durationTicks; }
    public void setDurationTicks(Integer durationTicks) { this.durationTicks = durationTicks; }
}
