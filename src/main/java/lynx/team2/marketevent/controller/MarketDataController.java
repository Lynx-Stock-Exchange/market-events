package lynx.team2.marketevent.controller;

import lombok.RequiredArgsConstructor;
import lynx.team2.marketevent.model.dto.MarketEventResponse;
import lynx.team2.marketevent.service.MarketEventService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/market")
@RequiredArgsConstructor
public class MarketDataController {

    private final MarketEventService marketEventService;

    /**
     * Spec §4.2 — Returns recent and active market events.
     * Maps internal entities to the spec-compliant response DTO so we don't
     * leak persistence-layer details (or differing field naming) over the wire.
     */
    @GetMapping("/events")
    public ResponseEntity<List<MarketEventResponse>> getMarketEvents() {
        List<MarketEventResponse> events = marketEventService.getRecentEvents().stream()
                .map(MarketEventResponse::fromEntity)
                .toList();
        return ResponseEntity.ok(events);
    }
}
