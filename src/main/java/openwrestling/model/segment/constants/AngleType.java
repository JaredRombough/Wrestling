package openwrestling.model.segment.constants;

import openwrestling.model.interfaces.Description;
import openwrestling.model.interfaces.iAngleType;

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

        @Override
        public String resultString() {
            return "%s cut%s a promo targeting %s";
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

        @Override
        public String resultString() {
            return "%s attack%s %s";
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
            return TeamType.BRAWLER_PRIME;
        }

        @Override
        public TeamType addTeamType() {
            return TeamType.BRAWLER;
        }

        @Override
        public String resultString() {
            return "%s brawl%s with %s";
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

        @Override
        public String resultString() {
            return "%s offer%s %s";
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

        @Override
        public String resultString() {
            return "%s challenge%s %s";
        }

    };

    public static String label() {
        return "Type: ";
    }

    @Override
    public String toString() {
        return description();
    }

}
