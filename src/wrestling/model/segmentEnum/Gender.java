package wrestling.model.segmentEnum;

public enum Gender {
    ALL {
        @Override
        public String toString() {
            return "All";
        }
    },
    MALE {
        @Override
        public String toString() {
            return "Male";
        }
    },
    FEMALE {
        @Override
        public String toString() {
            return "Female";
        }
    }

}
