package openwrestling.model.manager;

import openwrestling.model.factory.PersonFactory;
import openwrestling.model.gameObjects.Relationship;
import openwrestling.model.gameObjects.Worker;
import openwrestling.model.gameObjects.WorkerRelationship;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static openwrestling.model.constants.GameConstants.DEFAULT_RELATIONSHIP_LEVEL;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RelationshipManagerTest {

    private RelationshipManager relationshipManager;
    private Worker worker;
    private Worker worker2;

    public RelationshipManagerTest() {
    }

    @Before
    public void setUp() {
        relationshipManager = new RelationshipManager();
        worker = PersonFactory.randomWorker();
        worker2 = PersonFactory.randomWorker();
    }

    @Test
    public void getRelationship_returnsDefault() {
        WorkerRelationship relationship = relationshipManager.getWorkerRelationship(worker, worker2);
        assertEquals(relationship.getLevel(), DEFAULT_RELATIONSHIP_LEVEL);
    }

    @Test
    public void getRelationshipLevel_returnsDefault() {
        int level = relationshipManager.getWorkerRelationshipLevel(worker, worker2);
        assertEquals(level, DEFAULT_RELATIONSHIP_LEVEL);
    }

    @Test
    public void setRelationshipLevel_returnsSetValueFromBoth() {
        int value = 13;
        relationshipManager.setWorkerRelationshipLevel(worker, worker2, value);
        Relationship relationship1 = relationshipManager.getWorkerRelationship(worker, worker2);
        Relationship relationship2 = relationshipManager.getWorkerRelationship(worker2, worker);
        assertEquals(relationship1.getLevel(), value);
        assertEquals(relationship2.getLevel(), value);
        assertEquals(relationship2, relationship1);
    }

    @Test
    public void getThenSetRelationshipLevel() {
        Relationship relationship = relationshipManager.getWorkerRelationship(worker, worker2);
        int value = 23;
        relationshipManager.setWorkerRelationshipLevel(worker, worker2, value);
        Relationship relationship1 = relationshipManager.getWorkerRelationship(worker, worker2);
        Relationship relationship2 = relationshipManager.getWorkerRelationship(worker2, worker);
        assertEquals(relationship1.getLevel(), value);
        assertEquals(relationship2.getLevel(), value);
        assertEquals(relationship2, relationship1);
        assertEquals(relationship, relationship1);
        assertEquals(relationship2, relationship);
    }

    @Test
    public void addRelationshipValue() {
        int value = 23;
        relationshipManager.addWorkerRelationshipValue(worker, worker2, value);
        Relationship relationship = relationshipManager.getRelationship(worker, worker2);
        assertEquals(relationship.getLevel(), DEFAULT_RELATIONSHIP_LEVEL + value);
    }

    @Test
    public void subtractRelationshipValue() {
        int value = 23;
        relationshipManager.addWorkerRelationshipValue(worker, worker2, -value);
        Relationship relationship = relationshipManager.getRelationship(worker, worker2);
        assertEquals(relationship.getLevel(), DEFAULT_RELATIONSHIP_LEVEL - value);
    }

    @Test
    public void getRelationships() {
        List<Relationship> relationships = relationshipManager.getRelationships(worker);
        assertEquals(relationships.size(), 0);
        Relationship relationship1 = relationshipManager.getWorkerRelationship(worker, worker2);
        relationships = relationshipManager.getRelationships(worker);
        assertTrue(relationships.contains(relationship1));
    }

}
