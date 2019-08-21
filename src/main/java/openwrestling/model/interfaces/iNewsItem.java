package openwrestling.model.interfaces;

import java.time.LocalDate;
import java.util.List;
import openwrestling.model.modelView.PromotionView;

public interface iNewsItem {

    public String getSummary();

    public LocalDate getDate();

    public List<PromotionView> getPromotions();

}
