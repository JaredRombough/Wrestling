package openwrestling.model.segment.constants;

import openwrestling.model.interfaces.Description;

public enum TimingType implements Description {

    BEFORE {
        @Override
        public String description() {
            return "Before";
        }

        @Override
        public String result() {
            return "before";
        }

    },
    DURING {
        @Override
        public String description() {
            return "During";
        }

        @Override
        public String result() {
            return "during";
        }

    },
    AFTER {
        @Override
        public String description() {
            return "After";
        }

        @Override
        public String result() {
            return "after";
        }

    };

    public static String label() {
        return "Timing: ";
    }

    @Override
    public String toString() {
        return description();
    }

}
