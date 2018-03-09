package wrestling.view.event;

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

    }

}
