package wrestling.model.segmentEnum;

import wrestling.model.interfaces.iAngleType;

public enum AngleType implements Description, iAngleType {

    PROMO {
        @Override
        public String description() {
            return "Promo";
        }

        @Override
        public String result() {
            return "";
        }

        @Override
        public int minWorkers() {
            return 1;
        }

        @Override
        public int defaultWorkers() {
            return 1;
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

        @Override
        public int minWorkers() {
            return 2;
        }

        @Override
        public int defaultWorkers() {
            return 2;
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

        @Override
        public int minWorkers() {
            return 2;
        }

        @Override
        public int defaultWorkers() {
            return 2;
        }

    },
    BRAWL {
        @Override
        public String description() {
            return "Brawl";
        }

        @Override
        public String result() {
            return "";
        }

        @Override
        public int minWorkers() {
            return 2;
        }

        @Override
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

        @Override
        public int minWorkers() {
            return 2;
        }

        @Override
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

        @Override
        public int minWorkers() {
            return 1;
        }

        @Override
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
