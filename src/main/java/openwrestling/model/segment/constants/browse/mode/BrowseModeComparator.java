package openwrestling.model.segment.constants.browse.mode;

import openwrestling.view.utility.comparators.DateComparator;
import openwrestling.view.utility.comparators.MatchPromotionComparator;
import openwrestling.view.utility.comparators.MatchRatingComparator;
import openwrestling.view.utility.comparators.NameComparator;
import openwrestling.view.utility.comparators.NewsItemComparator;
import openwrestling.view.utility.comparators.SegmentItemAgeComparator;
import openwrestling.view.utility.comparators.SegmentItemBehaviourComparator;
import openwrestling.view.utility.comparators.StaffSkillComparator;
import openwrestling.view.utility.comparators.TitlePrestigeComparator;
import openwrestling.view.utility.comparators.WorkerCharismaComparator;
import openwrestling.view.utility.comparators.WorkerFlyingComparator;
import openwrestling.view.utility.comparators.WorkerPopularityComparator;
import openwrestling.view.utility.comparators.WorkerStrikingComparator;
import openwrestling.view.utility.comparators.WorkerWrestlingComparator;
import openwrestling.view.utility.comparators.WorkrateComparator;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static openwrestling.model.segment.constants.browse.mode.BrowseMode.*;

public class BrowseModeComparator {

    private static final List<Comparator> WORKER_COMPARATORS = List.of(
            new NameComparator(),
            new WorkerPopularityComparator(),
            new WorkrateComparator(),
            new WorkerCharismaComparator(),
            new WorkerWrestlingComparator(),
            new WorkerFlyingComparator(),
            new WorkerStrikingComparator(),
            new SegmentItemBehaviourComparator(),
            new SegmentItemAgeComparator()
    );

    private static final List<Comparator> STAFF_COMPARATORS = List.of(
            new NameComparator(),
            new StaffSkillComparator(),
            new SegmentItemBehaviourComparator(),
            new SegmentItemAgeComparator()
    );

    private static final List<Comparator> TITLE_COMPARATORS = List.of(
            new NameComparator(),
            new TitlePrestigeComparator()
    );


    private static final List<Comparator> MATCH_COMPARATORS = List.of(
            new MatchRatingComparator(),
            new MatchPromotionComparator(),
            new DateComparator()
    );

    private static final List<Comparator> NAME_COMPARATOR = List.of(new NameComparator());

    private static final List<Comparator> NEWS_COMPARATOR = List.of(new NewsItemComparator());

    private static final Map<BrowseMode, List<Comparator>> COMPARATOR_MAP = new HashMap<>() {
        {
            put(FREE_AGENTS, WORKER_COMPARATORS);
            put(WORKERS, WORKER_COMPARATORS);
            put(HIRE_STAFF, STAFF_COMPARATORS);
            put(STAFF, STAFF_COMPARATORS);
            put(REFS, STAFF_COMPARATORS);
            put(BROADCAST, STAFF_COMPARATORS);
            put(TITLES, TITLE_COMPARATORS);
            put(TAG_TEAMS, NAME_COMPARATOR);
            put(STABLES, NAME_COMPARATOR);
            put(ROSTER_SPLIT, NAME_COMPARATOR);
            put(EVENTS, NAME_COMPARATOR);
            put(NEWS, NEWS_COMPARATOR);
            put(MATCHES, MATCH_COMPARATORS);
        }
    };

    public static List<Comparator> getComparators(BrowseMode browseMode) {
        if (COMPARATOR_MAP.containsKey(browseMode)) {
            return COMPARATOR_MAP.get(browseMode);
        }
        return List.of();
    }

}
