package wrestling.model;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static wrestling.model.constants.GameConstants.DEFAULT_RELATIONSHIP_LEVEL;
import static wrestling.model.constants.GameConstants.MAX_RELATIONSHIP_LEVEL;
import static wrestling.model.constants.GameConstants.MIN_RELATIONSHIP_LEVEL;
import wrestling.model.factory.PersonFactory;

public class RelationshipTest {

    private SegmentItem segmentItem;
    private SegmentItem segmentItem2;

    public RelationshipTest() {
    }

    @Before
    public void setUp() {
        segmentItem = PersonFactory.randomWorker();
        segmentItem2 = PersonFactory.randomWorker();
    }

    @Test
    public void setLevel_bounds() {
        Relationship relationship = new Relationship(segmentItem, segmentItem2, DEFAULT_RELATIONSHIP_LEVEL);
        relationship.setLevel(MIN_RELATIONSHIP_LEVEL - 1);
        assertEquals(relationship.getLevel(), MIN_RELATIONSHIP_LEVEL);
        relationship.setLevel(MAX_RELATIONSHIP_LEVEL + 1);
        assertEquals(relationship.getLevel(), MAX_RELATIONSHIP_LEVEL);
    }

    @Test
    public void modifyValue_bounds() {
        Relationship relationship = new Relationship(segmentItem, segmentItem2, MAX_RELATIONSHIP_LEVEL);
        relationship.modifyValue(1);
        assertEquals(relationship.getLevel(), MAX_RELATIONSHIP_LEVEL);
        relationship.modifyValue(-1);
        assertEquals(relationship.getLevel(), MAX_RELATIONSHIP_LEVEL - 1);

        relationship.setLevel(MIN_RELATIONSHIP_LEVEL);

        relationship.modifyValue(-1);
        assertEquals(relationship.getLevel(), MIN_RELATIONSHIP_LEVEL);
        relationship.modifyValue(1);
        assertEquals(relationship.getLevel(), MIN_RELATIONSHIP_LEVEL + 1);
    }

}
