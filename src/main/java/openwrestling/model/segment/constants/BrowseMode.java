package openwrestling.model.segment.constants;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import openwrestling.model.controller.GameController;
import openwrestling.model.gameObjects.Promotion;
import openwrestling.model.interfaces.iBrowseMode;
import openwrestling.view.utility.ScreenCode;
import openwrestling.view.utility.ViewUtils;
import openwrestling.view.utility.comparators.DateComparator;
import openwrestling.view.utility.comparators.MatchPromotionComparator;
import openwrestling.view.utility.comparators.MatchRatingComparator;
import openwrestling.view.utility.comparators.NameComparator;
import openwrestling.view.utility.comparators.NewsItemComparator;
import openwrestling.view.utility.comparators.TitlePrestigeComparator;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

public enum BrowseMode implements iBrowseMode {
    FREE_AGENTS {
        @Override
        public ObservableList comparators() {
            return ViewUtils.getWorkerComparators();
        }

        @Override
        public ScreenCode subScreenCode() {
            return ScreenCode.WORKER_OVERVIEW;
        }

        @Override
        public List listToBrowse(GameController gameController, Promotion promotion) {
            return gameController.getWorkerManager().freeAgents(promotion);
        }

        @Override
        public List<EnumSet> getSortFilters() {
            return Arrays.asList(EnumSet.allOf(Gender.class));
        }
    },
    HIRE_STAFF {
        @Override
        public ObservableList comparators() {
            return ViewUtils.getStaffComparators();
        }

        @Override
        public ScreenCode subScreenCode() {
            return ScreenCode.STAFF_VIEW;
        }

        @Override
        public List listToBrowse(GameController gameController, Promotion promotion) {
            return gameController.getStaffManager().getAvailableStaff();
        }

        @Override
        public List<EnumSet> getSortFilters() {
            return Arrays.asList(EnumSet.allOf(StaffType.class));
        }
    },
    WORKERS {
        @Override
        public ObservableList comparators() {
            return ViewUtils.getWorkerComparators();
        }

        @Override
        public ScreenCode subScreenCode() {
            return ScreenCode.WORKER_OVERVIEW;
        }

        @Override
        public List listToBrowse(GameController gameController, Promotion promotion) {
            return gameController.getWorkerManager().getRoster(promotion);
        }

        @Override
        public List<EnumSet> getSortFilters() {
            return Arrays.asList(EnumSet.allOf(Gender.class));
        }

        @Override
        public String toString() {
            return "Workers";
        }
    },
    STAFF {
        @Override
        public ObservableList comparators() {
            return ViewUtils.getStaffComparators();
        }

        @Override
        public ScreenCode subScreenCode() {
            return ScreenCode.STAFF_VIEW;
        }

        @Override
        public List listToBrowse(GameController gameController, Promotion promotion) {
            return gameController.getStaffManager().getStaffMembers(promotion);
        }

        @Override
        public List<EnumSet> getSortFilters() {
            return Arrays.asList(EnumSet.allOf(StaffType.class));
        }

        @Override
        public String toString() {
            return "Staff";
        }
    },
    REFS {
        @Override
        public ObservableList comparators() {
            return ViewUtils.getStaffComparators();
        }

        @Override
        public ScreenCode subScreenCode() {
            return ScreenCode.STAFF_VIEW;
        }

        @Override
        public List listToBrowse(GameController gameController, Promotion promotion) {
            return gameController.getStaffManager().getStaff(StaffType.REFEREE, promotion);
        }

        @Override
        public String toString() {
            return "Referees";
        }
    },
    BROADCAST {
        @Override
        public ObservableList comparators() {
            return ViewUtils.getStaffComparators();
        }

        @Override
        public ScreenCode subScreenCode() {
            return ScreenCode.STAFF_VIEW;
        }

        @Override
        public List listToBrowse(GameController gameController, Promotion promotion) {
            return gameController.getStaffManager().getStaff(StaffType.BROADCAST, promotion);
        }

        @Override
        public String toString() {
            return "Broadcast Team";
        }
    },
    TITLES {
        @Override
        public ObservableList comparators() {
            return FXCollections.observableArrayList(
                    new NameComparator(),
                    new TitlePrestigeComparator());
        }

        @Override
        public ScreenCode subScreenCode() {
            return ScreenCode.TITLE_VIEW;
        }

        @Override
        public List listToBrowse(GameController gameController, Promotion promotion) {
            return gameController.getTitleManager().getTitles(promotion);
        }

        @Override
        public List<EnumSet> getSortFilters() {
            return Arrays.asList(EnumSet.allOf(ActiveType.class));
        }

        @Override
        public String toString() {
            return "Titles";
        }
    },
    TAG_TEAMS {
        @Override
        public ObservableList comparators() {
            return FXCollections.observableArrayList(
                    new NameComparator());
        }

        @Override
        public ScreenCode subScreenCode() {
            return ScreenCode.TAG_TEAM_VIEW;
        }

        @Override
        public List listToBrowse(GameController gameController, Promotion promotion) {
            return gameController.getTagTeamManager().getTagTeams(promotion);
        }

        @Override
        public List<EnumSet> getSortFilters() {
            return Arrays.asList(EnumSet.allOf(Gender.class), EnumSet.allOf(ActiveType.class));
        }

        @Override
        public String toString() {
            return "Tag Teams";
        }
    },
    STABLES {
        @Override
        public ObservableList comparators() {
            return FXCollections.observableArrayList(
                    new NameComparator());
        }

        @Override
        public ScreenCode subScreenCode() {
            return ScreenCode.STABLE;
        }

        @Override
        public List listToBrowse(GameController gameController, Promotion promotion) {
            return gameController.getStableManager().getStables()
                    .stream().filter((stable) -> stable.getOwner().equals(promotion)).collect(Collectors.toList());
        }

        @Override
        public String toString() {
            return "Stables";
        }
    },
    ROSTER_SPLIT {
        @Override
        public ObservableList comparators() {
            return FXCollections.observableArrayList(
                    new NameComparator());
        }

        @Override
        public ScreenCode subScreenCode() {
            return ScreenCode.STABLE;
        }

        @Override
        public List listToBrowse(GameController gameController, Promotion promotion) {
            return gameController.getRosterSplitManager().getRosterSplits()
                    .stream().filter((split) -> split.getOwner().equals(promotion)).collect(Collectors.toList());
        }

        @Override
        public String toString() {
            return "Roster splits";
        }
    },
    EVENTS {
        @Override
        public ObservableList comparators() {
            return FXCollections.observableArrayList(new NameComparator());
        }

        @Override
        public ScreenCode subScreenCode() {
            return ScreenCode.EVENT_TEMPLATE;
        }

        @Override
        public List listToBrowse(GameController gameController, Promotion promotion) {
            return gameController.getEventManager().getEventTemplates(promotion);
        }
    },
    NEWS {
        @Override
        public ObservableList comparators() {
            return FXCollections.observableArrayList(new NewsItemComparator());
        }

        @Override
        public List listToBrowse(GameController gameController, Promotion promotion) {
            return gameController.getNewsManager().getNewsItems();
        }
    },
    MATCHES {
        @Override
        public List listToBrowse(GameController gameController, Promotion promotion) {
            return gameController.getSegmentManager().getMatches();
        }

        @Override
        public ObservableList comparators() {
            return FXCollections.observableArrayList(
                    new MatchRatingComparator(),
                    new MatchPromotionComparator(),
                    new DateComparator()
            );
        }

        @Override
        public List<EnumSet> getSortFilters() {
            return Arrays.asList(EnumSet.allOf(TopMatchFilter.class));
        }

        @Override
        public String toString() {
            return "Matches";
        }
    }

}
