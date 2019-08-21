package openwrestling.model.segmentEnum;

import openwrestling.model.interfaces.iSortFilter;

public enum Gender implements iSortFilter {
    ALL {
        @Override
        public String toString() {
            return "All";
        }
    },
    MALE {
        @Override
        public String toString() {
            return "Male";
        }
    },
    FEMALE {
        @Override
        public String toString() {
            return "Female";
        }
    }

}
