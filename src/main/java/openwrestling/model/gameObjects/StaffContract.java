package openwrestling.model.gameObjects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import openwrestling.model.interfaces.iContract;
import openwrestling.model.interfaces.iPerson;

import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StaffContract extends GameObject implements Serializable, iContract {

    private long staffContractID;
    private StaffMember staff;
    private Promotion promotion;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate lastShowDate;
    private boolean active;
    private int biWeeklyCost;

    public StaffContract(LocalDate startDate, StaffMember staff, Promotion promotion) {
        active = true;
        this.startDate = startDate;
        this.staff = staff;
        this.promotion = promotion;
    }

    /**
     * @return the biWeeklyCost
     */
    @Override
    public int getMonthlyCost() {
        return biWeeklyCost;
    }

    /**
     * @param biWeeklyCost the biWeeklyCost to set
     */
    public void setMonthlyCost(int biWeeklyCost) {
        this.biWeeklyCost = biWeeklyCost;
    }

    @Override
    public boolean isExclusive() {
        return true;
    }

    @Override
    public int getAppearanceCost() {
        return 0;
    }

    @Override
    public Worker getWorker() {
        return null;
    }

    @Override
    public iPerson getPerson() {
        return staff;
    }

}
