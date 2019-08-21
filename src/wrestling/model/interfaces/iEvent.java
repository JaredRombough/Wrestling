package wrestling.model.interfaces;

import java.time.LocalDate;
import wrestling.model.modelView.PromotionView;

public interface iEvent {

    public PromotionView getPromotion();

    public LocalDate getDate();
}
