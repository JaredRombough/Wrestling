package openwrestling.model.segment.constants;

import openwrestling.model.interfaces.Description;

public enum JoinTeamType implements Description {

    TAG_TEAM {
        @Override
        public String description() {
            return "New Tag Team";
        }

        @Override
        public String result() {
            return "";
        }

    },
    NEW_STABLE {
        @Override
        public String description() {
            return "New Stable";
        }

        @Override
        public String result() {
            return "";
        }

    },
    STABLE {
        @Override
        public String description() {
            return "Existing Stable";
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
