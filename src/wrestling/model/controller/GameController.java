package wrestling.model.controller;

import wrestling.model.factory.MatchFactory;
import java.io.IOException;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import wrestling.model.Event;
import wrestling.model.EventName;
import wrestling.model.Promotion;
import wrestling.model.Television;
import wrestling.model.factory.ContractFactory;
import wrestling.model.factory.EventFactory;
import wrestling.model.factory.PromotionFactory;
import wrestling.model.factory.TitleFactory;
import wrestling.model.factory.WorkerFactory;
import wrestling.model.manager.ContractManager;
import wrestling.model.manager.DateManager;
import wrestling.model.manager.MatchManager;
import wrestling.model.manager.EventManager;
import wrestling.model.manager.PromotionManager;
import wrestling.model.manager.TagTeamManager;
import wrestling.model.manager.TelevisionManager;
import wrestling.model.manager.TitleManager;
import wrestling.model.manager.WorkerManager;
import wrestling.model.utility.ModelUtilityFunctions;

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
    private final TelevisionManager televisionManager;
    private final PromotionManager promotionManager;
    private final TagTeamManager tagTeamManager;
    private final MatchManager matchManager;

    private final PromotionController promotionController;

    private final int EVENT_MONTHS = 6;

    public GameController(boolean randomGame) throws IOException {
        //set the initial date here
        dateManager = new DateManager(LocalDate.of(2015, 1, 1));

        titleManager = new TitleManager(dateManager);

        televisionManager = new TelevisionManager();
        promotionManager = new PromotionManager();
        workerFactory = new WorkerFactory();
        matchManager = new MatchManager(dateManager);

        contractManager = new ContractManager(promotionManager);
        eventManager = new EventManager(
                contractManager,
                dateManager,
                matchManager);

        titleFactory = new TitleFactory(titleManager);
        matchFactory = new MatchFactory(matchManager, dateManager);
        tagTeamManager = new TagTeamManager(contractManager);
        workerManager = new WorkerManager(contractManager);
        contractFactory = new ContractFactory(contractManager);

        eventFactory = new EventFactory(
                contractManager,
                eventManager,
                matchFactory,
                matchManager,
                promotionManager,
                titleManager,
                workerManager);

        promotionFactory = new PromotionFactory(
                contractFactory,
                workerFactory,
                contractManager,
                dateManager,
                promotionManager,
                workerManager);

        promotionController = new PromotionController(
                contractFactory,
                eventFactory,
                matchFactory,
                contractManager,
                dateManager,
                eventManager,
                televisionManager,
                titleManager,
                workerManager);

        if (randomGame) {
            promotionFactory.preparePromotions();
        }

    }

    //only called by MainApp
    public void nextDay() {

        //iterate through all promotions
        for (Promotion promotion : promotionManager.aiPromotions()) {
            getPromotionController().dailyUpdate(promotion);

        }

        if (dateManager.today().getDayOfMonth() == 1) {
            monthlyUpdate();
        }

        dateManager.nextDay();
    }

    public void initializeEvents() {
        YearMonth yearMonth = YearMonth.from(dateManager.today());
        for (int i = 0; i < EVENT_MONTHS; i++) {
            bookEventsForMonth(yearMonth);
            yearMonth = yearMonth.plusMonths(1);
        }
    }

    private void monthlyUpdate() {
        YearMonth yearMonth = YearMonth.from(dateManager.today());
        yearMonth = yearMonth.plusMonths(EVENT_MONTHS);
        bookEventsForMonth(yearMonth);
    }

    private void bookEventsForMonth(YearMonth yearMonth) {
        LocalDate currentDate = LocalDate.of(yearMonth.getYear(), yearMonth.getMonthValue(), 1);
        List<LocalDate> weekends = new ArrayList<>();

        while (currentDate.getMonth().equals(yearMonth.getMonth())) {

            for (Promotion promotion : promotionManager.getPromotions()) {
                List<Television> tvOnDate = televisionManager.tvOnDate(promotion, currentDate);
                for (Television television : tvOnDate) {
                    Event eventOnDate = eventManager.getEventOnDate(promotion, currentDate);
                    if (eventOnDate == null || !television.equals(eventOnDate.getTelevision())) {
                        promotionController.bookNextEvent(promotion, currentDate, television);
                    }
                }
            }
            if (currentDate.getDayOfWeek().toString().equals("SUNDAY")
                    || currentDate.getDayOfWeek().toString().equals("SATURDAY")) {
                weekends.add(currentDate);
            }
            currentDate = LocalDate.from(currentDate).plusDays(1);
        }

        //add monthly events
        for (Promotion promotion : promotionManager.getPromotions()) {
            LocalDate eventDate = null;
            do {
                eventDate = weekends.get(ModelUtilityFunctions.randRange(0, weekends.size() - 1));
            } while (eventManager.getEventOnDate(promotion, eventDate) != null);

            EventName eventName = eventManager.getEventName(promotion, yearMonth.getMonth());
            if (eventName != null) {
                promotionController.bookNextEvent(promotion, eventDate, eventName);
            } else {
                promotionController.bookNextEvent(promotion, eventDate);
            }

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
     * @return the televisionManager
     */
    public TelevisionManager getTelevisionManager() {
        return televisionManager;
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
    public MatchManager getMatchManager() {
        return matchManager;
    }

}
