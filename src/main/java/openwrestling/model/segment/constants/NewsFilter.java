package openwrestling.model.segment.constants;

import openwrestling.model.interfaces.iSortFilter;

public enum NewsFilter implements iSortFilter {
    ALL {
        @Override
        public String toString() {
            return "All";
        }
    },
    PLAYER {
        @Override
        public String toString() {
            return "My Promotion";
        }
    }

}
