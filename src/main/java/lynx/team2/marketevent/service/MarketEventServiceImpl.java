package lynx.team2.marketevent.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lynx.team2.marketevent.model.dto.EventTriggerRequest;
import lynx.team2.marketevent.model.dto.MarketEventPayload;
import lynx.team2.marketevent.model.dto.WebSocketEnvelope;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MarketEventServiceImpl implements MarketEventService {

    private final HeadlineSelector headlineSelector;

    @Override
    public void triggerEvent(EventTriggerRequest request) {
        log.info("Processing event trigger request for type: {}", request.getEventType());

        // 1. Validation Logic (Your Role B)
        // Rule: Only one MARKET event at a time [cite: 366]
        if ("MARKET".equals(request.getScope())) {
            validateNoActiveMarketEvent();
        }

        // 2. Generate the Headline (Your Role B)
        String generatedHeadline = headlineSelector.getRandomHeadline(request.getEventType());

        // 3. Assemble the Output DTO (The "Letter" for Kafka)
        // Here we map the Request to the Payload required by the Spec [cite: 133-134]
        MarketEventPayload kafkaPayload = MarketEventPayload.builder()
                .eventId("evt-" + UUID.randomUUID())
                .eventType(request.getEventType())
                .scope(request.getScope())
                .target(request.getTarget())
                .magnitude(request.getMagnitude())
                .durationTicks(request.getDurationTicks())
                .headline(generatedHeadline)
                .marketTime(LocalDateTime.now().toString()) // Should match simulated time eventually
                .build();

        // 4. Wrap in Envelope and Send [cite: 197-199]
        WebSocketEnvelope<MarketEventPayload> envelope = new WebSocketEnvelope<>(
                "MARKET_EVENT",
                kafkaPayload
        );

        sendToKafka(envelope);
    }

    private void validateNoActiveMarketEvent() {
        // Logica de validare va veni aici [cite: 366-367]
    }

    private void sendToKafka(WebSocketEnvelope<MarketEventPayload> envelope) {
        log.info("Sending message to Kafka topic market.events.active: {}", envelope);
    }
}