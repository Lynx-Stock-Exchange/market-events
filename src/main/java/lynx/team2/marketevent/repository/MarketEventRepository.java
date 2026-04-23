package lynx.team2.marketevent.repository;

import lynx.team2.marketevent.model.entity.MarketEvent;
import lynx.team2.marketevent.model.enums.EventScope;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MarketEventRepository extends JpaRepository<MarketEvent,String> {
    boolean existsByScope(EventScope scope);
}
