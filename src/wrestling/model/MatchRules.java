package wrestling.model;

import wrestling.model.interfaces.MatchRule;

public enum MatchRules implements MatchRule {

    DEFAULT {
        @Override
        public int injuryRate() {
            return 1;
        }

        @Override
        public boolean nodq() {
            return false;
        }

        @Override
        public String description() {
            return "Standard";
        }

    },
    HARDCORE {
        @Override
        public int injuryRate() {
            return 8;
        }

        @Override
        public boolean nodq() {
            return true;
        }

        @Override
        public String description() {
            return "Hardcore";
        }

    },
    SUBMISSION {
        @Override
        public int injuryRate() {
            return 1;
        }

        @Override
        public boolean nodq() {
            return false;
        }

        @Override
        public String description() {
            return "Submission";
        }

    },
    LADDER {
        @Override
        public int injuryRate() {
            return 10;
        }

        @Override
        public boolean nodq() {
            return true;
        }

        @Override
        public String description() {
            return "Ladder";
        }

    },
    TABLES {
        @Override
        public int injuryRate() {
            return 5;
        }

        @Override
        public boolean nodq() {
            return true;
        }

        @Override
        public String description() {
            return "Tables";
        }

    },
    CAGE {
        @Override
        public int injuryRate() {
            return 6;
        }

        @Override
        public boolean nodq() {
            return true;
        }

        @Override
        public String description() {
            return "Cage";
        }
    },
    LASTMANSTANDING {
        @Override
        public int injuryRate() {
            return 3;
        }

        @Override
        public boolean nodq() {
            return true;
        }

        @Override
        public String description() {
            return "Last Man Standing";
        }
    },
    IRONMAN {
        @Override
        public int injuryRate() {
            return 2;
        }

        @Override
        public boolean nodq() {
            return false;
        }

        @Override
        public String description() {
            return "Ironman";
        }
    },
    IQUIT {
        @Override
        public int injuryRate() {
            return 5;
        }

        @Override
        public boolean nodq() {
            return true;
        }

        @Override
        public String description() {
            return "I Quit";
        }

    },
    INFERNO {
        @Override
        public int injuryRate() {
            return 10;
        }

        @Override
        public boolean nodq() {
            return true;
        }

        @Override
        public String description() {
            return "Inferno";
        }
    },
    FIRSTBLOOD {
        @Override
        public int injuryRate() {
            return 6;
        }

        @Override
        public boolean nodq() {
            return true;
        }

        @Override
        public String description() {
            return "First Blood";
        }
    },
    BARBEDWIRE {
        @Override
        public int injuryRate() {
            return 10;
        }

        @Override
        public boolean nodq() {
            return true;
        }

        @Override
        public String description() {
            return "Barbed Wire";
        }
    }

}
