package wrestling.model.segmentEnum;

import wrestling.model.interfaces.iSortFilter;

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
