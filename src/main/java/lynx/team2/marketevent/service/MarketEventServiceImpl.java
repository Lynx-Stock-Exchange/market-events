package lynx.team2.marketevent.service;

import lynx.team2.marketevent.model.dto.EventTriggerRequest;
import org.springframework.stereotype.Service;

@Service
public class MarketEventServiceImpl implements MarketEventService {

    @Override
    public void triggerEvent(EventTriggerRequest request) {
        System.out.println("Event received: " + request.getEvent_type());
    }
}