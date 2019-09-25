package openwrestling.model.gameObjects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import openwrestling.model.SegmentItem;
import openwrestling.model.interfaces.iRosterSplit;
import openwrestling.model.segmentEnum.ActiveType;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Title extends GameObject implements SegmentItem, Serializable, iRosterSplit {

    private long titleID;
    @Builder.Default
    private List<TitleReign> titleReigns = new ArrayList<>();
    private TitleReign championTitleReign;
    private RosterSplit rosterSplit;
    private Promotion promotion;
    private int teamSize;
    private String name;
    private ActiveType activeType;
    private int prestige;
    private int sequenceNumber;


    public void addReign(List<Worker> workers, LocalDate dayWon) {
        if (championTitleReign != null) {
            championTitleReign.setDayLost(dayWon);
        }
        int sequenceNumber = titleReigns.size() + 1;
        TitleReign newChamps = new TitleReign(workers, dayWon, sequenceNumber);
        titleReigns.add(newChamps);
        championTitleReign = newChamps;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public String getLongName() {
        return name + " Title";
    }

    public List<Worker> getChampions() {
        return championTitleReign.getWorkers();
    }

}
