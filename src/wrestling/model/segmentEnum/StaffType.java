package wrestling.model.segmentEnum;

import static wrestling.model.constants.GameConstants.BROADCAST_TEAM_DIFF_RATIO;
import static wrestling.model.constants.GameConstants.CREATIVE_DIFF_RATIO;
import static wrestling.model.constants.GameConstants.PRODUCTION_DIFF_RATIO;
import static wrestling.model.constants.GameConstants.REF_DIFF_RATIO;
import static wrestling.model.constants.GameConstants.ROAD_AGENT_DIFF_RATIO;
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

        @Override
        public int diffRatio() {
            throw new UnsupportedOperationException("Not supported yet.");
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

        @Override
        public int diffRatio() {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    },
    BROADCAST {
        @Override
        public String toString() {
            return "Broadcast";
        }

        @Override
        public int workerRatio() {
            return 0;
        }

        @Override
        public int diffRatio() {
            return BROADCAST_TEAM_DIFF_RATIO;
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

        @Override
        public int diffRatio() {
            return REF_DIFF_RATIO;
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

        @Override
        public int diffRatio() {
            return PRODUCTION_DIFF_RATIO;
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

        @Override
        public int diffRatio() {
            return 0;
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

        @Override
        public int diffRatio() {
            return CREATIVE_DIFF_RATIO;
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

        @Override
        public int diffRatio() {
            return ROAD_AGENT_DIFF_RATIO;
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

        @Override
        public int diffRatio() {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }

}
