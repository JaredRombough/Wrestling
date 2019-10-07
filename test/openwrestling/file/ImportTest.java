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

    private ImportHelper importHelper;

    @Before
    public void setUp() {
        importHelper = new ImportHelper(new File(".\\test_data"));
    }

    @Test
    public void promotionsDat() {
        List<Promotion> promotions = importHelper.promotionsDat("promos");
        assertThat(promotions).isNotNull().hasSize(18);
        assertThat(promotions.get(0).getName().substring(0, 5)).isEqualTo("World");
        assertThat(promotions.get(0).getName().toCharArray()).hasSize(26);
        assertThat(promotions.get(0).getLevel()).isEqualTo(5);
        assertThat(promotions.get(8).getLevel()).isEqualTo(1);
        assertThat(promotions.get(5).getLevel()).isEqualTo(3);
        assertThat(promotions.get(16).getLevel()).isEqualTo(2);
        for (int i = 0; i < promotions.size(); i++) {
            Promotion promotion = promotions.get(i);
            assertThat(promotion.getName()).isNotNull();
            assertThat(promotion.getLevel()).isNotNull();
            assertThat(promotion.getShortName()).isNotNull();
            assertThat(promotion.getPopularity()).isEqualTo(50);
        }
    }

    @Test
    public void rosterSplits() {
        List<Promotion> promotions = importHelper.promotionsDat("promos_with_splits");
        List<RosterSplit> rosterSplits = importHelper.rosterSplits(
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
        List<Worker> workers = importHelper.workersDat();
        List<Worker> updatedWorkers = importHelper.setManagers(workers);
        assertThat(updatedWorkers).hasSize(41);
    }

    @Test
    public void otherPromotions() {
        List<Promotion> promotions = importHelper.otherPromotions();
        assertThat(promotions).hasSize(11);
    }

    @Test
    public void otherPromotionContracts() {
        List<Promotion> promotions = importHelper.otherPromotions();
        List<Worker> workers = importHelper.workersDat();
        List<Contract> contracts = importHelper.otherPromotionContracts(promotions, workers, LocalDate.now());
        assertThat(contracts).hasSize(245);
    }

    @Test
    public void processOther() {
        List<Worker> workers = importHelper.workersDat();
        List<Promotion> promotions = importHelper.otherPromotions();
        List<Contract> contracts = importHelper.otherPromotionContracts(promotions, workers, LocalDate.now());
        List<Promotion> updatedPromotions = importHelper.updateOther(promotions, contracts);
        assertThat(updatedPromotions).hasSize(11);
    }

    @Test
    public void workersDat() {
        List<Worker> workers = importHelper.workersDat();
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
        List<Promotion> promotions = importHelper.promotionsDat("promos");
        List<Worker> workers = importHelper.workersDat();
        List<Contract> contracts = importHelper.contracts(workers, promotions, LocalDate.now());

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
        List<Worker> workers = importHelper.workersDat();
        List<TagTeam> tagTeams = importHelper.teamsDat(workers);
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
        List<Worker> workers = importHelper.workersDat();
        List<Promotion> promotions = importHelper.promotionsDat("promos");
        List<Stable> stables = importHelper.stablesDat(workers, promotions);
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
        List<Promotion> promotions = importHelper.promotionsDat("promos");
        List<Worker> workers = importHelper.workersDat();
        List<RosterSplit> rosterSplits = importHelper.rosterSplits(
                "promos",
                promotions);
        List<Title> titles = importHelper.beltDat(promotions, workers, LocalDate.now(), rosterSplits);
        assertThat(titles).hasSize(69);
    }


    @Test
    public void eventDat() {
        List<Promotion> promotions = importHelper.promotionsDat("promos");
        List<RosterSplit> rosterSplits = importHelper.rosterSplits(
                "promos",
                promotions);
        List<EventTemplate> eventTemplates = importHelper.eventDat(rosterSplits, promotions);
        assertThat(eventTemplates).hasSize(68);
    }

    @Test
    public void relateDat() {
        List<Worker> workers = importHelper.workersDat();
        List<WorkerRelationship> workerRelationships = importHelper.relateDat(workers);
        assertThat(workerRelationships).hasSize(2842);
    }
}