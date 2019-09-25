package openwrestling.model.gameObjects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TitleReign extends GameObject implements Serializable {
    private long titleReignID;
    @Builder.Default
    private List<Worker> workers = new ArrayList<>();
    private LocalDate dayWon;
    private LocalDate dayLost;
    private int sequenceNumber;
    private Title title;

    public TitleReign(List<Worker> workers, LocalDate dayWon, int sequenceNumber) {
        this.workers = workers;
        this.dayWon = dayWon;
        this.sequenceNumber = sequenceNumber;
    }

    public String getDayLostString() {
        return dayLost == null ? "Today" : dayLost.toString();
    }

}
