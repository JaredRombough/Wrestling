package wrestling.model.segmentEnum;

import wrestling.model.interfaces.iSortFilter;
import wrestling.model.interfaces.iStaffType;

public enum StaffType implements iSortFilter, iStaffType {

    ALL {
        @Override
        public String toString() {
            return "All";
        }

        @Override
        public int workerRatio() {
            return 0;
        }
    },
    OWNER {
        @Override
        public String toString() {
            return "Owner";
        }

        @Override
        public int workerRatio() {
            return 0;
        }
    },
    COMMENTARY {
        @Override
        public String toString() {
            return "Commentary";
        }

        @Override
        public int workerRatio() {
            return 0;
        }
    },
    REFEREE {
        @Override
        public String toString() {
            return "Referee";
        }

        @Override
        public int workerRatio() {
            return 0;
        }
    },
    PRODUCTION {
        @Override
        public String toString() {
            return "Production";
        }

        @Override
        public int workerRatio() {
            return 0;
        }
    },
    MEDICAL {
        @Override
        public String toString() {
            return "Medical";
        }

        @Override
        public int workerRatio() {
            return 20;
        }
    },
    CREATIVE {
        @Override
        public String toString() {
            return "Creative";
        }

        @Override
        public int workerRatio() {
            return 25;
        }
    },
    ROAD_AGENT {
        @Override
        public String toString() {
            return "Road Agent";
        }

        @Override
        public int workerRatio() {
            return 30;
        }
    },
    TRAINER {
        @Override
        public String toString() {
            return "Trainer";
        }

        @Override
        public int workerRatio() {
            return 40;
        }
    }

}
