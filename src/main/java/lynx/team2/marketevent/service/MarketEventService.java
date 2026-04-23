package lynx.team2.marketevent.service;


import lynx.team2.marketevent.model.dto.MarketEventPayload;

public interface MarketEventService {
    //EventTriggerRequest e din dto.
    // Luiza va apela metoda asta din Controller.
    // Robi va scrie codul care se executa efectiv in spatele ei.
    void triggerEvent(MarketEventPayload requestPayload);
}
