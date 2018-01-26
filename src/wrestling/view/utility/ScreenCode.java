package wrestling.view.utility;

public enum ScreenCode implements ScreenCodeInterface {
    EVENT_SCREEN {

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
    }
}
