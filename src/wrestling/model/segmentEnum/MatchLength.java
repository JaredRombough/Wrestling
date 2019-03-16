package wrestling.model.segmentEnum;

import wrestling.model.interfaces.iSegmentLength;

public enum MatchLength implements iSegmentLength {
    MINIMUM {
        @Override
        public String toString() {
            return "5";
        }

        @Override
        public int value() {
            return 5;
        }
    },
    SHORT {
        @Override
        public String toString() {
            return "15";
        }

        @Override
        public int value() {
            return 15;
        }
    },
    MEDIUM {
        @Override
        public String toString() {
            return "30";
        }

        @Override
        public int value() {
            return 30;
        }
    },
    LONG {
        @Override
        public String toString() {
            return "45";
        }

        @Override
        public int value() {
            return 45;
        }
    },
    MAXIMUM {
        @Override
        public String toString() {
            return "60";
        }

        @Override
        public int value() {
            return 60;
        }
    };
}
