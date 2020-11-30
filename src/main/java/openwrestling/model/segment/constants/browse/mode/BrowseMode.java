package openwrestling.model.segment.constants.browse.mode;

import lombok.Getter;
import openwrestling.view.utility.ScreenCode;

import java.util.Comparator;
import java.util.List;

@Getter
public enum BrowseMode {
    FREE_AGENTS("Free Agents", ScreenCode.WORKER_OVERVIEW),
    WORKERS("Workers", ScreenCode.WORKER_OVERVIEW),
    HIRE_STAFF("Hire Staff", ScreenCode.STAFF_VIEW),
    STAFF("Staff", ScreenCode.STAFF_VIEW),
    REFS("Referees", ScreenCode.STAFF_VIEW),
    BROADCAST("Broadcast Team", ScreenCode.STAFF_VIEW),
    TITLES("Titles", ScreenCode.TITLE_VIEW),
    TAG_TEAMS("Tag Teams", ScreenCode.TAG_TEAM_VIEW),
    STABLES("Stables", ScreenCode.STABLE),
    ROSTER_SPLIT("Roster splits", ScreenCode.STABLE),
    EVENTS("Events", ScreenCode.EVENT_TEMPLATE),
    PAST_EVENTS("Event History"),
    NEWS("News"),
    MATCHES("Best Matches"),
    CONTRACTS_SIGNED("Contracts (New)"),
    CONTRACTS_EXPIRED("Contracts (Expired)"),
    TOP_POPULARITY("Top 100 Most Popular"),
    TOP_STRIKING("Top 100 Striking"),
    TOP_WRESTLING("Top 100 Wrestling"),
    TOP_FLYING("Top 100 Flying"),
    TOP_CHARISMA("Top 100 Most Charismatic"),
    TOP_WORKRATE("Top 100 Workrate"),
    TOP_POPULARITY_MEN("Men - 100 Most Popular"),
    TOP_STRIKING_MEN("Men - 100 Striking"),
    TOP_WRESTLING_MEN("Men - 100 Wrestling"),
    TOP_FLYING_MEN("Men - 100 Flying"),
    TOP_CHARISMA_MEN("Men - 100 Most Charismatic"),
    TOP_WORKRATE_MEN("Men - 100 Workrate"),
    TOP_POPULARITY_WOMEN("Women - 100 Most Popular"),
    TOP_STRIKING_WOMEN("Women - 100 Striking"),
    TOP_WRESTLING_WOMEN("Women - 100 Wrestling"),
    TOP_FLYING_WOMEN("Women - 100 Flying"),
    TOP_CHARISMA_WOMEN("Women - 100 Most Charismatic"),
    TOP_WORKRATE_WOMEN("Women - 100 Workrate");


    private final String name;
    private final ScreenCode screenCode;

    BrowseMode(String name, ScreenCode screenCode) {
        this.name = name;
        this.screenCode = screenCode;
    }

    BrowseMode(String name) {
        this(name, null);
    }

    public List<Comparator> getComparators() {
        return BrowseModeComparator.getComparators(this);
    }

    public List<Class<? extends Enum>> getSortFilters() {
        return BrowseModeFilter.getFilters(this);
    }

    @Override
    public String toString() {
        return name;
    }

}
