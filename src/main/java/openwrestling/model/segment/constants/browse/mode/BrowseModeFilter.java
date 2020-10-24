package openwrestling.model.segment.constants.browse.mode;

import openwrestling.model.segment.constants.ActiveType;
import openwrestling.model.segment.constants.Gender;
import openwrestling.model.segment.constants.StaffType;
import openwrestling.model.segment.constants.TopMatchFilter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static openwrestling.model.segment.constants.browse.mode.BrowseMode.*;

public class BrowseModeFilter {
    private static final Map<BrowseMode, List<Class<? extends Enum>>> COMPARATOR_MAP = new HashMap<>() {
        {
            put(FREE_AGENTS, List.of(Gender.class));
            put(WORKERS, List.of(Gender.class));
            put(HIRE_STAFF, List.of(StaffType.class));
            put(STAFF, List.of(StaffType.class));
            put(TITLES, List.of(ActiveType.class));
            put(TAG_TEAMS, List.of(Gender.class, ActiveType.class));
            put(MATCHES, List.of(TopMatchFilter.class));
        }
    };

    public static List<Class<? extends Enum>> getFilters(BrowseMode browseMode) {
        if (COMPARATOR_MAP.containsKey(browseMode)) {
            return COMPARATOR_MAP.get(browseMode);
        }
        return List.of();
    }
}
