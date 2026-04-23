package lynx.team2.marketevent;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class MarketEventApplication {
    public static void main(String[] args) {
        SpringApplication.run(MarketEventApplication.class, args);
    }
}