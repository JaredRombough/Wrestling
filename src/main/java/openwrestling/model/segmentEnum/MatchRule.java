package openwrestling.model.segmentEnum;

import openwrestling.model.interfaces.iMatchRule;

public enum MatchRule implements iMatchRule {

    DEFAULT {
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
        public int getInjuryModifier() {
            return 10;
        }

        @Override
        public int getStrikingModifier() {
            return 10;
        }

        @Override
        public int getFlyingModifier() {
            return 5;
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
        public int getInjuryModifier() {
            return -15;
        }

        @Override
        public int getStrikingModifier() {
            return -10;
        }

        @Override
        public int getFlyingModifier() {
            return -10;
        }

        @Override
        public int getWrestingModifier() {
            return 15;
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
        public int getInjuryModifier() {
            return 10;
        }

        @Override
        public int getFlyingModifier() {
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
        public int getInjuryModifier() {
            return 10;
        }

        @Override
        public int getWrestingModifier() {
            return -5;
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
        public int getInjuryModifier() {
            return 10;
        }

        @Override
        public int getStrikingModifier() {
            return 5;
        }

        @Override
        public int getFlyingModifier() {
            return 10;
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
        public int getInjuryModifier() {
            return 5;
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
        public boolean nodq() {
            return false;
        }

        @Override
        public int getWrestingModifier() {
            return 5;
        }

        @Override
        public String description() {
            return "Ironman";
        }
    },
    IQUIT {
        @Override
        public int getInjuryModifier() {
            return 10;
        }

        @Override
        public int getStrikingModifier() {
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
        public int getInjuryModifier() {
            return 15;
        }

        @Override
        public int getStrikingModifier() {
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
        public int getInjuryModifier() {
            return 15;
        }

        @Override
        public int getStrikingModifier() {
            return 10;
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
        public int getInjuryModifier() {
            return 20;
        }

        @Override
        public int getStrikingModifier() {
            return 15;
        }

        @Override
        public int getFlyingModifier() {
            return -5;
        }

        @Override
        public int getWrestingModifier() {
            return -5;
        }

        @Override
        public boolean nodq() {
            return true;
        }

        @Override
        public String description() {
            return "Barbed Wire";
        }
    };

    @Override
    public String toString() {
        return description();
    }

}
