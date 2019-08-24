package openwrestling.model.interfaces;

import java.time.LocalDate;
import openwrestling.model.gameObjects.Promotion;

public interface iEvent {

    public Promotion getPromotion();

    public LocalDate getDate();
}
