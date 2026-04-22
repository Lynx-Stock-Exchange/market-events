package lynx.team2.marketevent.service;


public interface MarketEventService {
    //EventTriggerRequest e din dto.
    // Luiza va apela metoda asta din Controller.
    // Robi va scrie codul care se executa efectiv in spatele ei.
    void triggerEvent(EventTriggerRequest request);
}
