package wrestling.model.segmentEnum;

import wrestling.model.SegmentItem;
import wrestling.model.interfaces.Description;
import wrestling.model.interfaces.iTeamType;
import wrestling.model.modelView.StableView;
import wrestling.model.modelView.StaffView;
import wrestling.model.modelView.TagTeamView;
import wrestling.model.modelView.TitleView;
import wrestling.model.modelView.WorkerView;

public enum TeamType implements Description, iTeamType {
    CHALLENGER {
        @Override
        public String description() {
            return "Challenger";
        }

        @Override
        public String result() {
            return "";
        }

        @Override
        public boolean droppable(SegmentItem segmentItem) {
            return workerDroppable(segmentItem);
        }

    },
    CHALLENGED {
        @Override
        public String description() {
            return "Challenged";
        }

        @Override
        public String result() {
            return "";
        }

        @Override
        public boolean droppable(SegmentItem segmentItem) {
            return workerDroppable(segmentItem);
        }
    },
    ATTACKER {
        @Override
        public String description() {
            return "Attacker";
        }

        @Override
        public String result() {
            return "";
        }

        @Override
        public boolean droppable(SegmentItem segmentItem) {
            return workerDroppable(segmentItem);
        }

    },
    VICTIM {
        @Override
        public String description() {
            return "Victim";
        }

        @Override
        public String result() {
            return "";
        }

        @Override
        public boolean droppable(SegmentItem segmentItem) {
            return workerDroppable(segmentItem);
        }
    },
    PROMO {
        @Override
        public String description() {
            return "Promo";
        }

        @Override
        public String result() {
            return "";
        }

        @Override
        public boolean droppable(SegmentItem segmentItem) {
            return workerDroppable(segmentItem);
        }
    },
    BRAWLER_PRIME {
        @Override
        public String description() {
            return "Brawler";
        }

        @Override
        public String result() {
            return "";
        }

        @Override
        public boolean droppable(SegmentItem segmentItem) {
            return workerDroppable(segmentItem);
        }
    },
    BRAWLER {
        @Override
        public String description() {
            return "Brawler";
        }

        @Override
        public String result() {
            return "";
        }

        @Override
        public boolean droppable(SegmentItem segmentItem) {
            return workerDroppable(segmentItem);
        }
    },
    PROMO_TARGET {
        @Override
        public String description() {
            return "Promo Target";
        }

        @Override
        public String result() {
            return "";
        }

        @Override
        public boolean droppable(SegmentItem segmentItem) {
            return workerDroppable(segmentItem);
        }
    },
    OFFERER {
        @Override
        public String description() {
            return "Offerer";
        }

        @Override
        public String result() {
            return "";
        }

        @Override
        public boolean droppable(SegmentItem segmentItem) {
            return workerDroppable(segmentItem);
        }
    },
    OFFEREE {
        @Override
        public String description() {
            return "Offeree";
        }

        @Override
        public String result() {
            return "";
        }

        @Override
        public boolean droppable(SegmentItem segmentItem) {
            return workerDroppable(segmentItem);
        }
    },
    INTERFERENCE {
        @Override
        public String description() {
            return "Interference";
        }

        @Override
        public String result() {
            return "";
        }

        @Override
        public boolean droppable(SegmentItem segmentItem) {
            return workerDroppable(segmentItem);
        }
    },
    INTERVIEWER {
        @Override
        public String description() {
            return "Interviewer";
        }

        @Override
        public String result() {
            return "";
        }

        @Override
        public boolean droppable(SegmentItem segmentItem) {
            return workerDroppable(segmentItem);
        }
    },
    ANNOUNCER {
        @Override
        public String description() {
            return "Announcer";
        }

        @Override
        public String result() {
            return "";
        }

        @Override
        public boolean droppable(SegmentItem segmentItem) {
            return workerDroppable(segmentItem);
        }
    },
    AUDIENCE {
        @Override
        public String description() {
            return "Audience";
        }

        @Override
        public String result() {
            return "";
        }

        @Override
        public boolean droppable(SegmentItem segmentItem) {
            return workerDroppable(segmentItem);
        }
    },
    DEFAULT {
        @Override
        public String description() {
            return "Default";
        }

        @Override
        public String result() {
            return "";
        }

        @Override
        public boolean droppable(SegmentItem segmentItem) {
            return workerDroppable(segmentItem);
        }
    },
    WINNER {
        @Override
        public String description() {
            return "Winner";
        }

        @Override
        public String result() {
            return "";
        }

        @Override
        public boolean droppable(SegmentItem segmentItem) {
            return workerDroppable(segmentItem);
        }
    },
    LOSER {
        @Override
        public String description() {
            return "Loser";
        }

        @Override
        public String result() {
            return "";
        }

        @Override
        public boolean droppable(SegmentItem segmentItem) {
            return workerDroppable(segmentItem);
        }
    },
    DRAW {
        @Override
        public String description() {
            return "Draw";
        }

        @Override
        public String result() {
            return "";
        }

        @Override
        public boolean droppable(SegmentItem segmentItem) {
            return workerDroppable(segmentItem);
        }
    },
    EVERYONE {
        @Override
        public String description() {
            return "Everyone";
        }

        @Override
        public String result() {
            return "";
        }

        @Override
        public boolean droppable(SegmentItem segmentItem) {
            return false;
        }
    },
    TITLES {
        @Override
        public String description() {
            return "Titles";
        }

        @Override
        public String result() {
            return "";
        }

        @Override
        public boolean droppable(SegmentItem segmentItem) {
            return segmentItem instanceof TitleView;
        }
    },
    REF {
        @Override
        public String description() {
            return "Referee";
        }

        @Override
        public String result() {
            return "";
        }

        @Override
        public boolean droppable(SegmentItem segmentItem) {
            return segmentItem instanceof StaffView && ((StaffView) segmentItem).getStaffType().equals(StaffType.REFEREE);
        }
    },
    BROADCAST {
        @Override
        public String description() {
            return "Broadcast Team";
        }

        @Override
        public String result() {
            return "";
        }

        @Override
        public boolean droppable(SegmentItem segmentItem) {
            return workerDroppable(segmentItem) || (segmentItem instanceof StaffView && ((StaffView) segmentItem).getStaffType().equals(StaffType.BROADCAST));
        }
    };

    @Override
    public String toString() {
        return description();
    }

    public boolean isMatch() {
        return this.equals(WINNER) || this.equals(LOSER) || this.equals(DRAW);
    }

    private static boolean workerDroppable(SegmentItem segmentItem) {
        return segmentItem instanceof WorkerView || segmentItem instanceof TagTeamView || segmentItem instanceof StableView;
    }

}
