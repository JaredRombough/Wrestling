package wrestling.model.segmentEnum;

import wrestling.model.interfaces.Description;

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

    @Override
    public String toString() {
        return description();
    }
    
    public static String label() {
        return "Violence: ";
    }

}
