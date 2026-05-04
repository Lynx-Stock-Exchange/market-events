package lynx.team2.marketevent.exception;

/**
 * Thrown when an {@code EventTriggerRequest} violates the spec §7.1 rules
 * — for example, an event_type/scope mismatch, or a target that is missing
 * or present when it shouldn't be.
 *
 * <p>Mapped to HTTP 400 by {@link GlobalExceptionHandler}.
 */
public class InvalidEventException extends RuntimeException {

    public InvalidEventException(String message) {
        super(message);
    }
}
