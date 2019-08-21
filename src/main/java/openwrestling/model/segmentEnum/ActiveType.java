
package openwrestling.model.segmentEnum;

public enum ActiveType {
    ALL {
        @Override
        public String toString() {
            return "All";
        }
    },
    ACTIVE {
        @Override
        public String toString() {
            return "Active";
        }
    },
    INACTIVE {
        @Override
        public String toString() {
            return "Inactive";
        }

    }

}
