package lynx.team2.marketevent.controller;

import jakarta.validation.Valid;
import lynx.team2.marketevent.model.dto.EventTriggerRequest;
import lynx.team2.marketevent.model.dto.MarketEventResponse;
import lynx.team2.marketevent.model.entity.MarketEvent;
import lynx.team2.marketevent.model.enums.TriggeredBy;
import lynx.team2.marketevent.service.MarketEventService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/events")
public class InternalEventController {

    private final MarketEventService marketEventService;

    public InternalEventController(MarketEventService marketEventService) {
        this.marketEventService = marketEventService;
    }

    /**
     * Spec §4.5 — Manual event injection by an admin.
     * Returns 201 Created with the persisted event so the caller can use the
     * generated event_id to correlate with downstream Kafka/WebSocket messages.
     */
    @PostMapping("/trigger")
    public ResponseEntity<MarketEventResponse> triggerEvent(@Valid @RequestBody EventTriggerRequest request) {
        MarketEvent created = marketEventService.triggerEvent(request, TriggeredBy.ADMIN);
        return ResponseEntity.status(HttpStatus.CREATED).body(MarketEventResponse.fromEntity(created));
    }
}
