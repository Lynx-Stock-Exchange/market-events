package lynx.team2.marketevent.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lynx.team2.marketevent.exception.EventConflictException;
import lynx.team2.marketevent.exception.InvalidEventException;
import lynx.team2.marketevent.messaging.KafkaEventPublisher;
import lynx.team2.marketevent.model.dto.EventTriggerRequest;
import lynx.team2.marketevent.model.dto.MarketEventPayload;
import lynx.team2.marketevent.model.dto.WebSocketEnvelope;
import lynx.team2.marketevent.model.entity.MarketEvent;
import lynx.team2.marketevent.model.enums.EventScope;
import lynx.team2.marketevent.model.enums.EventStatus;
import lynx.team2.marketevent.model.enums.TriggeredBy;
import lynx.team2.marketevent.repository.MarketEventRepository;
import lynx.team2.marketevent.simulation.MarketClock;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MarketEventServiceImpl implements MarketEventService {

    private final HeadlineSelector headlineSelector;
    private final KafkaEventPublisher kafkaPublisher;
    private final MarketEventRepository marketEventRepository;
    private final MarketClock marketClock;

    @Override
    @Transactional
    public MarketEvent triggerEvent(EventTriggerRequest request, TriggeredBy triggeredBy) {
        log.info("Processing event: {} for target: {}", request.getEventType(), request.getTarget());

        validateRequest(request);

        if (EventScope.MARKET.equals(request.getScope())) {
            validateNoActiveMarketEvent();
        }

        String headline = headlineSelector.getRandomHeadline(request.getEventType());

        MarketEvent newEvent = new MarketEvent();
        newEvent.setEventType(request.getEventType());
        newEvent.setScope(request.getScope());
        newEvent.setTarget(request.getTarget());
        newEvent.setMagnitude(request.getMagnitude());
        newEvent.setDurationTicks(request.getDurationTicks());
        newEvent.setHeadline(headline);
        newEvent.setTriggeredBy(triggeredBy);

        MarketEvent saved = marketEventRepository.saveAndFlush(newEvent);

        MarketEventPayload payload = MarketEventPayload.builder()
                .eventId(saved.getEventId())
                .eventType(saved.getEventType())
                .scope(saved.getScope())
                .target(saved.getTarget())
                .magnitude(saved.getMagnitude())
                .durationTicks(saved.getDurationTicks())
                .headline(saved.getHeadline())
                .marketTime(marketClock.nowSimulated().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .build();

        WebSocketEnvelope<MarketEventPayload> envelope = new WebSocketEnvelope<>(
                "MARKET_EVENT",
                payload
        );


        kafkaPublisher.publishEvent(envelope);

        return saved;
    }

    private void validateNoActiveMarketEvent() {

        boolean isMarketActive = marketEventRepository.existsByScopeAndStatus(EventScope.MARKET, EventStatus.ACTIVE);
        if (isMarketActive) {
            throw new EventConflictException("A global MARKET event is already active. Cannot trigger another one.");
        }
    }


    private void validateRequest(EventTriggerRequest request) {
        EventScope expectedScope = request.getEventType().requiredScope();
        if (request.getScope() != expectedScope) {
            throw new InvalidEventException(
                    "Event type %s requires scope %s but %s was provided.".formatted(
                            request.getEventType(), expectedScope, request.getScope()));
        }


        boolean targetMissing = request.getTarget() == null || request.getTarget().isBlank();
        if (request.getScope() == EventScope.MARKET) {
            if (!targetMissing) {
                throw new InvalidEventException("Target must be null or empty for MARKET scope.");
            }
        } else if (targetMissing) {
            throw new InvalidEventException(
                    "Target is required for %s scope.".formatted(request.getScope()));
        }
    }

    @Override
    public List<MarketEvent> getRecentEvents() {
        log.info("Fetching recent market events from DB.");
        return marketEventRepository.findTop50ByOrderByTriggeredAtDesc();
    }
}