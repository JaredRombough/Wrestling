package openwrestling.model.gameObjects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Injury extends GameObject {
    private long InjuryID;
    private LocalDate expiryDate;
    private LocalDate startDate;
    private Worker worker;
    private Promotion promotion;
    private Segment segment;
}
