package openwrestling.model.manager;

import lombok.Getter;
import openwrestling.database.Database;
import openwrestling.model.gameObjects.MoraleRelationship;
import openwrestling.model.gameObjects.Promotion;
import openwrestling.model.gameObjects.Relationship;
import openwrestling.model.gameObjects.Worker;
import openwrestling.model.gameObjects.WorkerRelationship;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static openwrestling.model.constants.GameConstants.DEFAULT_RELATIONSHIP_LEVEL;

@Getter
public class RelationshipManager {

    private List<WorkerRelationship> workerRelationships = new ArrayList<>();
    private List<MoraleRelationship> moraleRelationships = new ArrayList<>();

    public List<Relationship> getRelationships(Worker worker) {
        List<Relationship> relationships = new ArrayList<>();
        relationships.addAll(
                this.workerRelationships.stream()
                        .filter(workerRelationship -> workerRelationship.getWorker1().getWorkerID() == worker.getWorkerID()
                                || workerRelationship.getWorker2().getWorkerID() == worker.getWorkerID())
                        .collect(Collectors.toList())
        );
        relationships.addAll(
                this.moraleRelationships.stream()
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
        MoraleRelationship moraleRelationship = moraleRelationships.stream()
                .filter(moraleRelationship1 -> moraleRelationship1.getWorker().getWorkerID() == worker.getWorkerID() &&
                        moraleRelationship1.getPromotion().getPromotionID() == promotion.getPromotionID())
                .findFirst()
                .orElse(null);
        if (moraleRelationship == null) {
            MoraleRelationship saved = Database.insertGameObject(MoraleRelationship.builder()
                    .level(DEFAULT_RELATIONSHIP_LEVEL)
                    .worker(worker)
                    .promotion(promotion)
                    .build());
            moraleRelationships = Database.selectAll(MoraleRelationship.class);
            return saved;
        }
        return moraleRelationship;
    }

    public MoraleRelationship getMoraleRelationship(Worker worker, Promotion promotion) {
        return moraleRelationships.stream()
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
        MoraleRelationship moraleRelationship = moraleRelationships.stream()
                .filter(moraleRelationship1 -> moraleRelationship1.getWorker().getWorkerID() == worker.getWorkerID() &&
                        moraleRelationship1.getPromotion().getPromotionID() == promotion.getPromotionID())
                .findFirst()
                .orElse(MoraleRelationship.builder()
                        .level(addValue)
                        .worker(worker)
                        .promotion(promotion)
                        .build()
                );
        Database.insertGameObject(moraleRelationship);
        moraleRelationships = Database.selectAll(MoraleRelationship.class);
    }


    public List<MoraleRelationship> createOrUpdateMoraleRelationships(List<MoraleRelationship> workerRelationships) {
        List<MoraleRelationship> savedRelationships = Database.insertOrUpdateList(workerRelationships);
        this.moraleRelationships = Database.selectAll(MoraleRelationship.class);
        return savedRelationships;
    }

    public List<WorkerRelationship> createWorkerRelationships(List<WorkerRelationship> workerRelationships) {
        List<WorkerRelationship> savedRelationships = Database.insertOrUpdateList(workerRelationships);
        this.workerRelationships = Database.selectAll(WorkerRelationship.class);
        return savedRelationships;
    }
}
