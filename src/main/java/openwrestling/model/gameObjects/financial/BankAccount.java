package openwrestling.model.gameObjects.financial;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import openwrestling.model.gameObjects.GameObject;
import openwrestling.model.gameObjects.Promotion;

@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BankAccount extends GameObject {

    private long bankAccountID;
    private Promotion promotion;
    @Builder.Default
    private long funds = 1000000;


    public void setFunds(long income) {
        funds = income;
    }


}
