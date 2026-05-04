package lynx.team2.marketevent.exception;

/**
 * Thrown when the service fails to broadcast an event to Kafka.
 *
 * <p>Mapped to HTTP 503 by {@link GlobalExceptionHandler}: the event was not
 * persisted (the surrounding {@code @Transactional} rolls back), so the caller
 * should retry. This is a minimum-viable approach — for higher availability,
 * adopt a transactional outbox.
 */
public class EventPublishException extends RuntimeException {

    public EventPublishException(String message, Throwable cause) {
        super(message, cause);
    }
}
