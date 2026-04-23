package lynx.team2.marketevent.controller;

import jakarta.validation.Valid;
import lynx.team2.marketevent.model.dto.EventTriggerRequest;
import lynx.team2.marketevent.model.entity.MarketEvent;
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

    @PostMapping("/trigger")
    public ResponseEntity<Void> triggerEvent(@Valid @RequestBody EventTriggerRequest request) {
        marketEventService.triggerEvent(request);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
