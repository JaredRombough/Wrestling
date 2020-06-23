package openwrestling.model.controller;

import openwrestling.manager.ContractManager;
import openwrestling.manager.DateManager;
import openwrestling.manager.NewsManager;
import openwrestling.manager.StaffManager;
import openwrestling.manager.TitleManager;
import openwrestling.manager.WorkerManager;
import openwrestling.model.factory.ContractFactory;
import openwrestling.model.factory.EventFactory;
import org.junit.Before;

import static org.mockito.Mockito.mock;

public class PromotionControllerTest {

    private PromotionController promotionController;


    @Before
    public void setUp() {
        promotionController = new PromotionController(
                mock(ContractFactory.class),
                mock(EventFactory.class),
                mock(ContractManager.class),
                mock(DateManager.class),
                mock(TitleManager.class),
                mock(WorkerManager.class),
                mock(NewsManager.class),
                mock(StaffManager.class)
        );
    }

//    @Test
//    public void updateEventTemplate() {
//        LocalDate date = LocalDate.now();
//        EventTemplate eventTemplate = EventTemplate.builder().build();
//        eventTemplate.setNextDate(date);
//        eventTemplate.setEventRecurrence(EventRecurrence.LIMITED);
//        eventTemplate.setEventsLeft(3);
//        EventTemplate updated = promotionController.updateEventTemplate(eventTemplate);
//        assertThat(updated.getNextDate()).isNotNull().isNotEqualTo(date);
//        assertThat(updated.getEventsLeft()).isEqualTo(2);
//    }
}