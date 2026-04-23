package lynx.team2.marketevent.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lynx.team2.marketevent.messaging.KafkaEventPublisher;
import lynx.team2.marketevent.model.dto.EventTriggerRequest;
import lynx.team2.marketevent.model.dto.MarketEventPayload;
import lynx.team2.marketevent.model.dto.WebSocketEnvelope;
import lynx.team2.marketevent.model.enums.EventScope;
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

    @Override
    public void triggerEvent(EventTriggerRequest request) {
        log.info("Processing event trigger request for type: {}", request.getEvent_type());


        if (EventScope.MARKET.equals(request.getScope())) {
            validateNoActiveMarketEvent();
        }


        String generatedHeadline = headlineSelector.getRandomHeadline(request.getEvent_type().name());


        MarketEventPayload kafkaPayload = MarketEventPayload.builder()
                .eventId("evt-" + UUID.randomUUID())
                .eventType(request.getEvent_type())
                .scope(request.getScope())
                .target(request.getTarget())
                .magnitude(request.getMagnitude())
                .durationTicks(request.getDuration_ticks())
                .headline(generatedHeadline)
                .marketTime(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .build();

        WebSocketEnvelope<MarketEventPayload> envelope = new WebSocketEnvelope<>(
                "MARKET_EVENT",
                kafkaPayload
        );

        kafkaPublisher.publishEvent(envelope);
    }

    private void validateNoActiveMarketEvent() {
        // TODO: Query the database to check for active MARKET events once the Repository is ready
        // if (marketEventRepository.existsByScopeAndStatus(EventScope.MARKET, "ACTIVE")) {
        //     log.warn("ERROR: A MARKET event is already active!");
        //     throw new RuntimeException("Cannot trigger two MARKET events simultaneously.");
        // }
    }

}