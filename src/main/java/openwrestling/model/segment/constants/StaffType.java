package openwrestling.model.segment.constants;

import openwrestling.model.interfaces.iSortFilter;
import openwrestling.model.interfaces.iStaffType;

import static openwrestling.model.constants.GameConstants.BROADCAST_TEAM_MODIFIER_WEIGHT;
import static openwrestling.model.constants.GameConstants.REF_MODIFIER_WEIGHT;

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
            throw new UnsupportedOperationException();
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
            throw new UnsupportedOperationException();
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
    }

}
