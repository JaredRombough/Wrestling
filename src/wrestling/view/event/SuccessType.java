package wrestling.view.event;

public enum SuccessType implements Description {

    WIN {
        @Override
        public String description() {
            return "Win";
        }

    },
    LOSE {
        @Override
        public String description() {
            return "Lose";
        }

    },
    DRAW {
        @Override
        public String description() {
            return "Draw";
        }

    }

}
