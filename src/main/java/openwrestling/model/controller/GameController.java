package openwrestling.model.controller;

import lombok.Getter;
import openwrestling.Logging;
import openwrestling.manager.BankAccountManager;
import openwrestling.manager.ContractManager;
import openwrestling.manager.EntourageManager;
import openwrestling.manager.EventManager;
import openwrestling.manager.PromotionManager;
import openwrestling.manager.RosterSplitManager;
import openwrestling.manager.StableManager;
import openwrestling.manager.StaffManager;
import openwrestling.manager.TagTeamManager;
import openwrestling.manager.TitleManager;
import openwrestling.manager.WorkerManager;
import openwrestling.model.factory.ContractFactory;
import openwrestling.model.factory.EventFactory;
import openwrestling.model.factory.MatchFactory;
import openwrestling.model.factory.PromotionFactory;
import openwrestling.model.gameObjects.Event;
import openwrestling.model.gameObjects.EventTemplate;
import openwrestling.model.gameObjects.Promotion;
import openwrestling.model.gameObjects.Worker;
import openwrestling.model.manager.DateManager;
import openwrestling.model.manager.InjuryManager;
import openwrestling.model.manager.NewsManager;
import openwrestling.model.manager.RelationshipManager;
import openwrestling.model.manager.SegmentManager;
import openwrestling.model.segmentEnum.EventFrequency;
import org.apache.logging.log4j.Level;

import java.io.IOException;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Getter
public final class GameController extends Logging implements Serializable {

    private final ContractFactory contractFactory;
    private final EventFactory eventFactory;
    private final PromotionFactory promotionFactory;
    private final MatchFactory matchFactory;

    private final DateManager dateManager;
    private final ContractManager contractManager;
    private final EventManager eventManager;
    private final TitleManager titleManager;
    private final WorkerManager workerManager;
    private final PromotionManager promotionManager;
    private final TagTeamManager tagTeamManager;
    private final SegmentManager segmentManager;
    private final InjuryManager injuryManager;
    private final NewsManager newsManager;
    private final StaffManager staffManager;
    private final StableManager stableManager;
    private final RelationshipManager relationshipManager;
    private final BankAccountManager bankAccountManager;
    private final RosterSplitManager rosterSplitManager;
    private final EntourageManager entourageManager;

    private final PromotionController promotionController;

    private final int EVENT_MONTHS = 6;

    public GameController(boolean randomGame) throws IOException {
        //set the initial date here
        dateManager = new DateManager(LocalDate.of(2015, 1, 5));

        titleManager = new TitleManager(dateManager);

        bankAccountManager = new BankAccountManager();
        promotionManager = new PromotionManager(bankAccountManager);

        newsManager = new NewsManager();
        staffManager = new StaffManager();
        stableManager = new StableManager();
        relationshipManager = new RelationshipManager();
        rosterSplitManager = new RosterSplitManager();


        contractManager = new ContractManager(promotionManager,
                titleManager,
                newsManager,
                relationshipManager,
                bankAccountManager);

        workerManager = new WorkerManager(contractManager);
        entourageManager = new EntourageManager(workerManager);
        tagTeamManager = new TagTeamManager(workerManager);
        segmentManager = new SegmentManager(dateManager, tagTeamManager, stableManager);
        injuryManager = new InjuryManager(newsManager, workerManager);
        contractFactory = new ContractFactory(contractManager);

        eventManager = new EventManager(
                contractManager,
                dateManager,
                segmentManager);

        matchFactory = new MatchFactory(segmentManager, dateManager, injuryManager, workerManager);


        eventFactory = new EventFactory(
                contractManager,
                eventManager,
                matchFactory,
                promotionManager,
                titleManager,
                workerManager,
                tagTeamManager,
                stableManager,
                relationshipManager,
                newsManager,
                bankAccountManager,
                segmentManager);

        promotionFactory = new PromotionFactory(
                contractFactory,
                dateManager,
                promotionManager,
                workerManager,
                staffManager,
                bankAccountManager,
                contractManager);

        promotionController = new PromotionController(
                contractFactory,
                eventFactory,
                matchFactory,
                contractManager,
                dateManager,
                eventManager,
                titleManager,
                workerManager,
                newsManager);

        if (randomGame) {
            promotionFactory.preparePromotions();
        }

    }

    public void initializeGameData() {
        for (Promotion promotion : promotionManager.getPromotions()) {
            if (eventManager.getEventTemplates(promotion).isEmpty() && !promotion.equals(promotionManager.getPlayerPromotion())) {
                eventFactory.createMonthlyEvents(promotion);
            }
        }
        initialBookEventTemplates(getDateManager().today());
    }

    //only called by MainApp
    public void nextDay() {
        logger.log(Level.DEBUG, "nextDay start");
        contractManager.dailyUpdate(dateManager.today());
        logger.log(Level.DEBUG, "promotion loop");
        for (Promotion promotion : promotionManager.getPromotions()) {
            injuryManager.dailyUpdate(dateManager.today(), promotion);
            promotionController.trainerUpdate(promotion);
            if (dateManager.isPayDay()) {
                promotionController.payDay(promotion, dateManager.today());
            }
            if (!promotionManager.getPlayerPromotion().equals(promotion)) {
                promotionController.dailyUpdate(promotion);


            }
        }

        List<Event> events = promotionManager.getPromotions().stream()
                .filter(promotion -> !promotionManager.getPlayerPromotion().equals(promotion))
                .map(this::eventOnDay)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        logger.log(Level.DEBUG, "after eventOnDay");

       List<EventTemplate> eventTemplates =  events.stream()
               .map(event -> promotionController.updateEventTemplate(event.getEventTemplate()))
               .collect(Collectors.toList());

        logger.log(Level.DEBUG, "after updateEventTemplate");
        eventManager.createEvents(events);
        eventManager.createEventTemplates(eventTemplates);

        if (dateManager.today().getDayOfMonth() == 1) {
            bookEventTemplatesFuture(dateManager.today().minusMonths(1).getMonth().getValue());
        }
        dateManager.nextDay();
        logger.log(Level.DEBUG, "nextDay end");
    }

    public Event eventOnDay(Promotion promotion) {
        Event eventToday = eventManager.getEventOnDate(promotion, dateManager.today());
        if (eventToday != null) {
            List<Worker> roster = workerManager.selectRoster(promotion);
            if (roster.size() >= 2) {
                eventToday = promotionController.bookEvent(eventToday, promotion);
            }
        }
        return eventToday;
    }

    public void bookEventTemplatesFuture(int month) {
        logger.log(Level.DEBUG, "bookEventTemplatesFuture");
        YearMonth thisMonthNextYear = YearMonth.of(dateManager.today().plusYears(1).getYear(), month);
        initialBookEventTemplates(LocalDate.of(thisMonthNextYear.getYear(), thisMonthNextYear.getMonth(), 1));
    }

    public void initialBookEventTemplates(LocalDate startDate) {
        logger.log(Level.DEBUG, "initialBookEventTemplates " + startDate.toString());
        YearMonth yearMonth = YearMonth.of(startDate.getYear(), startDate.getMonth());
        List<Event> toInsert = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            for (EventTemplate eventTemplate : eventManager.getEventTemplates()) {
                if (eventTemplate.getMonth() == yearMonth.getMonth().getValue()
                        || eventTemplate.getEventFrequency().equals(EventFrequency.WEEKLY)) {
                    toInsert.addAll(promotionController.bookEventTemplate(eventTemplate, yearMonth));
                }
            }
            yearMonth = yearMonth.plusMonths(1);
        }
        eventManager.createEvents(toInsert);
    }

}
