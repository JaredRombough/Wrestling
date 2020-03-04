package openwrestling.manager;

import lombok.Getter;
import openwrestling.database.Database;
import openwrestling.model.gameObjects.MoraleRelationship;
import openwrestling.model.gameObjects.Promotion;
import openwrestling.model.gameObjects.Relationship;
import openwrestling.model.gameObjects.Worker;
import openwrestling.model.gameObjects.WorkerRelationship;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static openwrestling.model.constants.GameConstants.DEFAULT_RELATIONSHIP_LEVEL;

@Getter
public class RelationshipManager extends GameObjectManager {

    public RelationshipManager(Database database) {
        super(database);
    }


    private List<WorkerRelationship> workerRelationships = new ArrayList<>();
    //private List<MoraleRelationship> moraleRelationships = new ArrayList<>();
    private Map<Long, MoraleRelationship> moraleRelationshipMap = new HashMap<>();

    @Override
    public void selectData() {
        workerRelationships = getDatabase().selectAll(WorkerRelationship.class);
        //moraleRelationships = getDatabase().selectAll(MoraleRelationship.class);
        List<MoraleRelationship> moraleRelationships = getDatabase().selectAll(MoraleRelationship.class);
        moraleRelationships.forEach(relationship -> {
            moraleRelationshipMap.put(relationship.getRelationshipID(), relationship);
        });
    }

    public List<MoraleRelationship> getMoraleRelationships() {
        return new ArrayList<>(moraleRelationshipMap.values());
    }

    public List<Relationship> getRelationships(Worker worker) {
        List<Relationship> relationships = new ArrayList<>();
        relationships.addAll(
                this.workerRelationships.stream()
                        .filter(workerRelationship -> workerRelationship.getWorker1().getWorkerID() == worker.getWorkerID()
                                || workerRelationship.getWorker2().getWorkerID() == worker.getWorkerID())
                        .collect(Collectors.toList())
        );
        relationships.addAll(
                getMoraleRelationships().stream()
                        .filter(workerRelationship -> workerRelationship.getWorker().getWorkerID() == worker.getWorkerID())
                        .collect(Collectors.toList())
        );
        return relationships;
    }

    public List<WorkerRelationship> getWorkerRelationships(Worker worker) {
        List<WorkerRelationship> relationships = new ArrayList<>();
        relationships.addAll(
                this.workerRelationships.stream()
                        .filter(workerRelationship -> workerRelationship.getWorker1().getWorkerID() == worker.getWorkerID()
                                || workerRelationship.getWorker2().getWorkerID() == worker.getWorkerID())
                        .collect(Collectors.toList())
        );
        return relationships;
    }

    public MoraleRelationship getOrCreateMoraleRelationship(Worker worker, Promotion promotion) {
        MoraleRelationship moraleRelationship = getMoraleRelationships().stream()
                .filter(moraleRelationship1 -> moraleRelationship1.getWorker().getWorkerID() == worker.getWorkerID() &&
                        moraleRelationship1.getPromotion().getPromotionID() == promotion.getPromotionID())
                .findFirst()
                .orElse(null);
        if (moraleRelationship == null) {
            MoraleRelationship saved = getDatabase().insertGameObject(MoraleRelationship.builder()
                    .level(DEFAULT_RELATIONSHIP_LEVEL)
                    .worker(worker)
                    .promotion(promotion)
                    .build());
            //moraleRelationships = getDatabase().selectAll(MoraleRelationship.class);
            moraleRelationshipMap.put(saved.getRelationshipID(), saved);
            return saved;
        }
        return moraleRelationship;
    }

    public MoraleRelationship getMoraleRelationship(Worker worker, Promotion promotion) {
        return getMoraleRelationships().stream()
                .filter(moraleRelationship1 -> moraleRelationship1.getWorker().getWorkerID() == worker.getWorkerID() &&
                        moraleRelationship1.getPromotion().getPromotionID() == promotion.getPromotionID())
                .findFirst()
                .orElse(MoraleRelationship.builder()
                        .level(DEFAULT_RELATIONSHIP_LEVEL)
                        .worker(worker)
                        .promotion(promotion)
                        .build());
    }

    public void addRelationshipValue(Worker worker, Promotion promotion, int addValue) {
        MoraleRelationship moraleRelationship = getMoraleRelationships().stream()
                .filter(moraleRelationship1 -> moraleRelationship1.getWorker().getWorkerID() == worker.getWorkerID() &&
                        moraleRelationship1.getPromotion().getPromotionID() == promotion.getPromotionID())
                .findFirst()
                .orElse(MoraleRelationship.builder()
                        .level(addValue)
                        .worker(worker)
                        .promotion(promotion)
                        .build()
                );
        getDatabase().insertGameObject(moraleRelationship);
        //moraleRelationships = getDatabase().selectAll(MoraleRelationship.class);
        moraleRelationshipMap.put(moraleRelationship.getRelationshipID(), moraleRelationship);
    }


    public List<MoraleRelationship> createOrUpdateMoraleRelationships(List<MoraleRelationship> workerRelationships) {
        List<MoraleRelationship> savedRelationships = getDatabase().insertList(workerRelationships);
        //this.moraleRelationships = getDatabase().selectAll(MoraleRelationship.class);
        savedRelationships.forEach(moraleRelationship -> {
            moraleRelationshipMap.put(moraleRelationship.getRelationshipID(), moraleRelationship);
        });
        return savedRelationships;
    }

    public List<WorkerRelationship> createWorkerRelationships(List<WorkerRelationship> workerRelationships) {
        List<WorkerRelationship> savedRelationships = getDatabase().insertList(workerRelationships);
        this.workerRelationships = getDatabase().selectAll(WorkerRelationship.class);
        return savedRelationships;
    }
}
