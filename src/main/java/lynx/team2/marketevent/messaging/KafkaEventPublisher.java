package lynx.team2.marketevent.messaging;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lynx.team2.marketevent.exception.EventPublishException;
import lynx.team2.marketevent.model.dto.MarketEventPayload;
import lynx.team2.marketevent.model.dto.WebSocketEnvelope;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;


@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaEventPublisher {

    private static final long PUBLISH_TIMEOUT_SECONDS = 5;

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    /**
     * Kafka topic events are published to. Default matches the canonical
     * topic name defined by the broker setup ({@code market.events}).
     * Override with {@code market.kafka.event-topic} when needed.
     */
    @Value("${market.kafka.event-topic:market.events}")
    private String topic;

    /**
     * Publishes the event envelope synchronously, blocking until the broker acks
     * (or up to {@link #PUBLISH_TIMEOUT_SECONDS}). A failure throws
     * {@link EventPublishException}, which the surrounding {@code @Transactional}
     * boundary uses to roll back the persisted event.
     */
    public void publishEvent(WebSocketEnvelope<MarketEventPayload> envelope) {
        String eventId = envelope.getPayload().getEventId();
        log.info("Publishing event {} to Kafka topic {}", eventId, topic);

        String jsonMessage;
        try {
            jsonMessage = objectMapper.writeValueAsString(envelope);
        } catch (Exception e) {
            log.error("Failed to serialize event {} for publish", eventId, e);
            throw new EventPublishException("Failed to serialize event " + eventId, e);
        }

        try {
            kafkaTemplate.send(topic, jsonMessage)
                    .orTimeout(PUBLISH_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                    .join();
            log.debug("Published event {}", eventId);
        } catch (Exception e) {
            log.error("Failed to publish event {} to Kafka", eventId, e);
            throw new EventPublishException("Failed to publish event " + eventId, e);
        }
    }
}
