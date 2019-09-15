package openwrestling.manager;

import openwrestling.database.Database;
import openwrestling.entities.TagTeamEntity;
import openwrestling.model.factory.PersonFactory;
import openwrestling.model.gameObjects.TagTeam;
import openwrestling.model.gameObjects.Worker;
import openwrestling.model.segmentEnum.ActiveType;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class TagTeamManagerTest {

    @Before
    public void setUp() {
        Database.createNewDatabase("testdb");
    }

    @Test
    public void createTagTeams() {
        Worker worker = Database.insertGameObject(PersonFactory.randomWorker());
        Worker worker2 = Database.insertGameObject(PersonFactory.randomWorker());

        String name = RandomStringUtils.random(10);
        ActiveType activeType = ActiveType.ACTIVE;
        int experience = 12;

        TagTeam tagTeam = TagTeam.builder()
                .workers(List.of(worker, worker2))
                .activeType(activeType)
                .experience(experience)
                .name(name)
                .build();

        TagTeamManager tagTeamManager = new TagTeamManager(mock(WorkerManager.class));

        tagTeamManager.createTagTeams(List.of(tagTeam));

        List<TagTeam> tagTeams = Database.selectAll(TagTeam.class);

        assertThat(tagTeams).isNotNull().hasOnlyOneElementSatisfying(savedTagTeam -> {
            assertThat(savedTagTeam.getName()).isEqualTo(name);
            assertThat(savedTagTeam.getExperience()).isEqualTo(experience);
            assertThat(savedTagTeam.getActiveType()).isEqualTo(activeType);
        });
    }
}