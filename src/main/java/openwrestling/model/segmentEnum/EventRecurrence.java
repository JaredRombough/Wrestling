package openwrestling.model.segmentEnum;

import openwrestling.model.interfaces.Description;

public enum EventRecurrence implements Description {
    UNLIMITED {
        @Override
        public String description() {
            return "Unlimited";
        }
    },
    LIMITED {
        @Override
        public String description() {
            return "Limited";
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
