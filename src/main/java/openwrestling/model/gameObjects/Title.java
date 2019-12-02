package openwrestling.model.gameObjects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import openwrestling.model.SegmentItem;
import openwrestling.model.interfaces.iRosterSplit;
import openwrestling.model.segmentEnum.ActiveType;
import org.apache.commons.collections4.CollectionUtils;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Title extends GameObject implements SegmentItem, Serializable, iRosterSplit {

    private long titleID;
    private List<TitleReign> titleReigns;
    private RosterSplit rosterSplit;
    private Promotion promotion;
    private int teamSize;
    private String name;
    private ActiveType activeType;
    private int prestige;

    @Override
    public String toString() {
        return name;
    }

    @Override
    public String getLongName() {
        return name + " Title";
    }

    public List<Worker> getChampions() {
        return getChampionTitleReign().getWorkers();
    }

    public TitleReign getChampionTitleReign() {
        return CollectionUtils.isEmpty(titleReigns) ? null : titleReigns.stream()
                .max(Comparator.comparing(TitleReign::getSequenceNumber))
                .orElseThrow();

    }

    @Override
    public boolean equals(Object object) {
        return object instanceof Title &&
                Objects.equals(((Title) object).getTitleID(), titleID);
    }

}
