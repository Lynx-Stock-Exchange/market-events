package lynx.team2.marketevent.controller;

import lombok.RequiredArgsConstructor;
import lynx.team2.marketevent.model.entity.MarketEvent;
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

    @GetMapping("/events")
    public ResponseEntity<List<MarketEvent>> getMarketEvents() {
        List<MarketEvent> events = marketEventService.getRecentAndActiveEvents();
        return ResponseEntity.ok(events);
    }
}