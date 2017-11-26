package wrestling.model.controller;

import wrestling.model.factory.MatchFactory;
import java.io.IOException;
import java.io.Serializable;
import java.time.LocalDate;
import wrestling.model.Promotion;
import wrestling.model.factory.ContractFactory;
import wrestling.model.factory.EventFactory;
import wrestling.model.factory.PromotionFactory;
import wrestling.model.factory.TitleFactory;
import wrestling.model.factory.WorkerFactory;
import wrestling.model.manager.EventWorkerManager;
import wrestling.model.manager.ContractManager;
import wrestling.model.manager.DateManager;
import wrestling.model.manager.MatchManager;
import wrestling.model.manager.EventManager;
import wrestling.model.manager.PromotionManager;
import wrestling.model.manager.TagTeamManager;
import wrestling.model.manager.TelevisionManager;
import wrestling.model.manager.TitleManager;
import wrestling.model.manager.WorkerManager;

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
    private final EventWorkerManager bookingManager;
    private final TelevisionManager televisionManager;
    private final PromotionManager promotionManager;
    private final TagTeamManager tagTeamManager;
    private final MatchManager matchManager;

    private final PromotionController promotionController;

    public GameController(boolean randomGame) throws IOException {
        //set the initial date here
        dateManager = new DateManager(LocalDate.of(2015, 1, 1));

        titleManager = new TitleManager(dateManager);

        bookingManager = new EventWorkerManager();
        televisionManager = new TelevisionManager();
        promotionManager = new PromotionManager();
        workerFactory = new WorkerFactory();
        matchManager = new MatchManager();

        contractManager = new ContractManager(promotionManager);
        eventManager = new EventManager(contractManager, matchManager);

        titleFactory = new TitleFactory(titleManager);
        matchFactory = new MatchFactory(matchManager);
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
                eventManager,
                promotionManager,
                workerManager);

        promotionController = new PromotionController(
                contractFactory,
                eventFactory,
                matchFactory,
                bookingManager,
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

        dateManager.nextDay();
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
     * @return the bookingManager
     */
    public EventWorkerManager getBookingManager() {
        return bookingManager;
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
