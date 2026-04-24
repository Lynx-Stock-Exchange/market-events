package lynx.team2.marketevent.model.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lynx.team2.marketevent.model.enums.EventScope;
import lynx.team2.marketevent.model.enums.EventStatus;
import lynx.team2.marketevent.model.enums.EventType;
import lynx.team2.marketevent.model.enums.TriggeredBy;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name="market_events")
public class MarketEvent {

    @Id
    @Column(name="event_id",nullable = false,updatable = false, length = 64)
    private String eventId;

    @Enumerated(EnumType.STRING)
    @Column(name="event_type",nullable = false,length = 32)
    @NotNull
    private EventType eventType;

    @Enumerated(EnumType.STRING)
    @Column(name="scope",nullable = false,length = 32)
    @NotNull
    private EventScope scope;

    @Column(name="target",length = 32)
    private String target;


    @Column(name="magnitude",nullable = false)
    @NotNull
    @DecimalMin(value="0.1")
    private Double magnitude;


    @Column(name="duration_ticks",nullable = false)
    @NotNull
    @Min(1)
    private Integer duration_ticks;

    @Column(name="headline",nullable = false,length = 255)
    @NotBlank
    private String headline;

    @Column(name = "triggered_at",nullable = false)
    @NotNull
    private LocalDateTime triggered_at;

    @Enumerated(EnumType.STRING)
    @Column(name = "triggered_by",nullable = false,length = 16)
    @NotNull
    private TriggeredBy triggered_by;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 16)
    @NotNull
    private EventStatus status;

    public MarketEvent() {}

    public MarketEvent(EventType event_type,EventScope scope,String target, Double magnitude, Integer duration_ticks, String headline, LocalDateTime triggered_at, TriggeredBy triggered_by) {
        this.eventType = event_type;
        this.scope = scope;
        this.target = target;
        this.magnitude = magnitude;
        this.duration_ticks = duration_ticks;
        this.headline = headline;
        this.triggered_at = triggered_at;
        this.triggered_by = triggered_by;
    }

    @PrePersist
    public void prePersist() {
        if (this.eventId == null || this.eventId.isBlank()) {
            this.eventId = UUID.randomUUID().toString();
        }
        if (this.triggered_at == null) {
            this.triggered_at = LocalDateTime.now();
        }
        if (this.status == null) {
            this.status = EventStatus.ACTIVE;
        }
    }
    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
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

    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public LocalDateTime getTriggered_at() {
        return triggered_at;
    }

    public void setTriggered_at(LocalDateTime triggered_at) {
        this.triggered_at = triggered_at;
    }

    public TriggeredBy getTriggered_by() {
        return triggered_by;
    }

    public void setTriggered_by(TriggeredBy triggered_by) {
        this.triggered_by = triggered_by;
    }

    public boolean isMarketWide() {
        return this.scope == EventScope.MARKET;
    }

    public EventStatus getStatus() {
        return status;
    }

    public void setStatus(EventStatus status) {
        this.status = status;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MarketEvent that)) return false;
        return Objects.equals(eventId, that.eventId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventId);
    }


}
