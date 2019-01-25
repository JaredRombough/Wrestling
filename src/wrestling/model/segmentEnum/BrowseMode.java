package wrestling.model.segmentEnum;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import wrestling.model.controller.GameController;
import wrestling.model.interfaces.iBrowseMode;
import wrestling.model.modelView.PromotionView;
import wrestling.view.utility.ScreenCode;
import wrestling.view.utility.ViewUtils;
import wrestling.view.utility.comparators.DateComparator;
import wrestling.view.utility.comparators.NameComparator;
import wrestling.view.utility.comparators.TitlePrestigeComparator;

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
        public List listToBrowse(GameController gameController, PromotionView promotion) {
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
        public List listToBrowse(GameController gameController, PromotionView promotion) {
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
        public List listToBrowse(GameController gameController, PromotionView promotion) {
            return promotion.getFullRoster();
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
        public List listToBrowse(GameController gameController, PromotionView promotion) {
            return promotion.getAllStaff();
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
        public List listToBrowse(GameController gameController, PromotionView promotion) {
            return promotion.getStaff(StaffType.REFEREE);
        }

        @Override
        public List<EnumSet> getSortFilters() {
            return Arrays.asList();
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
        public List listToBrowse(GameController gameController, PromotionView promotion) {
            return promotion.getStaff(StaffType.BROADCAST);
        }

        @Override
        public List<EnumSet> getSortFilters() {
            return Arrays.asList();
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
        public List listToBrowse(GameController gameController, PromotionView promotion) {
            return gameController.getTitleManager().getTitleViews(promotion);
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
        public List listToBrowse(GameController gameController, PromotionView promotion) {
            return gameController.getTagTeamManager().getTagTeamViews(promotion);
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
    EVENTS {
        @Override
        public ObservableList comparators() {
            return FXCollections.observableArrayList(new DateComparator(), new NameComparator());
        }

        @Override
        public ScreenCode subScreenCode() {
            return ScreenCode.EVENT_TEMPLATE;
        }

        @Override
        public List listToBrowse(GameController gameController, PromotionView promotion) {
            return gameController.getEventManager().getEventTemplates(promotion);
        }

        @Override
        public List<EnumSet> getSortFilters() {
            return Arrays.asList();
        }
    };

}
