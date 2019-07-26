package wrestling.model.manager;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import wrestling.model.Relationship;
import wrestling.model.SegmentItem;

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

    public void addRelationshipValue(SegmentItem segmentItem, SegmentItem segmentItem2, int addValue) {
        if (!hasRelationship(segmentItem, segmentItem2)) {
            createRelationship(segmentItem, segmentItem2, 100 + addValue);
        } else {
            setRelationshipLevel(segmentItem, segmentItem2, getRelationshipLevel(segmentItem, segmentItem2) + addValue);
        }
    }

    public Relationship getRelationship(SegmentItem segmentItem, SegmentItem segmentItem2) {
        if (hasRelationship(segmentItem, segmentItem2)) {
            return relationshipMap.get(segmentItem).get(segmentItem2);
        }
        return createRelationship(segmentItem, segmentItem2, 100);
    }

    public int getRelationshipLevel(SegmentItem segmentItem, SegmentItem segmentItem2) {
        return getRelationship(segmentItem, segmentItem2).getLevel();
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
        Relationship relationship = new Relationship(segmentItem, segmentItem2, value);

        if (!relationshipMap.containsKey(segmentItem)) {
            relationshipMap.put(segmentItem, new HashMap<>());
        }

        if (!relationshipMap.containsKey(segmentItem2)) {
            relationshipMap.put(segmentItem2, new HashMap<>());
        }

        relationshipMap.get(segmentItem).put(segmentItem2, relationship);
        relationshipMap.get(segmentItem2).put(segmentItem, relationship);

        return relationship;
    }
}
