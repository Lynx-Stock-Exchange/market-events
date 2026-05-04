package lynx.team2.marketevent.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lynx.team2.marketevent.model.entity.MarketEvent;
import lynx.team2.marketevent.model.enums.EventStatus;
import lynx.team2.marketevent.repository.MarketEventRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventExpirationScheduler {

    private final MarketEventRepository marketEventRepository;

    @Value("${market.simulation.tick-interval-ms}")
    private long tickIntervalMs;

    @Scheduled(fixedRateString = "${market.simulation.expiration-scheduler-rate}")
    public void expireOldEvents() {
        List<MarketEvent> activeEvents = marketEventRepository.findByStatus(EventStatus.ACTIVE);

        if (activeEvents.isEmpty()) {
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        int expiredCount = 0;

        for (MarketEvent event : activeEvents) {
            long durationSeconds = (long) event.getDuration_ticks() * tickIntervalMs / 1000;
            LocalDateTime expirationTime = event.getTriggered_at().plusSeconds(durationSeconds);

            if (now.isAfter(expirationTime)) {
                event.setStatus(EventStatus.EXPIRED);
                marketEventRepository.save(event);

                expiredCount++;
                log.info("Event {} expired and marked as EXPIRED.", event.getEventId());
            }
        }

        if (expiredCount > 0) {
            log.info("Expiration run complete: {} event(s) expired.", expiredCount);
        }
    }
}
