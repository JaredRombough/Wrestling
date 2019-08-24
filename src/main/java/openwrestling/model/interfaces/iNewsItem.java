package openwrestling.model.interfaces;

import java.time.LocalDate;
import java.util.List;
import openwrestling.model.gameObjects.Promotion;

public interface iNewsItem {

    public String getSummary();

    public LocalDate getDate();

    public List<Promotion> getPromotions();

}
