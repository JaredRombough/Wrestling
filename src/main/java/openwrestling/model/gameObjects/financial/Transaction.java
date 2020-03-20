package openwrestling.model.gameObjects.financial;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import openwrestling.model.gameObjects.GameObject;
import openwrestling.model.gameObjects.Promotion;
import openwrestling.model.segmentEnum.TransactionType;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction extends GameObject {

    private long transactionID;
    private long amount;
    private TransactionType type;
    private LocalDate date;
    private BankAccount bankAccount;
    private Promotion promotion;
}
