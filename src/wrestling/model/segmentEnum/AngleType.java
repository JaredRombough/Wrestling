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

        @Override
        public TeamType mainTeamType() {
            return TeamType.PROMO;
        }

        @Override
        public TeamType addTeamType() {
            return TeamType.PROMO_TARGET;
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

        @Override
        public TeamType mainTeamType() {
            return TeamType.ATTACKER;
        }

        @Override
        public TeamType addTeamType() {
            return TeamType.VICTIM;
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

        @Override
        public TeamType mainTeamType() {
            return TeamType.OFFERER;
        }

        @Override
        public TeamType addTeamType() {
            return TeamType.OFFEREE;
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

        @Override
        public TeamType mainTeamType() {
            return TeamType.BRAWLER;
        }

        @Override
        public TeamType addTeamType() {
            return TeamType.BRAWLER;
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

        @Override
        public TeamType mainTeamType() {
            return TeamType.CHALLENGER;
        }

        @Override
        public TeamType addTeamType() {
            return TeamType.CHALLENGED;
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

        @Override
        public TeamType mainTeamType() {
            return TeamType.ANNOUNCER;
        }

        @Override
        public TeamType addTeamType() {
            return TeamType.AUDIENCE;
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
