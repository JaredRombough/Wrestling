package openwrestling.model.segment.constants.browse.mode;

import lombok.Getter;
import openwrestling.model.controller.GameController;
import openwrestling.model.gameObjects.Promotion;
import openwrestling.model.segment.constants.ActiveType;
import openwrestling.model.segment.constants.Gender;
import openwrestling.model.segment.constants.StaffType;
import openwrestling.model.segment.constants.TopMatchFilter;
import openwrestling.view.utility.ScreenCode;

import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public enum BrowseMode implements iBrowseMode {
    FREE_AGENTS("Free Agents", ScreenCode.WORKER_OVERVIEW) {
        @Override
        public List listToBrowse(GameController gameController, Promotion promotion) {
            return gameController.getWorkerManager().freeAgents(promotion);
        }

        @Override
        public List<EnumSet> getSortFilters() {
            return Arrays.asList(EnumSet.allOf(Gender.class));
        }
    },
    HIRE_STAFF("Hire Staff", ScreenCode.STAFF_VIEW) {
        @Override
        public List listToBrowse(GameController gameController, Promotion promotion) {
            return gameController.getStaffManager().getAvailableStaff();
        }

        @Override
        public List<EnumSet> getSortFilters() {
            return Arrays.asList(EnumSet.allOf(StaffType.class));
        }
    },
    WORKERS("Workers", ScreenCode.WORKER_OVERVIEW) {
        @Override
        public List listToBrowse(GameController gameController, Promotion promotion) {
            return gameController.getWorkerManager().getRoster(promotion);
        }

        @Override
        public List<EnumSet> getSortFilters() {
            return Arrays.asList(EnumSet.allOf(Gender.class));
        }
    },
    STAFF("Staff", ScreenCode.STAFF_VIEW) {
        @Override
        public List listToBrowse(GameController gameController, Promotion promotion) {
            return gameController.getStaffManager().getStaffMembers(promotion);
        }

        @Override
        public List<EnumSet> getSortFilters() {
            return Arrays.asList(EnumSet.allOf(StaffType.class));
        }
    },
    REFS("Referees", ScreenCode.STAFF_VIEW) {
        @Override
        public List listToBrowse(GameController gameController, Promotion promotion) {
            return gameController.getStaffManager().getStaff(StaffType.REFEREE, promotion);
        }
    },
    BROADCAST("Broadcast Team", ScreenCode.STAFF_VIEW) {
        @Override
        public List listToBrowse(GameController gameController, Promotion promotion) {
            return gameController.getStaffManager().getStaff(StaffType.BROADCAST, promotion);
        }
    },
    TITLES("Titles", ScreenCode.TITLE_VIEW) {
        @Override
        public List listToBrowse(GameController gameController, Promotion promotion) {
            return gameController.getTitleManager().getTitles(promotion);
        }

        @Override
        public List<EnumSet> getSortFilters() {
            return Arrays.asList(EnumSet.allOf(ActiveType.class));
        }
    },
    TAG_TEAMS("Tag Teams", ScreenCode.TAG_TEAM_VIEW) {
        @Override
        public List listToBrowse(GameController gameController, Promotion promotion) {
            return gameController.getTagTeamManager().getTagTeams(promotion);
        }

        @Override
        public List<EnumSet> getSortFilters() {
            return Arrays.asList(EnumSet.allOf(Gender.class), EnumSet.allOf(ActiveType.class));
        }
    },
    STABLES("Stables", ScreenCode.STABLE) {
        @Override
        public List listToBrowse(GameController gameController, Promotion promotion) {
            return gameController.getStableManager().getStables()
                    .stream().filter((stable) -> stable.getOwner().equals(promotion)).collect(Collectors.toList());
        }
    },
    ROSTER_SPLIT("Roster splits", ScreenCode.STABLE) {
        @Override
        public List listToBrowse(GameController gameController, Promotion promotion) {
            return gameController.getRosterSplitManager().getRosterSplits()
                    .stream().filter((split) -> split.getOwner().equals(promotion)).collect(Collectors.toList());
        }
    },
    EVENTS("Events", ScreenCode.EVENT_TEMPLATE) {
        @Override
        public List listToBrowse(GameController gameController, Promotion promotion) {
            return gameController.getEventManager().getEventTemplates(promotion);
        }
    },
    NEWS("News") {
        @Override
        public List listToBrowse(GameController gameController, Promotion promotion) {
            return gameController.getNewsManager().getNewsItems();
        }
    },
    MATCHES("Matches") {
        @Override
        public List listToBrowse(GameController gameController, Promotion promotion) {
            return gameController.getSegmentManager().getMatches();
        }

        @Override
        public List<EnumSet> getSortFilters() {
            return Arrays.asList(EnumSet.allOf(TopMatchFilter.class));
        }
    };

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

    @Override
    public String toString() {
        return name;
    }

}
