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
import java.util.Objects;

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

    public String getDayLostString() {
        return dayLost == null ? "Today" : dayLost.toString();
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof TitleReign &&
                Objects.equals(((TitleReign) object).getTitleReignID(), titleReignID);
    }
}
