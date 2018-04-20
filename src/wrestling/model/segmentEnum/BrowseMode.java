package wrestling.model.segmentEnum;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import wrestling.model.Promotion;
import wrestling.model.controller.GameController;
import wrestling.model.interfaces.iBrowseMode;
import wrestling.model.interfaces.iSortFilter;
import wrestling.view.utility.ScreenCode;
import wrestling.view.utility.ViewUtils;
import wrestling.view.utility.comparators.DateComparator;
import wrestling.view.utility.comparators.TagTeamNameComparator;
import wrestling.view.utility.comparators.TitleNameComparator;

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
    },
    TITLES {
        @Override
        public ObservableList comparators() {
            return FXCollections.observableArrayList(
                    new TitleNameComparator());
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
    },
    TAG_TEAMS {
        @Override
        public ObservableList comparators() {
            return FXCollections.observableArrayList(
                    new TagTeamNameComparator());
        }

        @Override
        public ScreenCode subScreenCode() {
            return ScreenCode.SIMPLE_DISPLAY;
        }

        @Override
        public List listToBrowse(GameController gameController, Promotion promotion) {
            return gameController.getTagTeamManager().getTagTeams(promotion);
        }

        @Override
        public List<EnumSet> getSortFilters() {
            return Arrays.asList();
        }
    },
    EVENTS {
        @Override
        public ObservableList comparators() {
            return FXCollections.observableArrayList(new DateComparator());
        }

        @Override
        public ScreenCode subScreenCode() {
            return ScreenCode.SIMPLE_DISPLAY;
        }

        @Override
        public List listToBrowse(GameController gameController, Promotion promotion) {
            return gameController.getEventManager().getEventTemplates(promotion);
        }

        @Override
        public List<EnumSet> getSortFilters() {
            return Arrays.asList();
        }
    };

}
