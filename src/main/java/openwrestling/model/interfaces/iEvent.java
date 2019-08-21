package openwrestling.model.interfaces;

import java.time.LocalDate;
import openwrestling.model.modelView.PromotionView;

public interface iEvent {

    public PromotionView getPromotion();

    public LocalDate getDate();
}
