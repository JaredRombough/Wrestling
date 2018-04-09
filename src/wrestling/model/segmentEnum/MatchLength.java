package wrestling.model.segmentEnum;

import wrestling.model.interfaces.iSegmentLength;

public enum MatchLength implements iSegmentLength {
    FIVE {
        @Override
        public String toString() {
            return "5";
        }

        @Override
        public int value() {
            return 5;
        }
    },
    FIFTEEN {
        @Override
        public String toString() {
            return "15";
        }

        @Override
        public int value() {
            return 15;
        }
    },
    THIRTY {
        @Override
        public String toString() {
            return "30";
        }

        @Override
        public int value() {
            return 30;
        }
    },
    FORTYFIVE {
        @Override
        public String toString() {
            return "45";
        }

        @Override
        public int value() {
            return 45;
        }
    },
    SIXTY {
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
