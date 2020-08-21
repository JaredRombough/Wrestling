package openwrestling.model.segment.constants;

import openwrestling.model.interfaces.Description;

public enum PresenceType implements Description {

    ABSENT {
        @Override
        public String description() {
            return "Absent";
        }

        @Override
        public String result() {
            return "";
        }

    },
    PRESENT {
        @Override
        public String description() {
            return "Respond";
        }

        @Override
        public String result() {
            return "";
        }

    };

    public static String label() {
        return "Presence: ";
    }

    @Override
    public String toString() {
        return description();
    }

}
