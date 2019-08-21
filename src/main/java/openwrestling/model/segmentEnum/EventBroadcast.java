package openwrestling.model.segmentEnum;

import openwrestling.model.interfaces.Description;

public enum EventBroadcast implements Description {
    NONE {
        @Override
        public String description() {
            return "None";
        }
    },
    TAPE {
        @Override
        public String description() {
            return "Taped";
        }
    },
    TELEVISION {
        @Override
        public String description() {
            return "Television";
        }
    },
    PPV {
        @Override
        public String description() {
            return "Pay-Per-View";
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
