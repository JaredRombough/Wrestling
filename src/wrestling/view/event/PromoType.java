package wrestling.view.event;

public enum PromoType implements Description {

    PROMO {
        @Override
        public String description() {
            return "Promo";
        }

        @Override
        public String result() {
            return "";
        }

    },
    OFFER {
        @Override
        public String description() {
            return "Offer";
        }

        @Override
        public String result() {
            return "";
        }

    },
    CHALLENGE {
        @Override
        public String description() {
            return "Challenge";
        }

        @Override
        public String result() {
            return "";
        }

    },
    ANNOUNCEMENT {
        @Override
        public String description() {
            return "Announcement";
        }

        @Override
        public String result() {
            return "";
        }

    };
    
    @Override
    public String toString() {
        return description();
    }
    
    public static String label() {
        return "Promo: ";
    }

}
