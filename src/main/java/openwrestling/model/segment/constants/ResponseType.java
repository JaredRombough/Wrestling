package openwrestling.model.segment.constants;

import openwrestling.model.interfaces.Description;

public enum ResponseType implements Description {

    YES {
        @Override
        public String description() {
            return "Yes";
        }

        @Override
        public String result() {
            return "";
        }

    },
    NO {
        @Override
        public String description() {
            return "No";
        }

        @Override
        public String result() {
            return "";
        }

    },
    PUSH {
        @Override
        public String description() {
            return "Push";
        }

        @Override
        public String result() {
            return "";
        }

    };

    public static String label() {
        return "Show: ";
    }

    @Override
    public String toString() {
        return description();
    }

}
