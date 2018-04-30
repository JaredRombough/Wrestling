package wrestling.model.segmentEnum;

import wrestling.model.interfaces.Description;

public enum EventFrequency implements Description {
    WEEKLY {
        @Override
        public String description() {
            return "Weekly";
        }
    },
    ANNUAL {
        @Override
        public String description() {
            return "Annual";
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
