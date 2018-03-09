package wrestling.view.event;

public enum PresenceType implements Description {

    PRESENT {
        @Override
        public String description() {
            return "Present";
        }

        @Override
        public String result() {
            return "";
        }

    },
    NOT_PRESENT {
        @Override
        public String description() {
            return "Not Present";
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
