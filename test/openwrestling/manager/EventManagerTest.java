package openwrestling.manager;

import openwrestling.database.Database;
import openwrestling.model.gameObjects.EventTemplate;
import openwrestling.model.manager.DateManager;
import openwrestling.model.manager.SegmentManager;
import openwrestling.model.segmentEnum.EventBroadcast;
import openwrestling.model.segmentEnum.EventFrequency;
import openwrestling.model.segmentEnum.EventRecurrence;
import openwrestling.model.segmentEnum.EventVenueSize;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class EventManagerTest {


    private EventManager eventManager;

    @Before
    public void setUp() {
        Database.createNewDatabase("testdb");
        DateManager mockDateManager = mock(DateManager.class);
        when(mockDateManager.today()).thenReturn(LocalDate.now());
        eventManager = new EventManager(mock(ContractManager.class), mockDateManager, mock(SegmentManager.class));
    }

    @Test
    public void createEventTemplates() {
        String name = RandomStringUtils.random(10);

        EventTemplate eventTemplate = EventTemplate.builder()
                .name(name)
                .eventBroadcast(EventBroadcast.TELEVISION)
                .eventFrequency(EventFrequency.WEEKLY)
                .eventRecurrence(EventRecurrence.LIMITED)
                .eventVenueSize(EventVenueSize.LARGE)
                .bookedUntil(LocalDate.now().plusDays(100))
                .build();

        eventManager.createEventTemplates(List.of(eventTemplate));

        List<EventTemplate> eventTemplates = Database.selectAll(EventTemplate.class);

        assertThat(eventTemplates).isNotNull().hasOnlyOneElementSatisfying(savedTemplate -> {
            assertThat(savedTemplate.getName()).isEqualTo(name);
            assertThat(savedTemplate.getEventBroadcast()).isEqualTo(EventBroadcast.TELEVISION);
        });
    }
}