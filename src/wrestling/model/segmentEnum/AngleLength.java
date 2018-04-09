package wrestling.model.segmentEnum;

import wrestling.model.interfaces.iSegmentLength;

public enum AngleLength implements iSegmentLength {
    ONE {
        @Override
        public String toString() {
            return "1";
        }

        @Override
        public int value() {
            return 1;
        }
    },
    THREE {
        @Override
        public String toString() {
            return "3";
        }

        @Override
        public int value() {
            return 3;
        }
    },
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
    TEN {
        @Override
        public String toString() {
            return "10";
        }

        @Override
        public int value() {
            return 10;
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
    };
}
