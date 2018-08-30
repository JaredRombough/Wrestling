package wrestling.model.segmentEnum;

public enum StaffType {

    OWNER {
        @Override
        public String toString() {
            return "Owner";
        }
    },
    COMMENTARY {
        @Override
        public String toString() {
            return "Commentary";
        }
    },
    REFEREE {
        @Override
        public String toString() {
            return "Referee";
        }
    },
    PRODUCTION {
        @Override
        public String toString() {
            return "Production";
        }
    },
    MEDICAL {
        @Override
        public String toString() {
            return "Medical";
        }
    },
    CREATIVE {
        @Override
        public String toString() {
            return "Creative";
        }
    },
    ROAD_AGENT {
        @Override
        public String toString() {
            return "Road Agent";
        }
    },
    TRAINER {
        @Override
        public String toString() {
            return "Trainer";
        }
    }

}
