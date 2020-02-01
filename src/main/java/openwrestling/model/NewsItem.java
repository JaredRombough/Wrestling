package openwrestling.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import openwrestling.model.gameObjects.GameObject;
import openwrestling.model.gameObjects.Promotion;
import openwrestling.model.gameObjects.Worker;
import openwrestling.model.interfaces.iNewsItem;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NewsItem extends GameObject implements iNewsItem {
    private long newsItemID;
    private String summary;
    private String headline;
    private LocalDate date;
    private List<Promotion> promotions;
    private List<Worker> workers;

    @Override
    public String toString() {
        return headline;
    }
}
