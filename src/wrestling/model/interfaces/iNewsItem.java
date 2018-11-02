package wrestling.model.interfaces;

import java.time.LocalDate;
import wrestling.model.modelView.PromotionView;

public interface iNewsItem {

    public String getSummary();

    public LocalDate getDate();

    public PromotionView getPromotion();

}
