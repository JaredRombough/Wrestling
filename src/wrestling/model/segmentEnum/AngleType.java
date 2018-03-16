package wrestling.model.segmentEnum;

public enum AngleType implements Description {

    PROMO {
        @Override
        public String description() {
            return "Promo";
        }

        @Override
        public String result() {
            return "";
        }

        public int minWorkers() {
            return 1;
        }

        public int defaultWorkers() {
            return 1;
        }

    },
    OFFER {
        @Override
        public String description() {
            return "Offer";
        }

        @Override
        public String result() {
            return "";
        }

        public int minWorkers() {
            return 2;
        }

        public int defaultWorkers() {
            return 2;
        }

    },
    CHALLENGE {
        @Override
        public String description() {
            return "Challenge";
        }

        @Override
        public String result() {
            return "";
        }

        public int minWorkers() {
            return 2;
        }

        public int defaultWorkers() {
            return 2;
        }

    },
    ANNOUNCEMENT {
        @Override
        public String description() {
            return "Announcement";
        }

        @Override
        public String result() {
            return "";
        }

        public int minWorkers() {
            return 1;
        }

        public int defaultWorkers() {
            return 1;
        }

    };

    @Override
    public String toString() {
        return description();
    }

    public static String label() {
        return "Type: ";
    }

}
