package openwrestling.model.segment.constants;

import openwrestling.model.interfaces.Description;

public enum SuccessType implements Description {

    WIN {
        @Override
        public String description() {
            return "Win";
        }

        @Override
        public String result() {
            return "and getting the better of the exchange.";
        }

    },
    LOSE {
        @Override
        public String description() {
            return "Lose";
        }

        @Override
        public String result() {
            return "but got run off to the back.";
        }

    },
    DRAW {
        @Override
        public String description() {
            return "Draw";
        }

        @Override
        public String result() {
            return "and proceeding to brawl.";
        }

    };

    public static String label() {
        return "Result: ";
    }

    @Override
    public String toString() {
        return description();
    }

}
