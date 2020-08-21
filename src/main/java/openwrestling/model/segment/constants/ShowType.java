package openwrestling.model.segment.constants;

import openwrestling.model.interfaces.Description;

public enum ShowType implements Description {

    TONIGHT {
        @Override
        public String description() {
            return "Tonight";
        }

        @Override
        public String result() {
            return "";
        }

    },
    NEXT_SHOW {
        @Override
        public String description() {
            return "Next Show";
        }

        @Override
        public String result() {
            return "";
        }

    },
    NEXT_BIG_SHOW {
        @Override
        public String description() {
            return "Next Big Show";
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
