package wrestling.view.utility;

import wrestling.view.utility.interfaces.ScreenCodeInterface;

public enum ScreenCode implements ScreenCodeInterface {
    TITLE {
        @Override
        public String resourcePath() {
            return "view/start/TitleScreen.fxml";
        }

        @Override
        public boolean alwaysUpdate() {
            return false;
        }
    },
    START {
        @Override
        public String resourcePath() {
            return "view/start/StartGameScreen.fxml";
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
            return "view/event/EventScreen.fxml";
        }

        @Override
        public boolean alwaysUpdate() {
            return false;
        }
    },
    WORKER_OVERVIEW {

        @Override
        public String resourcePath() {
            return "view/browser/WorkerOverview.fxml";
        }

        @Override
        public boolean alwaysUpdate() {
            return false;
        }
    },
    BROWSER {

        @Override
        public String resourcePath() {
            return "view/browser/Browser.fxml";
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
            return "view/calendar/Calendar.fxml";
        }

        @Override
        public boolean alwaysUpdate() {
            return true;
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
            return "view/calendar/BookShow.fxml";
        }

        @Override
        public boolean alwaysUpdate() {
            return false;
        }
    },
    IMPORT_DIALOG {

        @Override
        public String resourcePath() {
            return "view/start/ImportDialog.fxml";
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
            return "view/results/ResultsScreen.fxml";
        }

        @Override
        public boolean alwaysUpdate() {
            return true;
        }
    },
    RESULTS_CARD {
        @Override
        public String resourcePath() {
            return "view/results/ResultsCard.fxml";
        }

        @Override
        public boolean alwaysUpdate() {
            return false;
        }
    },
    RESULTS_DISPLAY {
        @Override
        public String resourcePath() {
            return "view/results/ResultsDisplay.fxml";
        }

        @Override
        public boolean alwaysUpdate() {
            return false;
        }
    },
    TEAM_PANE {
        @Override
        public String resourcePath() {
            return "view/event/TeamPane.fxml";
        }

        @Override
        public boolean alwaysUpdate() {
            return false;
        }
    },
    CONTRACT_PANE {
        @Override
        public String resourcePath() {
            return "view/browser/ContractPane.fxml";
        }

        @Override
        public boolean alwaysUpdate() {
            return false;
        }
    },
    SEGMENT_PANE {
        @Override
        public String resourcePath() {
            return "view/event/SegmentPane.fxml";
        }

        @Override
        public boolean alwaysUpdate() {
            return false;
        }
    }
}
