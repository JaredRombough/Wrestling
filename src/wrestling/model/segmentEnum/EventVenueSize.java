package wrestling.model.segmentEnum;

import wrestling.model.interfaces.Description;

public enum EventVenueSize implements Description {
    SMALL {
        @Override
        public String description() {
            return "Small";
        }
    },
    MEDIUM {
        @Override
        public String description() {
            return "Medium";
        }
    },
    LARGE {
        @Override
        public String description() {
            return "Large";
        }
    };

    @Override
    public String toString() {
        return description();
    }

    @Override
    public String result() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
