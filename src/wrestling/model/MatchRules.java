package wrestling.model;

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
    }

}
