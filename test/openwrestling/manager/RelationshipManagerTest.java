package openwrestling.manager;

import openwrestling.TestUtils;
import openwrestling.database.Database;
import openwrestling.manager.BankAccountManager;
import openwrestling.manager.ContractManager;
import openwrestling.manager.GameSettingManager;
import openwrestling.manager.PromotionManager;
import openwrestling.manager.RelationshipManager;
import openwrestling.manager.WorkerManager;
import openwrestling.model.factory.PersonFactory;
import openwrestling.model.gameObjects.MoraleRelationship;
import openwrestling.model.gameObjects.Promotion;
import openwrestling.model.gameObjects.Relationship;
import openwrestling.model.gameObjects.Worker;
import openwrestling.model.gameObjects.WorkerRelationship;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static openwrestling.TestUtils.TEST_DB_PATH;
import static openwrestling.model.constants.GameConstants.DEFAULT_RELATIONSHIP_LEVEL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class RelationshipManagerTest {

    private RelationshipManager relationshipManager;
    private Worker worker;
    private Worker worker2;
    private Promotion promotion;
    private Database database;

    public RelationshipManagerTest() {
    }

    @Before
    public void setUp() {
        database = new Database(TEST_DB_PATH);

        relationshipManager = new RelationshipManager(database);

        PromotionManager promotionManager = new PromotionManager(database, mock(BankAccountManager.class), mock(GameSettingManager.class));
        WorkerManager workerManager = new WorkerManager(database, mock(ContractManager.class));
        worker = workerManager.createWorker(PersonFactory.randomWorker());
        worker2 = workerManager.createWorker(PersonFactory.randomWorker());
        promotion = promotionManager.createPromotions(List.of(TestUtils.randomPromotion())).get(0);
    }

    @Test
    public void getMoraleRelationship_returnsDefault() {
        MoraleRelationship relationship = relationshipManager.getOrCreateMoraleRelationship(worker, promotion);
        assertThat(relationship.getLevel()).isEqualTo(DEFAULT_RELATIONSHIP_LEVEL);
    }


    @Test
    public void workerRelationship_create() {
        int level = 13;
        WorkerRelationship workerRelationship = WorkerRelationship.builder()
                .worker2(worker2)
                .worker1(worker)
                .level(level)
                .build();
        relationshipManager.createWorkerRelationships(List.of(workerRelationship));

        List<Relationship> relationships = relationshipManager.getRelationships(worker);
        assertThat(relationships).hasOnlyOneElementSatisfying(relationship -> {
            assertThat(relationship).isInstanceOf(WorkerRelationship.class);
            assertThat(relationship.getOtherSegmentItem(worker).getShortName()).isEqualTo(worker2.getShortName());
            assertThat(relationship.getLevel()).isEqualTo(level);
        });

        List<Relationship> relationships2 = relationshipManager.getRelationships(worker2);
        assertThat(relationships2).hasOnlyOneElementSatisfying(relationship -> {
            assertThat(relationship).isInstanceOf(WorkerRelationship.class);
            assertThat(relationship.getOtherSegmentItem(worker2).getShortName()).isEqualTo(worker.getShortName());
            assertThat(relationship.getLevel()).isEqualTo(level);
        });

        List<WorkerRelationship> workerRelationships = relationshipManager.getWorkerRelationships(worker);
        assertThat(workerRelationships).hasOnlyOneElementSatisfying(relationship -> {
            assertThat(relationship).isInstanceOf(WorkerRelationship.class);
            assertThat(relationship.getOtherSegmentItem(worker).getShortName()).isEqualTo(worker2.getShortName());
            assertThat(relationship.getLevel()).isEqualTo(level);
        });

        List<WorkerRelationship> worker2Relationships = relationshipManager.getWorkerRelationships(worker2);
        assertThat(worker2Relationships).hasOnlyOneElementSatisfying(relationship -> {
            assertThat(relationship).isInstanceOf(WorkerRelationship.class);
            assertThat(relationship.getOtherSegmentItem(worker2).getShortName()).isEqualTo(worker.getShortName());
            assertThat(relationship.getLevel()).isEqualTo(level);
        });

    }

    @Test
    public void workerRelationship_update() {
        int level = 13;
        int level2 = 23;
        WorkerRelationship workerRelationship = WorkerRelationship.builder()
                .worker2(worker2)
                .worker1(worker)
                .level(level)
                .build();
        relationshipManager.createWorkerRelationships(List.of(workerRelationship));

        List<WorkerRelationship> workerRelationships = relationshipManager.getWorkerRelationships(worker);
        assertThat(workerRelationships).hasSize(1);
        workerRelationships.get(0).setLevel(level2);
        relationshipManager.createWorkerRelationships(workerRelationships);

        List<WorkerRelationship> workerRelationshipsUpdated = relationshipManager.getWorkerRelationships(worker);
        assertThat(workerRelationshipsUpdated).hasSize(1);
        assertThat(workerRelationships.get(0).getLevel()).isEqualTo(level2);
    }

}
