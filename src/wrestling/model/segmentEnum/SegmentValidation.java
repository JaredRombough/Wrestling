package wrestling.model.segmentEnum;

import wrestling.model.interfaces.iSegmentValidation;

public enum SegmentValidation implements iSegmentValidation {
    EMPTY {
        @Override
        public String toString() {
            return "Empty";
        }

        @Override
        public String getCss() {
            return "lowStat";
        }

        @Override
        public String getSymbol() {
            return "!";
        }
    },
    INCOMPLETE {
        @Override
        public String toString() {
            return "Incomplete";
        }

        @Override
        public String getCss() {
            return "midStat";
        }

        @Override
        public String getSymbol() {
            return "!";
        }
    },
    COMPLETE {
        @Override
        public String toString() {
            return "Complete";
        }

        @Override
        public String getCss() {
            return "highStat";
        }

        @Override
        public String getSymbol() {
            return "✔";
        }
    };
}
