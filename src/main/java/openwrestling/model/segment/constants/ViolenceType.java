package openwrestling.model.segment.constants;

import openwrestling.model.interfaces.Description;

public enum ViolenceType implements Description {

    NO_BUMP {
        @Override
        public String description() {
            return "No Bump";
        }

        @Override
        public String result() {
            return "";
        }

    },
    ATTACK {
        @Override
        public String description() {
            return "Attack";
        }

        @Override
        public String result() {
            return "";
        }

    },
    DEFEND {
        @Override
        public String description() {
            return "Defend";
        }

        @Override
        public String result() {
            return "";
        }

    };

    public static String label() {
        return "Violence: ";
    }

    @Override
    public String toString() {
        return description();
    }

}
