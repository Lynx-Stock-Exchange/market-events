package lynx.team2.marketevent.model.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lynx.team2.marketevent.model.enums.EventScope;
import lynx.team2.marketevent.model.enums.EventType;

public class EventTriggerRequest {

    @NotNull
    private EventType event_type;

    @NotNull
    private EventScope scope;

    @Size(max = 32)
    private String target;

    @NotNull
    @DecimalMin(value = "0.1")
    private Double magnitude;

    @NotNull
    @Min(1)
    private Integer duration_ticks;

    public EventTriggerRequest() {
    }

    public EventTriggerRequest(EventType event_type,
                               EventScope scope,
                               String target,
                               Double magnitude,
                               Integer duration_ticks) {
        this.event_type = event_type;
        this.scope = scope;
        this.target = target;
        this.magnitude = magnitude;
        this.duration_ticks = duration_ticks;
    }

    public EventType getEvent_type() {
        return event_type;
    }

    public void setEvent_type(EventType event_type) {
        this.event_type = event_type;
    }

    public EventScope getScope() {
        return scope;
    }

    public void setScope(EventScope scope) {
        this.scope = scope;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public Double getMagnitude() {
        return magnitude;
    }

    public void setMagnitude(Double magnitude) {
        this.magnitude = magnitude;
    }

    public Integer getDuration_ticks() {
        return duration_ticks;
    }

    public void setDuration_ticks(Integer duration_ticks) {
        this.duration_ticks = duration_ticks;
    }
}