package openwrestling.model.gameObjects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import openwrestling.model.interfaces.iContract;
import openwrestling.model.interfaces.iPerson;
import openwrestling.model.modelView.StaffView;

import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Contract extends GameObject implements Serializable, iContract {

    private int contractID;
    private Promotion promotion;
    private Worker worker;

    private boolean active;

    private LocalDate startDate;

    private LocalDate endDate;

    private LocalDate lastShowDate;

    private boolean exclusive;

    private boolean pushed;

    private int appearanceCost;

    private int monthlyCost;

    public Contract(LocalDate startDate, Worker worker, Promotion promotion) {
        active = true;
        lastShowDate = startDate;
        this.startDate = startDate;
        this.worker = worker;
        this.promotion = promotion;
    }


    @Override
    public StaffView getStaff() {
        return null;
    }

    @Override
    public iPerson getPerson() {
        return worker;
    }

    /**
     * @return the lastShowDate
     */
    @Override
    public LocalDate getLastShowDate() {
        return lastShowDate;
    }

}
