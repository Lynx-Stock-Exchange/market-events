package lynx.team2.marketevent.service;


import lynx.team2.marketevent.model.dto.EventTriggerRequest;

public interface MarketEventService {
    void triggerEvent(EventTriggerRequest request);
}
