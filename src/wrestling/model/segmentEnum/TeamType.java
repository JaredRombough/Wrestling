package wrestling.model.segmentEnum;

public enum TeamType implements Description {
    CHALLENGER {
        @Override
        public String description() {
            return "Challenger";
        }

        @Override
        public String result() {
            return "";
        }

    },
    CHALLENGED {
        @Override
        public String description() {
            return "Challenged";
        }

        @Override
        public String result() {
            return "";
        }
    },
    ATTACKER {
        @Override
        public String description() {
            return "Attacker";
        }

        @Override
        public String result() {
            return "";
        }

    },
    VICTIM {
        @Override
        public String description() {
            return "Victim";
        }

        @Override
        public String result() {
            return "";
        }
    },
    PROMO {
        @Override
        public String description() {
            return "Promo";
        }

        @Override
        public String result() {
            return "";
        }
    },
    BRAWLER_PRIME {
        @Override
        public String description() {
            return "Brawler";
        }

        @Override
        public String result() {
            return "";
        }
    },
    BRAWLER {
        @Override
        public String description() {
            return "Brawler";
        }

        @Override
        public String result() {
            return "";
        }
    },
    PROMO_TARGET {
        @Override
        public String description() {
            return "Promo Target";
        }

        @Override
        public String result() {
            return "";
        }
    },
    OFFERER {
        @Override
        public String description() {
            return "Offerer";
        }

        @Override
        public String result() {
            return "";
        }
    },
    OFFEREE {
        @Override
        public String description() {
            return "Offeree";
        }

        @Override
        public String result() {
            return "";
        }
    },
    INTERFERENCE {
        @Override
        public String description() {
            return "Interference";
        }

        @Override
        public String result() {
            return "";
        }
    },
    INTERVIEWER {
        @Override
        public String description() {
            return "Interviewer";
        }

        @Override
        public String result() {
            return "";
        }
    },
    ANNOUNCER {
        @Override
        public String description() {
            return "Announcer";
        }

        @Override
        public String result() {
            return "";
        }
    },
    AUDIENCE {
        @Override
        public String description() {
            return "Audience";
        }

        @Override
        public String result() {
            return "";
        }
    },
    DEFAULT {
        @Override
        public String description() {
            return "Default";
        }

        @Override
        public String result() {
            return "";
        }
    },
    WINNER {
        @Override
        public String description() {
            return "Winner";
        }

        @Override
        public String result() {
            return "";
        }
    },
    LOSER {
        @Override
        public String description() {
            return "Loser";
        }

        @Override
        public String result() {
            return "";
        }
    },
    DRAW {
        @Override
        public String description() {
            return "Draw";
        }

        @Override
        public String result() {
            return "";
        }
    },
    EVERYONE {
        @Override
        public String description() {
            return "Everyone";
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

    public boolean isMatch() {
        return this.equals(WINNER) || this.equals(LOSER) || this.equals(DRAW);
    }
}
