package lynx.team2.marketevent.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WebSocketEnvelope<T> {

    private String type; // "MARKET_EVENT"
    private T payload;

}