package wrestling.model.segmentEnum;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import wrestling.model.Promotion;
import wrestling.model.controller.GameController;
import wrestling.model.interfaces.iBrowseMode;
import wrestling.view.utility.ScreenCode;
import wrestling.view.utility.ViewUtils;
import wrestling.view.utility.comparators.NameComparator;

public enum BookingBrowseMode implements iBrowseMode {

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
            return gameController.getContractManager().getFullRoster(promotion);
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
    TAG_TEAMS {
        @Override
        public ObservableList comparators() {
            return FXCollections.observableArrayList(
                    new NameComparator());
        }

        @Override
        public ScreenCode subScreenCode() {
            return ScreenCode.SIMPLE_DISPLAY;
        }

        @Override
        public List listToBrowse(GameController gameController, Promotion promotion) {
            return gameController.getTagTeamManager().getTagTeamViews(promotion);
        }

        @Override
        public List<EnumSet> getSortFilters() {
            return Arrays.asList();
        }

        @Override
        public String toString() {
            return "Tag Teams";
        }

    },
    TITLES {
        @Override
        public ObservableList comparators() {
            return FXCollections.observableArrayList(
                    new NameComparator());
        }

        @Override
        public ScreenCode subScreenCode() {
            return ScreenCode.SIMPLE_DISPLAY;
        }

        @Override
        public List listToBrowse(GameController gameController, Promotion promotion) {
            return gameController.getTitleManager().getTitles(promotion);
        }

        @Override
        public List<EnumSet> getSortFilters() {
            return Arrays.asList();
        }

        @Override
        public String toString() {
            return "Titles";
        }

    };

}
