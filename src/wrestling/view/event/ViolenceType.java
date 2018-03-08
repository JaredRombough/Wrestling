package wrestling.view.event;

public enum ViolenceType implements Description {

    NO_BUMP {
        @Override
        public String description() {
            return "No Bump";
        }

    },
    ATTACK {
        @Override
        public String description() {
            return "Attack";
        }

    },
    DEFEND {
        @Override
        public String description() {
            return "Defend";
        }

    }

}
