package wrestling.model.manager;

import java.util.List;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import wrestling.model.Relationship;
import wrestling.model.SegmentItem;
import static wrestling.model.constants.GameConstants.DEFAULT_RELATIONSHIP_LEVEL;
import wrestling.model.factory.PersonFactory;
import wrestling.model.segmentEnum.StaffType;

public class RelationshipManagerTest {

    private RelationshipManager relationshipManager;
    private SegmentItem segmentItem;
    private SegmentItem segmentItem2;
    private SegmentItem segmentItem3;

    public RelationshipManagerTest() {
    }

    @Before
    public void setUp() {
        relationshipManager = new RelationshipManager();
        segmentItem = PersonFactory.randomWorker();
        segmentItem2 = PersonFactory.randomWorker();
        segmentItem3 = PersonFactory.randomStaff(45, StaffType.REFEREE);
    }

    @Test
    public void getRelationship_returnsDefault() {
        Relationship relationship = relationshipManager.getRelationship(segmentItem, segmentItem2);
        assertEquals(relationship.getLevel(), DEFAULT_RELATIONSHIP_LEVEL);
    }

    @Test
    public void getRelationshipLevel_returnsDefault() {
        int level = relationshipManager.getRelationshipLevel(segmentItem, segmentItem2);
        assertEquals(level, DEFAULT_RELATIONSHIP_LEVEL);
    }

    @Test
    public void setRelationshipLevel_returnsSetValueFromBoth() {
        int value = 13;
        relationshipManager.setRelationshipLevel(segmentItem, segmentItem2, value);
        Relationship relationship1 = relationshipManager.getRelationship(segmentItem, segmentItem2);
        Relationship relationship2 = relationshipManager.getRelationship(segmentItem2, segmentItem);
        assertEquals(relationship1.getLevel(), value);
        assertEquals(relationship2.getLevel(), value);
        assertEquals(relationship2, relationship1);
    }

    @Test
    public void getThenSetRelationshipLevel() {
        Relationship relationship = relationshipManager.getRelationship(segmentItem, segmentItem2);
        int value = 23;
        relationshipManager.setRelationshipLevel(segmentItem, segmentItem2, value);
        Relationship relationship1 = relationshipManager.getRelationship(segmentItem, segmentItem2);
        Relationship relationship2 = relationshipManager.getRelationship(segmentItem2, segmentItem);
        assertEquals(relationship1.getLevel(), value);
        assertEquals(relationship2.getLevel(), value);
        assertEquals(relationship2, relationship1);
        assertEquals(relationship, relationship1);
        assertEquals(relationship2, relationship);
    }

    @Test
    public void addRelationshipValue() {
        int value = 23;
        relationshipManager.addRelationshipValue(segmentItem, segmentItem2, value);
        Relationship relationship = relationshipManager.getRelationship(segmentItem, segmentItem2);
        assertEquals(relationship.getLevel(), DEFAULT_RELATIONSHIP_LEVEL + value);
    }

    @Test
    public void subtractRelationshipValue() {
        int value = 23;
        relationshipManager.addRelationshipValue(segmentItem, segmentItem2, -value);
        Relationship relationship = relationshipManager.getRelationship(segmentItem, segmentItem2);
        assertEquals(relationship.getLevel(), DEFAULT_RELATIONSHIP_LEVEL - value);
    }

    @Test
    public void getRelationships() {
        List<Relationship> relationships = relationshipManager.getRelationships(segmentItem);
        assertEquals(relationships.size(), 0);
        Relationship relationship1 = relationshipManager.getRelationship(segmentItem, segmentItem2);
        Relationship relationship2 = relationshipManager.getRelationship(segmentItem, segmentItem3);
        relationships = relationshipManager.getRelationships(segmentItem);
        assertTrue(relationships.contains(relationship1));
        assertTrue(relationships.contains(relationship2));
        assertEquals(relationships.size(), 2);
    }

}
