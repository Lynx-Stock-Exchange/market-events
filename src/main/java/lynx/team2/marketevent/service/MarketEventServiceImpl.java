package lynx.team2.marketevent.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lynx.team2.marketevent.exception.EventConflictException;
import lynx.team2.marketevent.messaging.KafkaEventPublisher;
import lynx.team2.marketevent.model.dto.EventTriggerRequest;
import lynx.team2.marketevent.model.dto.MarketEventPayload;
import lynx.team2.marketevent.model.dto.WebSocketEnvelope;
import lynx.team2.marketevent.model.entity.MarketEvent;
import lynx.team2.marketevent.model.enums.EventScope;
import lynx.team2.marketevent.model.enums.EventStatus;
import lynx.team2.marketevent.repository.MarketEventRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MarketEventServiceImpl implements MarketEventService {

    private final HeadlineSelector headlineSelector;
    private final KafkaEventPublisher kafkaPublisher;


    private final MarketEventRepository marketEventRepository;

    @Override
    public void triggerEvent(EventTriggerRequest request) {
        log.info("Processing event: {} for target: {}", request.getEvent_type(), request.getTarget());

        if (EventScope.MARKET.equals(request.getScope())) {
            validateNoActiveMarketEvent();
        }

        String headline = headlineSelector.getRandomHeadline(request.getEvent_type().name());

        MarketEvent newEvent = new MarketEvent();
        newEvent.setEventType(request.getEvent_type());
        newEvent.setScope(request.getScope());
        newEvent.setTarget(request.getTarget());
        newEvent.setMagnitude(request.getMagnitude());
        newEvent.setDuration_ticks(request.getDuration_ticks());
        newEvent.setHeadline(headline);

        newEvent.setTriggered_by(lynx.team2.marketevent.model.enums.TriggeredBy.ADMIN);

        marketEventRepository.save(newEvent);

        MarketEventPayload payload = MarketEventPayload.builder()
                .eventId(newEvent.getEventId())
                .eventType(newEvent.getEventType())
                .scope(newEvent.getScope())
                .target(newEvent.getTarget())
                .magnitude(newEvent.getMagnitude())
                .durationTicks(newEvent.getDuration_ticks())
                .headline(newEvent.getHeadline())
                .marketTime(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .build();

        WebSocketEnvelope<MarketEventPayload> envelope = new WebSocketEnvelope<>(
                "MARKET_EVENT",
                payload
        );

        kafkaPublisher.publishEvent(envelope);
    }

    private void validateNoActiveMarketEvent() {

        boolean isMarketActive = marketEventRepository.existsByScopeAndStatus(EventScope.MARKET, EventStatus.ACTIVE);
        if (isMarketActive) {
            throw new EventConflictException("A global MARKET event is already active. Cannot trigger another one.");
        }
    }
}