package wrestling.view.utility;

import wrestling.view.utility.interfaces.ScreenCodeInterface;

public enum ScreenCode implements ScreenCodeInterface {
    TITLE {
        @Override
        public String resourcePath() {
            return "view/start/view/TitleScreen.fxml";
        }

        @Override
        public boolean alwaysUpdate() {
            return false;
        }
    },
    START {
        @Override
        public String resourcePath() {
            return "view/start/view/StartGameScreen.fxml";
        }

        @Override
        public boolean alwaysUpdate() {
            return false;
        }
    },
    ROOT {
        @Override
        public String resourcePath() {
            return "view/RootLayout.fxml";
        }

        @Override
        public boolean alwaysUpdate() {
            return true;
        }

    },
    EVENT {

        @Override
        public String resourcePath() {
            return "view/event/view/EventScreen.fxml";
        }

        @Override
        public boolean alwaysUpdate() {
            return false;
        }
    },
    WORKER_OVERVIEW {

        @Override
        public String resourcePath() {
            return "view/browser/view/WorkerOverview.fxml";
        }

        @Override
        public boolean alwaysUpdate() {
            return false;
        }
    }, STAFF_VIEW {

        @Override
        public String resourcePath() {
            return "view/browser/view/StaffView.fxml";
        }

        @Override
        public boolean alwaysUpdate() {
            return false;
        }
    },
    TAG_TEAM_VIEW {

        @Override
        public String resourcePath() {
            return "view/browser/view/TagTeamView.fxml";
        }

        @Override
        public boolean alwaysUpdate() {
            return false;
        }
    },
    TITLE_VIEW {

        @Override
        public String resourcePath() {
            return "view/browser/view/TitleView.fxml";
        }

        @Override
        public boolean alwaysUpdate() {
            return false;
        }
    },
    TITLE_REIGN {

        @Override
        public String resourcePath() {
            return "view/browser/view/TitleReign.fxml";
        }

        @Override
        public boolean alwaysUpdate() {
            return false;
        }
    },
    BROWSER {

        @Override
        public String resourcePath() {
            return "view/browser/view/Browser.fxml";
        }

        @Override
        public boolean alwaysUpdate() {
            return true;
        }
    },
    EDIT_LABEL {

        @Override
        public String resourcePath() {
            return "view/browser/view/EditLabel.fxml";
        }

        @Override
        public boolean alwaysUpdate() {
            return true;
        }
    },
    FINANCIAL {

        @Override
        public String resourcePath() {
            return "view/FinancialScreen.fxml";
        }

        @Override
        public boolean alwaysUpdate() {
            return true;
        }
    },
    CALENDAR {

        @Override
        public String resourcePath() {
            return "view/calendar/view/Calendar.fxml";
        }

        @Override
        public boolean alwaysUpdate() {
            return false;
        }
    },
    SIMPLE_DISPLAY {

        @Override
        public String resourcePath() {
            return "view/SimpleDisplay.fxml";
        }

        @Override
        public boolean alwaysUpdate() {
            return false;
        }
    },
    BOOK_FUTURE_SHOW {

        @Override
        public String resourcePath() {
            return "view/calendar/view/BookShow.fxml";
        }

        @Override
        public boolean alwaysUpdate() {
            return false;
        }
    },
    IMPORT_DIALOG {

        @Override
        public String resourcePath() {
            return "view/start/view/ImportDialog.fxml";
        }

        @Override
        public boolean alwaysUpdate() {
            return false;
        }
    },
    NEXT_DAY_SCREEN {

        @Override
        public String resourcePath() {
            return "view/NextDayScreen.fxml";
        }

        @Override
        public boolean alwaysUpdate() {
            return true;
        }
    },
    RESULTS {
        @Override
        public String resourcePath() {
            return "view/results/view/ResultsScreen.fxml";
        }

        @Override
        public boolean alwaysUpdate() {
            return true;
        }
    },
    RESULTS_CARD {
        @Override
        public String resourcePath() {
            return "view/results/view/ResultsCard.fxml";
        }

        @Override
        public boolean alwaysUpdate() {
            return false;
        }
    },
    RESULTS_DISPLAY {
        @Override
        public String resourcePath() {
            return "view/results/view/ResultsDisplay.fxml";
        }

        @Override
        public boolean alwaysUpdate() {
            return false;
        }
    },
    TEAM_PANE {
        @Override
        public String resourcePath() {
            return "view/event/view/TeamPane.fxml";
        }

        @Override
        public boolean alwaysUpdate() {
            return false;
        }
    },
    TEAM_PANE_WRAPPER {
        @Override
        public String resourcePath() {
            return "view/event/view/TeamPaneWrapper.fxml";
        }

        @Override
        public boolean alwaysUpdate() {
            return false;
        }
    },
    CONTRACT_PANE {
        @Override
        public String resourcePath() {
            return "view/browser/view/ContractPane.fxml";
        }

        @Override
        public boolean alwaysUpdate() {
            return false;
        }
    },
    SEGMENT_PANE {
        @Override
        public String resourcePath() {
            return "view/event/view/SegmentPane.fxml";
        }

        @Override
        public boolean alwaysUpdate() {
            return false;
        }
    },
    MATCH_OPTIONS {
        @Override
        public String resourcePath() {
            return "view/event/view/MatchOptions.fxml";
        }

        @Override
        public boolean alwaysUpdate() {
            return false;
        }
    },
    ANGLE_OPTIONS {
        @Override
        public String resourcePath() {
            return "view/event/view/AngleOptions.fxml";
        }

        @Override
        public boolean alwaysUpdate() {
            return false;
        }
    },
    SORT_CONTROL {
        @Override
        public String resourcePath() {
            return "view/utility/SortControl.fxml";
        }

        @Override
        public boolean alwaysUpdate() {
            return false;
        }
    },
    EVENT_TEMPLATE {
        @Override
        public String resourcePath() {
            return "view/browser/view/EventTemplate.fxml";
        }

        @Override
        public boolean alwaysUpdate() {
            return false;
        }
    }
}
