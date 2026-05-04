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

/**
 * Persistent record of a market event (spec §3.6 plus a service-internal
 * {@code status} field used by the expiration scheduler).
 *
 * <p>DB columns stay snake_case via {@code @Column(name=...)}; Java fields
 * are camelCase per Java conventions. Wire format mapping is the
 * responsibility of the DTOs (see {@code MarketEventResponse}).
 */
@Entity
@Table(name = "market_events")
public class MarketEvent {

    @Id
    @Column(name = "event_id", nullable = false, updatable = false, length = 64)
    private String eventId;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false, length = 32)
    @NotNull
    private EventType eventType;

    @Enumerated(EnumType.STRING)
    @Column(name = "scope", nullable = false, length = 32)
    @NotNull
    private EventScope scope;

    @Column(name = "target", length = 32)
    private String target;

    @Column(name = "magnitude", nullable = false)
    @NotNull
    @DecimalMin(value = "0.1")
    private Double magnitude;

    @Column(name = "duration_ticks", nullable = false)
    @NotNull
    @Min(1)
    private Integer durationTicks;

    @Column(name = "headline", nullable = false, length = 255)
    @NotBlank
    private String headline;

    @Column(name = "triggered_at", nullable = false)
    @NotNull
    private LocalDateTime triggeredAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "triggered_by", nullable = false, length = 16)
    @NotNull
    private TriggeredBy triggeredBy;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 16)
    @NotNull
    private EventStatus status;

    public MarketEvent() {}

    public MarketEvent(EventType eventType, EventScope scope, String target, Double magnitude,
                       Integer durationTicks, String headline, LocalDateTime triggeredAt,
                       TriggeredBy triggeredBy) {
        this.eventType = eventType;
        this.scope = scope;
        this.target = target;
        this.magnitude = magnitude;
        this.durationTicks = durationTicks;
        this.headline = headline;
        this.triggeredAt = triggeredAt;
        this.triggeredBy = triggeredBy;
    }

    @PrePersist
    public void prePersist() {
        if (this.eventId == null || this.eventId.isBlank()) {
            this.eventId = UUID.randomUUID().toString();
        }
        if (this.triggeredAt == null) {
            this.triggeredAt = LocalDateTime.now();
        }
        if (this.status == null) {
            this.status = EventStatus.ACTIVE;
        }
    }

    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }

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

    public String getHeadline() { return headline; }
    public void setHeadline(String headline) { this.headline = headline; }

    public LocalDateTime getTriggeredAt() { return triggeredAt; }
    public void setTriggeredAt(LocalDateTime triggeredAt) { this.triggeredAt = triggeredAt; }

    public TriggeredBy getTriggeredBy() { return triggeredBy; }
    public void setTriggeredBy(TriggeredBy triggeredBy) { this.triggeredBy = triggeredBy; }

    public EventStatus getStatus() { return status; }
    public void setStatus(EventStatus status) { this.status = status; }

    public boolean isMarketWide() {
        return this.scope == EventScope.MARKET;
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

    @Override
    public String toString() {
        return "MarketEvent{" +
                "eventId='" + eventId + '\'' +
                ", eventType=" + eventType +
                ", scope=" + scope +
                ", target='" + target + '\'' +
                ", magnitude=" + magnitude +
                ", durationTicks=" + durationTicks +
                ", triggeredAt=" + triggeredAt +
                ", triggeredBy=" + triggeredBy +
                ", status=" + status +
                '}';
    }
}
