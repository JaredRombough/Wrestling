package openwrestling.view.utility;

import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import lombok.Setter;
import openwrestling.model.NewsItem;
import openwrestling.model.SegmentItem;
import openwrestling.model.gameObjects.Promotion;
import openwrestling.model.gameObjects.RosterSplit;
import openwrestling.model.gameObjects.Stable;
import openwrestling.model.gameObjects.StaffMember;
import openwrestling.model.gameObjects.TagTeam;
import openwrestling.model.gameObjects.Title;
import openwrestling.model.gameObjects.Worker;
import openwrestling.model.segment.constants.ActiveType;
import openwrestling.model.segment.constants.Gender;
import openwrestling.model.segment.constants.NewsFilter;
import openwrestling.model.segment.constants.StaffType;

import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;

@Setter
public class FilterService {

    private final Promotion playerPromotion;
    private Gender genderFilter;
    private ActiveType activeTypeFilter;
    private StaffType staffTypeFilter;
    private NewsFilter newsFilter;
    private Stable stableFilter;
    private RosterSplit rosterSplitFilter;

    public FilterService(Promotion playerPromotion) {
        this.playerPromotion = playerPromotion;
        genderFilter = Gender.ALL;
        activeTypeFilter = ActiveType.ALL;
        staffTypeFilter = StaffType.ALL;
        newsFilter = NewsFilter.ALL;
    }

    public Enum selectedEnum(EnumSet set) {
        for (Enum e : getActiveFilters()) {
            if (set.contains(e)) {
                return e;
            }
        }
        return null;
    }

    public <T> SortedList<T> getSortedList(List<T> inputList, Comparator comparator) {
        FilteredList<T> filteredList = new FilteredList<>(
                FXCollections.observableArrayList(inputList),
                item -> !isFiltered(item)
        );

        return new SortedList<T>(filteredList, comparator);
    }

    public boolean isFiltered(Object object) {
        if (object instanceof SegmentItem) {
            SegmentItem segmentItem = (SegmentItem) object;
            return isActiveFiltered(segmentItem) || isGenderFiltered(segmentItem) || isStaffTypeFiltered(segmentItem) || isWorkerGroupFiltered(segmentItem);
        } else if (object instanceof NewsItem) {
            return isNewsItemFiltered((NewsItem) object);
        }
        return true;
    }


    private boolean isNewsItemFiltered(NewsItem newsItem) {
        if (newsFilter.equals(NewsFilter.ALL)) {
            return false;
        }
        return !newsItem.getPromotions().contains(playerPromotion);
    }

    private boolean isActiveFiltered(SegmentItem segmentItem) {
        if (activeTypeFilter.equals(ActiveType.ALL)
                || segmentItem.getActiveType().equals(ActiveType.ALL)) {
            return false;
        }
        return !activeTypeFilter.equals(segmentItem.getActiveType());
    }

    private boolean isGenderFiltered(SegmentItem segmentItem) {
        if (!genderFilter.equals(Gender.ALL)) {
            for (SegmentItem subItem : segmentItem.getSegmentItems()) {
                if (subItem instanceof Worker
                        && !((Worker) subItem).getGender().equals(genderFilter)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isWorkerGroupFiltered(SegmentItem segmentItem) {
        return isStableFiltered(segmentItem) || isRosterSplitFiltered(segmentItem);
    }

    private boolean isStableFiltered(SegmentItem segmentItem) {
        if (stableFilter != null && (segmentItem instanceof Worker || segmentItem instanceof TagTeam)) {
            for (SegmentItem subItem : segmentItem.getSegmentItems()) {
                if (!stableFilter.getWorkers().contains((Worker) subItem)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isRosterSplitFiltered(SegmentItem segmentItem) {
        if (rosterSplitFilter != null && (segmentItem instanceof Worker || segmentItem instanceof TagTeam || segmentItem instanceof Title)) {
            for (SegmentItem subItem : segmentItem.getSegmentItems()) {
                if (segmentItem instanceof Title) {
                    return !Objects.equals(((Title) segmentItem).getRosterSplit(), rosterSplitFilter);
                } else if (!rosterSplitFilter.getWorkers().contains(subItem)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isStaffTypeFiltered(SegmentItem segmentItem) {
        if (!staffTypeFilter.equals(StaffType.ALL)) {
            for (SegmentItem subItem : segmentItem.getSegmentItems()) {
                if (subItem instanceof StaffMember
                        && !((StaffMember) subItem).getStaffType().equals(staffTypeFilter)) {
                    return true;
                }
            }
        }
        return false;
    }

    private List<Enum> getActiveFilters() {
        return Arrays.asList(genderFilter, activeTypeFilter, staffTypeFilter);
    }

}
