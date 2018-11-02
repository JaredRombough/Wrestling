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
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    },
    OWNER {
        @Override
        public String toString() {
            return "Owner";
        }

        @Override
        public int workerRatio() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    },
    COMMENTARY {
        @Override
        public String toString() {
            return "Commentary";
        }

        @Override
        public int workerRatio() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    },
    REFEREE {
        @Override
        public String toString() {
            return "Referee";
        }

        @Override
        public int workerRatio() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    },
    PRODUCTION {
        @Override
        public String toString() {
            return "Production";
        }

        @Override
        public int workerRatio() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
