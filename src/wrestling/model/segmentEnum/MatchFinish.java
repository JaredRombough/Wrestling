package wrestling.model.segmentEnum;

import wrestling.model.interfaces.iMatchFinish;

public enum MatchFinish implements iMatchFinish {

    CLEAN {
        @Override
        public boolean nodq() {
            return true;
        }

        @Override
        public String description() {
            return "Clean";
        }
    },
    DRAW {
        @Override
        public boolean nodq() {
            return true;
        }

        @Override
        public String description() {
            return "Draw";
        }
    },
    COUNTOUT {
        @Override
        public boolean nodq() {
            return false;
        }

        @Override
        public String description() {
            return "Count Out";
        }
    },
    INTERFERENCE {
        @Override
        public boolean nodq() {
            return true;
        }

        @Override
        public String description() {
            return "Interference";
        }
    },
    INTERFERENCEBOTCH {
        @Override
        public boolean nodq() {
            return true;
        }

        @Override
        public String description() {
            return "Botched Interference";
        }
    },
    DQINTERFERENCE {
        @Override
        public boolean nodq() {
            return false;
        }

        @Override
        public String description() {
            return "DQ (Interference)";
        }
    },
    DQ {
        @Override
        public boolean nodq() {
            return false;
        }

        @Override
        public String description() {
            return "DQ";
        }
    },
    CHEATING {
        @Override
        public boolean nodq() {
            return true;
        }

        @Override
        public String description() {
            return "Cheating";
        }
    };

    @Override
    public String toString() {
        return description();
    }

}
