package wrestling.model.segmentEnum;

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

    @Override
    public String toString() {
        return description();
    }

    public static String label() {
        return "Presence: ";
    }

}
