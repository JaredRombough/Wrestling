package wrestling.model.interfaces;

import java.time.LocalDate;
import wrestling.model.Promotion;

/**
 *
 * @author jared
 */
public interface iContract {

    public void setDuration(int duration);

    public void setStartDate(LocalDate date);

    public Promotion getPromotion();

    public int getDuration();

    public boolean isExclusive();

    public int getBiWeeklyCost();

    public int getAppearanceCost();

}
