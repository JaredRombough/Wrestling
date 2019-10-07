package openwrestling.file;

import openwrestling.model.gameObjects.Contract;
import openwrestling.model.gameObjects.EventTemplate;
import openwrestling.model.gameObjects.Promotion;
import openwrestling.model.gameObjects.RosterSplit;
import openwrestling.model.gameObjects.Stable;
import openwrestling.model.gameObjects.TagTeam;
import openwrestling.model.gameObjects.Title;
import openwrestling.model.gameObjects.Worker;
import openwrestling.model.gameObjects.WorkerRelationship;
import openwrestling.model.segmentEnum.ActiveType;
import openwrestling.model.segmentEnum.Gender;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class ImportTest {

    private static final File TEST_DATA_FOLDER = new File(".\\test_data");
    private Import testImport;

    @Before
    public void setUp() {
        testImport = new Import();
    }

    @Test
    public void promotionsDat() {
        List<Promotion> promotions = testImport.promotionsDat(TEST_DATA_FOLDER, "promos");
        assertThat(promotions).isNotNull().hasSize(18);
        assertThat(promotions.get(0).getName().substring(0, 5)).isEqualTo("World");
        assertThat(promotions.get(0).getName().toCharArray()).hasSize(26);
        assertThat(promotions.get(0).getLevel()).isEqualTo(5);
        assertThat(promotions.get(8).getLevel()).isEqualTo(1);
        assertThat(promotions.get(5).getLevel()).isEqualTo(3);
        assertThat(promotions.get(16).getLevel()).isEqualTo(2);
        for (int i = 0; i < promotions.size(); i++) {
            Promotion promotion = promotions.get(i);
            assertThat(promotion.getImportKey())
                    .isEqualTo(testImport.getPromotionKeys().get(i));
            assertThat(promotion.getName()).isNotNull();
            assertThat(promotion.getLevel()).isNotNull();
            assertThat(promotion.getShortName()).isNotNull();
            assertThat(promotion.getPopularity()).isEqualTo(50);
        }
    }

    @Test
    public void rosterSplits() {
        List<Promotion> promotions = testImport.promotionsDat(TEST_DATA_FOLDER, "promos_with_splits");
        List<RosterSplit> rosterSplits = testImport.rosterSplits(TEST_DATA_FOLDER,
                "promos_with_splits",
                promotions);
        assertThat(rosterSplits).isNotNull().hasSize(8);
        rosterSplits.forEach(rosterSplit -> {
            assertThat(rosterSplit.getOwner()).isNotNull();
            assertThat(rosterSplit.getName()).isNotNull().isNotEqualToIgnoringCase("None");
        });
    }

    @Test
    public void setManagers() {
        List<Worker> workers = testImport.workersDat(TEST_DATA_FOLDER);
        List<Worker> updatedWorkers = testImport.setManagers(TEST_DATA_FOLDER, workers);
        assertThat(updatedWorkers).hasSize(41);
    }

    @Test
    public void otherPromotions() {
        List<Promotion> promotions = testImport.otherPromotions(TEST_DATA_FOLDER);
        assertThat(promotions).hasSize(11);
    }

    @Test
    public void otherPromotionContracts() {
        List<Promotion> promotions = testImport.otherPromotions(TEST_DATA_FOLDER);
        List<Worker> workers = testImport.workersDat(TEST_DATA_FOLDER);
        List<Contract> contracts = testImport.otherPromotionContracts(TEST_DATA_FOLDER, promotions, workers, LocalDate.now());
        assertThat(contracts).hasSize(245);
    }

    @Test
    public void processOther() {
        List<Worker> workers = testImport.workersDat(TEST_DATA_FOLDER);
        List<Promotion> promotions = testImport.otherPromotions(TEST_DATA_FOLDER);
        List<Contract> contracts = testImport.otherPromotionContracts(TEST_DATA_FOLDER, promotions, workers, LocalDate.now());
        List<Promotion> updatedPromotions = testImport.updateOther(promotions, contracts);
        assertThat(updatedPromotions).hasSize(11);
    }

    @Test
    public void workersDat() {
        List<Worker> workers = testImport.workersDat(TEST_DATA_FOLDER);
        assertThat(workers).isNotNull().hasSize(1801);
        assertThat(workers.get(0).getName().substring(0, 3)).isEqualTo("Chr");
        assertThat(workers.get(0).getName().toCharArray()).hasSize(13);
        assertThat(workers.get(0).getCharisma()).isEqualTo(98);
        assertThat(workers.get(0).getStriking()).isEqualTo(72);
        assertThat(workers.get(0).getPopularity()).isEqualTo(76);
        assertThat(workers.get(0).getFlying()).isEqualTo(82);
        assertThat(workers.get(0).getWrestling()).isEqualTo(84);
        assertThat(workers.get(0).getBehaviour()).isEqualTo(84);
        assertThat(workers.get(0).getAge()).isEqualTo(27);
        assertThat(workers.get(0).getGender()).isEqualTo(Gender.MALE);
        assertThat(workers.get(6).getGender()).isEqualTo(Gender.FEMALE);
        workers.forEach(worker -> {
            assertThat(worker.getCharisma()).isNotNull().isNotNegative();
            assertThat(worker.getStriking()).isNotNull().isNotNegative();
            assertThat(worker.getPopularity()).isNotNull().isNotNegative();
            assertThat(worker.getFlying()).isNotNull().isNotNegative();
            assertThat(worker.getWrestling()).isNotNull().isNotNegative();
            assertThat(worker.getBehaviour()).isNotNull().isNotNegative();
            assertThat(worker.getAge()).isNotNull().isNotNegative();
            assertThat(worker.getImportKey()).isNotNull().isNotNegative();
            assertThat(worker.getName()).isNotNull();
            assertThat(worker.getShortName()).isNotNull();
            assertThat(worker.getImageString()).isNotNull();
            assertThat(worker.getGender()).isNotNull();
        });
    }

    @Test
    public void contracts() {
        List<Promotion> promotions = testImport.promotionsDat(TEST_DATA_FOLDER, "promos");
        List<Worker> workers = testImport.workersDat(TEST_DATA_FOLDER);
        List<Contract> contracts = testImport.contracts(TEST_DATA_FOLDER, workers, promotions, LocalDate.now());

        List<Contract> found = contracts.stream().filter(contract -> contract.getWorker().getImportKey() == 6144).collect(Collectors.toList());
        assertThat(found).hasSize(2);
        assertThat(found).hasSize(2);
        assertThat(found.stream().map(Contract::getPromotion).collect(Collectors.toList()))
                .extracting(Promotion::getImportKey)
                .containsOnly(1, 12);
        assertThat(found).extracting(Contract::isExclusive)
                .containsOnly(Boolean.FALSE);

        assertThat(contracts).extracting(Contract::isExclusive)
                .containsOnly(Boolean.TRUE, Boolean.FALSE);

        contracts.forEach(contract -> {
            assertThat(contract.getStartDate()).isEqualTo(LocalDate.now());
            assertThat(contract.getPromotion()).isNotNull();
            assertThat(contract.getWorker()).isNotNull();
            assertThat(contract.getEndDate()).isNotNull();
            assertThat(contract.isExclusive()).isNotNull();
            assertThat(contract.isActive()).isTrue();
        });
    }

    @Test
    public void teamsDat() {
        List<Worker> workers = testImport.workersDat(TEST_DATA_FOLDER);
        List<TagTeam> tagTeams = testImport.teamsDat(TEST_DATA_FOLDER, workers);
        assertThat(tagTeams).hasSize(515);

        assertThat(tagTeams.get(0).getWorkers()).extracting(Worker::getImportKey).containsOnly(6144, 6400);
        assertThat(tagTeams.get(0).getName()).hasSize(14);
        assertThat(tagTeams.get(0).getExperience()).isEqualTo(55);
        assertThat(tagTeams.get(0).getActiveType()).isEqualTo(ActiveType.ACTIVE);
        assertThat(tagTeams.get(78).getActiveType()).isEqualTo(ActiveType.INACTIVE);

        tagTeams.forEach(tagTeam -> {
            assertThat(tagTeam.getActiveType()).isNotNull();
            assertThat(tagTeam.getExperience()).isNotNull();
            assertThat(tagTeam.getName()).isNotNull();
            assertThat(tagTeam.getWorkers()).hasSize(2);
            assertThat(tagTeam.getExperience()).isNotNull();
        });
    }

    @Test
    public void stablesDat() {
        List<Worker> workers = testImport.workersDat(TEST_DATA_FOLDER);
        List<Promotion> promotions = testImport.promotionsDat(TEST_DATA_FOLDER, "promos");
        List<Stable> stables = testImport.stablesDat(TEST_DATA_FOLDER, workers, promotions);
        assertThat(stables).hasSize(12);
        assertThat(stables.get(2).getWorkers()).hasSize(4);
        stables.forEach(stable -> {
            assertThat(stable.getActiveType()).isNotNull();
            assertThat(stable.getName()).isNotNull();
            assertThat(stable.getWorkers()).isNotEmpty();
            assertThat(stable.getOwner()).isNotNull();
        });
    }

    @Test
    public void beltsDat() {
        List<Promotion> promotions = testImport.promotionsDat(TEST_DATA_FOLDER, "promos");
        List<Worker> workers = testImport.workersDat(TEST_DATA_FOLDER);
        List<RosterSplit> rosterSplits = testImport.rosterSplits(TEST_DATA_FOLDER,
                "promos",
                promotions);
        List<Title> titles = testImport.beltDat(TEST_DATA_FOLDER, promotions, workers, LocalDate.now(), rosterSplits);
        assertThat(titles).hasSize(69);
    }


    @Test
    public void eventDat() {
        List<Promotion> promotions = testImport.promotionsDat(TEST_DATA_FOLDER, "promos");
        List<RosterSplit> rosterSplits = testImport.rosterSplits(TEST_DATA_FOLDER,
                "promos",
                promotions);
        List<EventTemplate> eventTemplates = testImport.eventDat(TEST_DATA_FOLDER, rosterSplits, promotions);
        assertThat(eventTemplates).hasSize(68);
    }

    @Test
    public void relateDat() {
        List<Worker> workers = testImport.workersDat(TEST_DATA_FOLDER);
        List<WorkerRelationship> workerRelationships = testImport.relateDat(TEST_DATA_FOLDER,
                workers);
        assertThat(workerRelationships).hasSize(2842);
    }
}