package wrestling.model;

public enum MatchFinishes implements MatchFinish {

    CLEAN {
        @Override
        public boolean nodq() {
            return true;
        }
    },
    DRAW {
        @Override
        public boolean nodq() {
            return true;
        }
    },
    COUNTOUT {
        @Override
        public boolean nodq() {
            return false;
        }
    },
    INTERFERENCE {
        @Override
        public boolean nodq() {
            return true;
        }
    },
    INTERFERENCEBOTCH {
        @Override
        public boolean nodq() {
            return true;
        }
    },
    DQINTERFERENCE {
        @Override
        public boolean nodq() {
            return false;
        }
    },
    DQ {
        @Override
        public boolean nodq() {
            return false;
        }
    },
    CHEATING {
        @Override
        public boolean nodq() {
            return true;
        }
    }

}
