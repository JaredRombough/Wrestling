package wrestling.view.utility;

public enum ScreenCode implements ScreenCodeInterface {
    TITLE {
        @Override
        public String resource() {
            return "view/TitleScreen.fxml";
        }

        @Override
        public boolean alwaysUpdate() {
            return false;
        }
    },
    START {
        @Override
        public String resource() {
            return "view/StartGameScreen.fxml";
        }

        @Override
        public boolean alwaysUpdate() {
            return false;
        }
    },
    ROOT {
        @Override
        public String resource() {
            return "view/RootLayout.fxml";
        }

        @Override
        public boolean alwaysUpdate() {
            return true;
        }
        
    },
    EVENT {

        @Override
        public String resource() {
            return "view/EventScreen.fxml";
        }

        @Override
        public boolean alwaysUpdate() {
            return true;
        }
    },
    WORKER_OVERVIEW {

        @Override
        public String resource() {
            return "view/WorkerOverview.fxml";
        }

        @Override
        public boolean alwaysUpdate() {
            return false;
        }
    },
    BROWSER {

        @Override
        public String resource() {
            return "view/Browser.fxml";
        }

        @Override
        public boolean alwaysUpdate() {
            return true;
        }
    },
    FINANCIAL {

        @Override
        public String resource() {
            return "view/FinancialScreen.fxml";
        }

        @Override
        public boolean alwaysUpdate() {
            return true;
        }
    },
    CALENDAR {

        @Override
        public String resource() {
            return "view/Calendar.fxml";
        }

        @Override
        public boolean alwaysUpdate() {
            return true;
        }
    }, 
    SIMPLE_DISPLAY {

        @Override
        public String resource() {
            return "view/SimpleDisplay.fxml";
        }

        @Override
        public boolean alwaysUpdate() {
            return false;
        }
    },
    BOOK_FUTURE_SHOW {

        @Override
        public String resource() {
            return "view/BookShow.fxml";
        }

        @Override
        public boolean alwaysUpdate() {
            return false;
        }
    }
}
