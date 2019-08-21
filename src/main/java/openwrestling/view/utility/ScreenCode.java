package openwrestling.view.utility;

import openwrestling.view.utility.interfaces.ScreenCodeInterface;

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
    }, STAFF_VIEW {
        @Override
        public String resourcePath() {
            return "view/browser/StaffView.fxml";
        }

        @Override
        public boolean alwaysUpdate() {
            return false;
        }
    },
    TAG_TEAM_VIEW {
        @Override
        public String resourcePath() {
            return "view/browser/TagTeamView.fxml";
        }

        @Override
        public boolean alwaysUpdate() {
            return false;
        }
    },
    STABLE {
        @Override
        public String resourcePath() {
            return "view/browser/Stable.fxml";
        }

        @Override
        public boolean alwaysUpdate() {
            return false;
        }
    },
    GROUP_MEMBER {
        @Override
        public String resourcePath() {
            return "view/browser/GroupMember.fxml";
        }

        @Override
        public boolean alwaysUpdate() {
            return false;
        }
    },
    TITLE_VIEW {
        @Override
        public String resourcePath() {
            return "view/browser/TitleView.fxml";
        }

        @Override
        public boolean alwaysUpdate() {
            return false;
        }
    },
    TITLE_REIGN {
        @Override
        public String resourcePath() {
            return "view/browser/TitleReign.fxml";
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
    EDIT_LABEL {
        @Override
        public String resourcePath() {
            return "view/browser/EditLabel.fxml";
        }

        @Override
        public boolean alwaysUpdate() {
            return true;
        }
    },
    FINANCIAL {
        @Override
        public String resourcePath() {
            return "view/financial/FinancialScreen.fxml";
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
    NEWS {
        @Override
        public String resourcePath() {
            return "view/news/NewsScreen.fxml";
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
    TEAM_PANE_WRAPPER {
        @Override
        public String resourcePath() {
            return "view/event/TeamPaneWrapper.fxml";
        }

        @Override
        public boolean alwaysUpdate() {
            return false;
        }
    },
    CONTRACT {
        @Override
        public String resourcePath() {
            return "view/browser/Contract.fxml";
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
    },
    MATCH_OPTIONS {
        @Override
        public String resourcePath() {
            return "view/event/MatchOptions.fxml";
        }

        @Override
        public boolean alwaysUpdate() {
            return false;
        }
    },
    ANGLE_OPTIONS {
        @Override
        public String resourcePath() {
            return "view/event/AngleOptions.fxml";
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
            return "view/browser/EventTemplate.fxml";
        }

        @Override
        public boolean alwaysUpdate() {
            return false;
        }
    },
    DEPARTMENT {
        @Override
        public String resourcePath() {
            return "view/financial/Department.fxml";
        }

        @Override
        public boolean alwaysUpdate() {
            return false;
        }
    },
    RINGSIDE {
        @Override
        public String resourcePath() {
            return "view/financial/Ringside.fxml";
        }

        @Override
        public boolean alwaysUpdate() {
            return false;
        }
    }
}
