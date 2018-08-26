package wrestling.model.controller;

import java.io.IOException;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import wrestling.model.EventTemplate;
import wrestling.model.NewsItem;
import wrestling.model.Promotion;
import wrestling.model.factory.ContractFactory;
import wrestling.model.factory.EventFactory;
import wrestling.model.factory.MatchFactory;
import wrestling.model.factory.PromotionFactory;
import wrestling.model.factory.TitleFactory;
import wrestling.model.factory.WorkerFactory;
import wrestling.model.manager.ContractManager;
import wrestling.model.manager.DateManager;
import wrestling.model.manager.EventManager;
import wrestling.model.manager.InjuryManager;
import wrestling.model.manager.PromotionManager;
import wrestling.model.manager.SegmentManager;
import wrestling.model.manager.TagTeamManager;
import wrestling.model.manager.TitleManager;
import wrestling.model.manager.WorkerManager;
import wrestling.model.segmentEnum.EventFrequency;
import wrestling.view.NextDayScreenController;

/**
 *
 * game controller handles game stuff
 */
public final class GameController implements Serializable {

    private final ContractFactory contractFactory;
    private final EventFactory eventFactory;
    private final PromotionFactory promotionFactory;
    private final TitleFactory titleFactory;
    private final WorkerFactory workerFactory;
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

    private final PromotionController promotionController;

    private final int EVENT_MONTHS = 6;

    public GameController(boolean randomGame) throws IOException {
        //set the initial date here
        dateManager = new DateManager(LocalDate.of(2015, 1, 5));

        titleManager = new TitleManager(dateManager);

        promotionManager = new PromotionManager();
        workerFactory = new WorkerFactory();

        contractManager = new ContractManager(promotionManager);

        tagTeamManager = new TagTeamManager(contractManager);

        segmentManager = new SegmentManager(dateManager, tagTeamManager);

        injuryManager = new InjuryManager(segmentManager);

        eventManager = new EventManager(
                contractManager,
                dateManager,
                segmentManager);

        titleFactory = new TitleFactory(titleManager);
        matchFactory = new MatchFactory(segmentManager, dateManager);

        workerManager = new WorkerManager(contractManager);
        contractFactory = new ContractFactory(contractManager);

        eventFactory = new EventFactory(
                contractManager,
                eventManager,
                matchFactory,
                segmentManager,
                promotionManager,
                titleManager,
                workerManager,
                dateManager, getInjuryManager());

        promotionFactory = new PromotionFactory(
                contractFactory,
                workerFactory,
                contractManager,
                dateManager,
                promotionManager,
                workerManager,
                eventManager);

        promotionController = new PromotionController(
                contractFactory,
                eventFactory,
                matchFactory,
                contractManager,
                dateManager,
                eventManager,
                titleManager,
                workerManager);

        if (randomGame) {
            promotionFactory.preparePromotions();
        }

    }

    public void initializeGameData() {
        for (Promotion promotion : promotionManager.getPromotions()) {
            if (eventManager.getEventTemplates(promotion).isEmpty() && !promotion.equals(promotionManager.playerPromotion())) {
                eventFactory.createMonthlyEvents(promotion);
            }
        }
        initialBookEventTemplates(getDateManager().today());
    }

    //only called by MainApp
    public void nextDay() {

        getInjuryManager().dailyUpdate(dateManager.today());

        //iterate through all promotions
        for (Promotion promotion : promotionManager.aiPromotions()) {
            getPromotionController().dailyUpdate(promotion);

        }

        if (dateManager.today().getDayOfMonth() == 1) {
            bookEventTemplatesFuture(dateManager.today().minusMonths(1).getMonth());
        }

        dateManager.nextDay();
    }

    public void bookEventTemplatesFuture(Month month) {
        YearMonth thisMonthNextYear = YearMonth.of(dateManager.today().plusYears(1).getYear(), month);
        for (EventTemplate eventTemplate : eventManager.getActiveEventTemplatesFuture(thisMonthNextYear)) {
            if (eventTemplate.getMonth().equals(month)) {
                promotionController.bookEventTemplate(eventTemplate,
                        thisMonthNextYear);
            }

        }
    }

    public void initialBookEventTemplates(LocalDate startDate) {
        YearMonth yearMonth = YearMonth.of(startDate.getYear(), startDate.getMonth());
        for (int i = 0; i < 12; i++) {
            for (EventTemplate eventTemplate : eventManager.getEventTemplates()) {
                if (eventTemplate.getMonth().equals(yearMonth.getMonth())
                        || eventTemplate.getEventFrequency().equals(EventFrequency.WEEKLY)) {
                    promotionController.bookEventTemplate(eventTemplate, yearMonth);
                }
            }
            yearMonth = yearMonth.plusMonths(1);
        }
    }

    /**
     * @return the contractFactory
     */
    public ContractFactory getContractFactory() {
        return contractFactory;
    }

    /**
     * @return the eventFactory
     */
    public EventFactory getEventFactory() {
        return eventFactory;
    }

    /**
     * @return the titleFactory
     */
    public TitleFactory getTitleFactory() {
        return titleFactory;
    }

    /**
     * @return the workerFactory
     */
    public WorkerFactory getWorkerFactory() {
        return workerFactory;
    }

    /**
     * @return the promotionFactory
     */
    public PromotionFactory getPromotionFactory() {
        return promotionFactory;
    }

    /**
     * @return the contractController
     */
    public ContractManager getContractManager() {
        return contractManager;
    }

    /**
     * @return the promotionEventManager
     */
    public EventManager getEventManager() {
        return eventManager;
    }

    /**
     * @return the promotionController
     */
    public PromotionController getPromotionController() {
        return promotionController;
    }

    /**
     * @return the dateManager
     */
    public DateManager getDateManager() {
        return dateManager;
    }

    /**
     * @return the titleManager
     */
    public TitleManager getTitleManager() {
        return titleManager;
    }

    /**
     * @return the workerManager
     */
    public WorkerManager getWorkerManager() {
        return workerManager;
    }

    /**
     * @return the promotionManager
     */
    public PromotionManager getPromotionManager() {
        return promotionManager;
    }

    /**
     * @return the tagTeamManager
     */
    public TagTeamManager getTagTeamManager() {
        return tagTeamManager;
    }

    /**
     * @return the matchFactory
     */
    public MatchFactory getMatchFactory() {
        return matchFactory;
    }

    /**
     * @return the matchManager
     */
    public SegmentManager getSegmentManager() {
        return segmentManager;
    }

    /**
     * @return the injuryManager
     */
    public InjuryManager getInjuryManager() {
        return injuryManager;
    }

}
