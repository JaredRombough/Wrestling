package wrestling.model.segmentEnum;

import wrestling.model.interfaces.Description;

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

    @Override
    public String toString() {
        return description();
    }

    public static String label() {
        return "Join: ";
    }

}
