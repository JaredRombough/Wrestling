package wrestling.model.segmentEnum;

import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import wrestling.model.Promotion;
import wrestling.model.controller.GameController;
import wrestling.model.interfaces.iBrowseMode;
import wrestling.view.utility.ScreenCode;
import wrestling.view.utility.ViewUtils;
import wrestling.view.utility.comparators.EventDateComparator;
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
    },
    EVENTS {
        @Override
        public ObservableList comparators() {
            return FXCollections.observableArrayList(
                    new EventDateComparator());
        }

        @Override
        public ScreenCode subScreenCode() {
            return ScreenCode.SIMPLE_DISPLAY;
        }
        
        @Override
        public List listToBrowse(GameController gameController, Promotion promotion) {
            return gameController.getEventManager().getEvents(promotion);
        }
    };

}
