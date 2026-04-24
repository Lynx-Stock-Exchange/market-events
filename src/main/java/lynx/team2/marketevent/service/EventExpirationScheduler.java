package lynx.team2.marketevent.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lynx.team2.marketevent.model.entity.MarketEvent;
import lynx.team2.marketevent.model.enums.EventStatus;
import lynx.team2.marketevent.repository.MarketEventRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventExpirationScheduler {

    private final MarketEventRepository marketEventRepository;

    @Scheduled(fixedRateString = "${market.simulation.expiration-scheduler-rate}")
    public void expireOldEvents() {
        List<MarketEvent> activeEvents = marketEventRepository.findByStatus(EventStatus.ACTIVE);

        if (activeEvents.isEmpty()) {
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        int expiredCount = 0;

        for (MarketEvent event : activeEvents) {

            LocalDateTime expirationTime = event.getTriggered_at().plusMinutes(event.getDuration_ticks());

            if (now.isAfter(expirationTime)) {
                event.setStatus(EventStatus.EXPIRED);
                marketEventRepository.save(event);

                expiredCount++;
                log.info("Event-ul cu ID {} a expirat si a fost marcat ca EXPIRED.", event.getEventId());
            }
        }

        if (expiredCount > 0) {
            log.info("Curatenie finalizata: {} evenimente au fost expirate automat.", expiredCount);
        }
    }
}