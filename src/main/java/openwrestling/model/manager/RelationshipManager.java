package openwrestling.model.manager;

import openwrestling.database.Database;
import openwrestling.model.SegmentItem;
import openwrestling.model.gameObjects.Relationship;
import openwrestling.model.gameObjects.Worker;
import openwrestling.model.gameObjects.WorkerRelationship;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static openwrestling.model.constants.GameConstants.DEFAULT_RELATIONSHIP_LEVEL;

public class RelationshipManager {

    private final HashMap<SegmentItem, Map<SegmentItem, Relationship>> relationshipMap;

    public RelationshipManager() {
        relationshipMap = new HashMap<>();
    }

    public void setRelationshipLevel(SegmentItem segmentItem, SegmentItem segmentItem2, int value) {
        if (hasRelationship(segmentItem, segmentItem2)) {
            relationshipMap.get(segmentItem).get(segmentItem2).setLevel(value);
        } else {
            createRelationship(segmentItem, segmentItem2, value);
        }
    }

    public void setWorkerRelationshipLevel(Worker worker, Worker worker2, int value) {
        if (hasRelationship(worker, worker2)) {
            relationshipMap.get(worker).get(worker2).setLevel(value);
        } else {
            createWorkerRelationship(WorkerRelationship.builder().worker1(worker).worker2(worker2).level(value).build());
        }
    }

    public void addRelationshipValue(SegmentItem segmentItem, SegmentItem segmentItem2, int addValue) {
        if (!hasRelationship(segmentItem, segmentItem2)) {
            createRelationship(segmentItem, segmentItem2, DEFAULT_RELATIONSHIP_LEVEL + addValue);
        } else {
            setRelationshipLevel(segmentItem, segmentItem2, getRelationshipLevel(segmentItem, segmentItem2) + addValue);
        }
    }

    public void addWorkerRelationshipValue(Worker segmentItem, Worker segmentItem2, int addValue) {
        if (!hasRelationship(segmentItem, segmentItem2)) {
            createWorkerRelationship(WorkerRelationship.builder().worker1(segmentItem).worker2(segmentItem2).level(DEFAULT_RELATIONSHIP_LEVEL + addValue).build());
        } else {
            setWorkerRelationshipLevel(segmentItem, segmentItem2, getRelationshipLevel(segmentItem, segmentItem2) + addValue);
        }
    }

    public Relationship getRelationship(SegmentItem segmentItem, SegmentItem segmentItem2) {
        if (hasRelationship(segmentItem, segmentItem2)) {
            return relationshipMap.get(segmentItem).get(segmentItem2);
        }
        return createRelationship(segmentItem, segmentItem2, DEFAULT_RELATIONSHIP_LEVEL);
    }

    public WorkerRelationship getWorkerRelationship(Worker segmentItem, Worker segmentItem2) {
        if (hasRelationship(segmentItem, segmentItem2)) {
            return (WorkerRelationship)relationshipMap.get(segmentItem).get(segmentItem2);
        }
        return createWorkerRelationship(WorkerRelationship.builder().worker1(segmentItem).worker2(segmentItem2).level(DEFAULT_RELATIONSHIP_LEVEL).build());
    }

    public int getRelationshipLevel(SegmentItem segmentItem, SegmentItem segmentItem2) {
        return getRelationship(segmentItem, segmentItem2).getLevel();
    }

    public int getWorkerRelationshipLevel(Worker segmentItem, Worker segmentItem2) {
        return getWorkerRelationship(segmentItem, segmentItem2).getLevel();
    }

    public List<Relationship> getRelationships(SegmentItem segmentItem) {
        return relationshipMap.containsKey(segmentItem)
                ? relationshipMap.get(segmentItem).values().stream().collect(Collectors.toList())
                : Collections.emptyList();
    }

    private boolean hasRelationship(SegmentItem segmentItem, SegmentItem segmentItem2) {
        return relationshipMap.containsKey(segmentItem) && relationshipMap.get(segmentItem).containsKey(segmentItem2);
    }

    private Relationship createRelationship(SegmentItem segmentItem, SegmentItem segmentItem2, int value) {
//        Relationship relationship = new Relationship(segmentItem, segmentItem2, value);
//
//        if (!relationshipMap.containsKey(segmentItem)) {
//            relationshipMap.put(segmentItem, new HashMap<>());
//        }
//
//        if (!relationshipMap.containsKey(segmentItem2)) {
//            relationshipMap.put(segmentItem2, new HashMap<>());
//        }
//
//        relationshipMap.get(segmentItem).put(segmentItem2, relationship);
//        relationshipMap.get(segmentItem2).put(segmentItem, relationship);

        return null;
    }

    private WorkerRelationship createWorkerRelationship(WorkerRelationship workerRelationship) {

        if (!relationshipMap.containsKey(workerRelationship.getWorker1())) {
            relationshipMap.put(workerRelationship.getWorker1(), new HashMap<>());
        }

        if (!relationshipMap.containsKey(workerRelationship.getWorker2())) {
            relationshipMap.put(workerRelationship.getWorker2(), new HashMap<>());
        }

        relationshipMap.get(workerRelationship.getWorker1()).put(workerRelationship.getWorker2(), workerRelationship);
        relationshipMap.get(workerRelationship.getWorker2()).put(workerRelationship.getWorker1(), workerRelationship);

        return workerRelationship;
    }

    public List<WorkerRelationship> createWorkerRelationships(List<WorkerRelationship> workerRelationships) {
        List<WorkerRelationship> savedRelationships = Database.insertOrUpdateList(workerRelationships);
        savedRelationships.forEach(this::createWorkerRelationship);

        return savedRelationships;
    }
}
