package lynx.team2.marketevent.repository;

import lynx.team2.marketevent.model.entity.MarketEvent;
import lynx.team2.marketevent.model.enums.EventScope;
import lynx.team2.marketevent.model.enums.EventStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MarketEventRepository extends JpaRepository<MarketEvent,String> {
    boolean existsByScopeAndStatus(EventScope scope, EventStatus status);

    List<MarketEvent> findByStatus(EventStatus status);
}
