package openwrestling.model.segmentEnum;

import static openwrestling.model.constants.GameConstants.BROADCAST_TEAM_MODIFIER_WEIGHT;
import static openwrestling.model.constants.GameConstants.CREATIVE_MODIFIER_WEIGHT;
import static openwrestling.model.constants.GameConstants.PRODUCTION_MODIFIER_WEIGHT;
import static openwrestling.model.constants.GameConstants.REF_MODIFIER_WEIGHT;
import static openwrestling.model.constants.GameConstants.ROAD_AGENT_MODIFIER_WEIGHT;
import openwrestling.model.interfaces.iSortFilter;
import openwrestling.model.interfaces.iStaffType;

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
            return BROADCAST_TEAM_MODIFIER_WEIGHT;
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
            return REF_MODIFIER_WEIGHT;
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
            return PRODUCTION_MODIFIER_WEIGHT;
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
            return CREATIVE_MODIFIER_WEIGHT;
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
            return ROAD_AGENT_MODIFIER_WEIGHT;
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
