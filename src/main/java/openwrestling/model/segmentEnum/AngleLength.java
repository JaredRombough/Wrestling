package openwrestling.model.segmentEnum;

import openwrestling.model.interfaces.iSegmentLength;

public enum AngleLength implements iSegmentLength {
    MINIMUM {
        @Override
        public String toString() {
            return "1";
        }

        @Override
        public int value() {
            return 1;
        }
    },
    SHORT {
        @Override
        public String toString() {
            return "3";
        }

        @Override
        public int value() {
            return 3;
        }
    },
    MEDIUM {
        @Override
        public String toString() {
            return "5";
        }

        @Override
        public int value() {
            return 5;
        }
    },
    LONG {
        @Override
        public String toString() {
            return "10";
        }

        @Override
        public int value() {
            return 10;
        }
    },
    MAXIMUM {
        @Override
        public String toString() {
            return "15";
        }

        @Override
        public int value() {
            return 15;
        }
    };
}
