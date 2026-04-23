package lynx.team2.marketevent.messaging;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lynx.team2.marketevent.model.dto.MarketEventPayload;
import lynx.team2.marketevent.model.dto.WebSocketEnvelope;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;


@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaEventPublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;

    private final ObjectMapper objectMapper;

    private static final String TOPIC = "market.events.active";

    public void publishEvent(WebSocketEnvelope<MarketEventPayload> envelope) {
        log.info("Attempting to publish event to Kafka topic: {}", TOPIC);

        try {
            String jsonMessage = objectMapper.writeValueAsString(envelope);

            kafkaTemplate.send(TOPIC, jsonMessage);

            log.info("Successfully published event: {}", envelope.getPayload().getEventId());
            log.debug("Published JSON payload: {}", jsonMessage);
        } catch (Exception e) {
            log.error("Failed to publish event to Kafka: {}", e.getMessage());
        }
    }
}