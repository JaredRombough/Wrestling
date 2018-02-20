package wrestling.view.utility;

import wrestling.view.interfaces.ScreenCodeInterface;

public enum ScreenCode implements ScreenCodeInterface {
    TITLE {
        @Override
        public String resourcePath() {
            return "view/TitleScreen.fxml";
        }

        @Override
        public boolean alwaysUpdate() {
            return false;
        }
    },
    START {
        @Override
        public String resourcePath() {
            return "view/StartGameScreen.fxml";
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
            return "view/EventScreen.fxml";
        }

        @Override
        public boolean alwaysUpdate() {
            return false;
        }
    },
    WORKER_OVERVIEW {

        @Override
        public String resourcePath() {
            return "view/WorkerOverview.fxml";
        }

        @Override
        public boolean alwaysUpdate() {
            return false;
        }
    },
    BROWSER {

        @Override
        public String resourcePath() {
            return "view/Browser.fxml";
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
            return "view/Calendar.fxml";
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
            return "view/BookShow.fxml";
        }

        @Override
        public boolean alwaysUpdate() {
            return false;
        }
    },
    IMPORT_DIALOG {

        @Override
        public String resourcePath() {
            return "view/ImportDialog.fxml";
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
    }
}
