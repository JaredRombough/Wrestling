package wrestling.model;

public class Relationship {

    private final SegmentItem segmentItem;
    private final SegmentItem segmentItem2;
    private int level;

    public Relationship(SegmentItem worker1, SegmentItem worker2, int level) {
        this.segmentItem = worker1;
        this.segmentItem2 = worker2;
        this.level = level;
    }

    public void modifyValue(int diff) {
        setLevel(level + diff);
    }

    /**
     * @return the level
     */
    public int getLevel() {
        return level;
    }

    /**
     * @param level the level to set
     */
    public void setLevel(int level) {
        this.level = level;
    }

}
