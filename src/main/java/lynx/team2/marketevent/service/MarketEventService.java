package lynx.team2.marketevent.service;


import lynx.team2.marketevent.model.dto.EventTriggerRequest;
import lynx.team2.marketevent.model.entity.MarketEvent;
import lynx.team2.marketevent.model.enums.TriggeredBy;

import java.util.List;

public interface MarketEventService {
    void triggerEvent(EventTriggerRequest request, TriggeredBy triggeredBy);

    List<MarketEvent> getRecentAndActiveEvents();
}
