package openwrestling.model.segment.constants;

import openwrestling.model.interfaces.iSortFilter;

public enum TopMatchFilter implements iSortFilter {
    WEEK {
        @Override
        public String toString() {
            return "Week";
        }
    },
    MONTH {
        @Override
        public String toString() {
            return "Month";
        }
    },
    YEAR {
        @Override
        public String toString() {
            return "Year";
        }
    }

}
