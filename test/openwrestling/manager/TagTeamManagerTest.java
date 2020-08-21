package openwrestling.manager;

import openwrestling.database.Database;
import openwrestling.model.SegmentItem;
import openwrestling.model.factory.PersonFactory;
import openwrestling.model.gameObjects.TagTeam;
import openwrestling.model.gameObjects.Worker;
import openwrestling.model.segment.constants.ActiveType;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static openwrestling.TestUtils.TEST_DB_PATH;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class TagTeamManagerTest {
    private Database database;

    private WorkerManager workerManager;
    private TagTeamManager tagTeamManager;

    @Before
    public void setUp() {
        database = new Database(TEST_DB_PATH);
        workerManager = new WorkerManager(database, mock(ContractManager.class));
        tagTeamManager = new TagTeamManager(database, workerManager);
    }

    @Test
    public void createTagTeams() {
        Worker worker = workerManager.createWorker(PersonFactory.randomWorker());
        Worker worker2 = workerManager.createWorker(PersonFactory.randomWorker());

        String name = RandomStringUtils.random(10);
        ActiveType activeType = ActiveType.ACTIVE;
        int experience = 12;

        TagTeam tagTeam = TagTeam.builder()
                .workers(List.of(worker, worker2))
                .activeType(activeType)
                .experience(experience)
                .name(name)
                .build();

        tagTeamManager.createTagTeams(List.of(tagTeam));

        List<TagTeam> tagTeams = tagTeamManager.getTagTeams();

        assertThat(tagTeams).isNotNull().hasOnlyOneElementSatisfying(savedTagTeam -> {
            assertThat(savedTagTeam.getName()).isEqualTo(name);
            assertThat(savedTagTeam.getExperience()).isEqualTo(experience);
            assertThat(savedTagTeam.getActiveType()).isEqualTo(activeType);
            assertThat(savedTagTeam.getSegmentItems()).extracting(SegmentItem::getLongName)
                    .containsOnly(worker.getLongName(), worker2.getLongName());
        });
    }
}