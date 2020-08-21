package openwrestling.model.segment.constants;

import openwrestling.model.interfaces.Description;

public enum OutcomeType implements Description {

    WINNER {
        @Override
        public String description() {
            return "Winner";
        }

        @Override
        public String result() {
            return "";
        }

    },
    LOSER {
        @Override
        public String description() {
            return "Loser";
        }

        @Override
        public String result() {
            return "";
        }

    },
    DRAW {
        @Override
        public String description() {
            return "Draw";
        }

        @Override
        public String result() {
            return "";
        }

    };

    public static String label() {
        return "Join: ";
    }

    @Override
    public String toString() {
        return description();
    }

}
