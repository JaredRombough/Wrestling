package openwrestling.model.controller;

import openwrestling.manager.ContractManager;
import openwrestling.manager.EventManager;
import openwrestling.manager.TitleManager;
import openwrestling.manager.WorkerManager;
import openwrestling.model.factory.ContractFactory;
import openwrestling.model.factory.EventFactory;
import openwrestling.model.factory.MatchFactory;
import openwrestling.model.gameObjects.EventTemplate;
import openwrestling.model.manager.DateManager;
import openwrestling.model.manager.NewsManager;
import openwrestling.model.segmentEnum.EventRecurrence;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class PromotionControllerTest {

    private PromotionController promotionController;


    @Before
    public void setUp() {
        promotionController = new PromotionController(
                mock(ContractFactory.class),
                mock(EventFactory.class),
                mock(MatchFactory.class),
                mock(ContractManager.class),
                mock(DateManager.class),
                mock(EventManager.class),
                mock(TitleManager.class),
                mock(WorkerManager.class),
                mock(NewsManager.class)
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