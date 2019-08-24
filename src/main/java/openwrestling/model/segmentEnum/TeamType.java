package openwrestling.model.segmentEnum;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import openwrestling.model.SegmentItem;
import openwrestling.model.interfaces.Description;
import openwrestling.model.interfaces.iTeamType;
import openwrestling.model.modelView.StaffView;
import openwrestling.model.modelView.TagTeamView;
import openwrestling.model.modelView.TitleView;
import openwrestling.model.modelView.WorkerGroup;
import openwrestling.model.gameObjects.Worker;

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

        @Override
        public List<TeamType> getShared() {
            return Collections.emptyList();
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

        @Override
        public List<TeamType> getShared() {
            return Collections.emptyList();
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

        @Override
        public List<TeamType> getShared() {
            return Collections.emptyList();
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

        @Override
        public List<TeamType> getShared() {
            return Collections.emptyList();
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

        @Override
        public List<TeamType> getShared() {
            return Collections.emptyList();
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

        @Override
        public List<TeamType> getShared() {
            return Collections.emptyList();
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

        @Override
        public List<TeamType> getShared() {
            return Collections.emptyList();
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

        @Override
        public List<TeamType> getShared() {
            return Collections.emptyList();
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

        @Override
        public List<TeamType> getShared() {
            return Collections.emptyList();
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

        @Override
        public List<TeamType> getShared() {
            return Collections.emptyList();
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

        @Override
        public List<TeamType> getShared() {
            return Arrays.asList(ENTOURAGE, BROADCAST);
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

        @Override
        public List<TeamType> getShared() {
            return Collections.emptyList();
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

        @Override
        public List<TeamType> getShared() {
            return Collections.emptyList();
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

        @Override
        public List<TeamType> getShared() {
            return Collections.emptyList();
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

        @Override
        public List<TeamType> getShared() {
            return Collections.emptyList();
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

        @Override
        public List<TeamType> getShared() {
            return Collections.emptyList();
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

        @Override
        public List<TeamType> getShared() {
            return Collections.emptyList();
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

        @Override
        public List<TeamType> getShared() {
            return Collections.emptyList();
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

        @Override
        public List<TeamType> getShared() {
            return Collections.emptyList();
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

        @Override
        public List<TeamType> getShared() {
            return Collections.emptyList();
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

        @Override
        public List<TeamType> getShared() {
            return Collections.emptyList();
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

        @Override
        public List<TeamType> getShared() {
            return Arrays.asList(ENTOURAGE, INTERFERENCE);
        }
    },
    ENTOURAGE {
        @Override
        public String description() {
            return "with";
        }

        @Override
        public String result() {
            return "";
        }

        @Override
        public boolean droppable(SegmentItem segmentItem) {
            return workerDroppable(segmentItem);
        }

        @Override
        public List<TeamType> getShared() {
            return Arrays.asList(BROADCAST, INTERFERENCE);
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
        return segmentItem instanceof Worker || segmentItem instanceof TagTeamView || segmentItem instanceof WorkerGroup;
    }

}
