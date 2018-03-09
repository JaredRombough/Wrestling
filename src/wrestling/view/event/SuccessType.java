package wrestling.view.event;

public enum SuccessType implements Description {

    WIN {
        @Override
        public String description() {
            return "Win";
        }
        
        @Override
        public String result() {
            return "and getting the better of the exchange.";
        }

    },
    LOSE {
        @Override
        public String description() {
            return "Lose";
        }
        
        @Override
        public String result() {
            return "but were run off to the back.";
        }

    },
    DRAW {
        @Override
        public String description() {
            return "Draw";
        }
        
        @Override
        public String result() {
            return "and proceeded to brawl.";
        }

    };

    @Override
    public String toString() {
        return description();
    }

}
