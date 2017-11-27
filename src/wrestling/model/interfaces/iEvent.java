package wrestling.model.interfaces;

import java.time.LocalDate;
import wrestling.model.Promotion;

public interface iEvent {

    public Promotion getPromotion();

    public LocalDate getDate();
}
