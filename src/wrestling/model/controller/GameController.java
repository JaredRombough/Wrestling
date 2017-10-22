package wrestling.model.controller;

import wrestling.model.dirt.DirtSheet;
import java.io.IOException;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import wrestling.model.Promotion;
import wrestling.model.TagTeam;
import wrestling.model.Television;
import wrestling.model.Worker;
import wrestling.model.factory.ContractFactory;
import wrestling.model.factory.EventFactory;
import wrestling.model.factory.PromotionFactory;
import wrestling.model.factory.TitleFactory;
import wrestling.model.factory.WorkerFactory;

/**
 *
 * game controller handles game stuff
 */
public final class GameController implements Serializable {
    
    private Promotion playerPromotion;
    private List<Promotion> promotions = new ArrayList<>();
    private List<TagTeam> tagTeams = new ArrayList<>();
    private List<Television> television = new ArrayList<>();

    private final DirtSheet dirtSheet;
    private final ContractFactory contractFactory;
    private final EventFactory eventFactory;
    private final PromotionFactory promotionFactory;
    private final TitleFactory titleFactory;
    private final WorkerFactory workerFactory;

    private final DateManager dateManager;
    private final ContractManager contractManager;
    private final PromotionEventManager promotionEventManager;
    private final TitleManager titleManager;
    private final PromotionController promotionController;
    private final WorkerManager workerManager;
    private final BookingManager bookingManager;

    private final transient Logger logger = LogManager.getLogger(getClass());

    public GameController() throws IOException {
        //set the initial date here
        dateManager = new DateManager(LocalDate.of(2015, 1, 1));

        dirtSheet = new DirtSheet(dateManager);

        titleManager = new TitleManager(dirtSheet);
        contractManager = new ContractManager(dirtSheet);

        promotionEventManager = new PromotionEventManager();
        bookingManager = new BookingManager();
        workerManager = new WorkerManager(contractManager);
        eventFactory = new EventFactory(this);
        contractFactory = new ContractFactory(contractManager);

        promotionFactory = new PromotionFactory(this);
        titleFactory = new TitleFactory(titleManager);
        workerFactory = new WorkerFactory(this);

        promotionController = new PromotionController(this);

        promotionFactory.preparePromotions(this);

    }

    //only called by MainApp
    public void nextDay() {

        //iterate through all promotions
        for (Promotion promotion : promotions) {
            if (!promotion.equals(playerPromotion)) {
                getPromotionController().dailyUpdate(promotion);
            }
        }

        dateManager.nextDay();
    }

    public int averageWorkerPopularity(Promotion promotion) {
        int totalPop = 0;
        int averagePop = 0;

        if (!contractManager.getFullRoster(promotion).isEmpty()) {
            for (Worker worker : contractManager.getFullRoster(promotion)) {
                totalPop += worker.getPopularity();
            }
            averagePop = totalPop / contractManager.getFullRoster(promotion).size();
        }

        return averagePop;
    }

    public void setPlayerPromotion(Promotion promotion) {
        playerPromotion = promotion;
    }

    public Promotion playerPromotion() {
        return playerPromotion;
    }

    public List<TagTeam> getTagTeams(Promotion promotion) {
        List<TagTeam> teams = new ArrayList<>();
        tagTeams.stream().filter((tt) -> (contractManager.getFullRoster(promotion).containsAll(tt.getWorkers()))).forEach((tt) -> {
            teams.add(tt);
        });
        return teams;
    }

    public void setPromotions(List<Promotion> promotions) {
        this.promotions = promotions;
    }

    

    public void setTelevision(List<Television> television) {
        this.television = television;
    }

    /**
     * @return the promotions
     */
    public List<Promotion> getPromotions() {
        return promotions;
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
     * @return the dirtSheet
     */
    public DirtSheet getDirtSheet() {
        return dirtSheet;
    }

    /**
     * @return the tagTeams
     */
    public List<TagTeam> getTagTeams() {
        return tagTeams;
    }

    /**
     * @param tagTeams the tagTeams to set
     */
    public void setTagTeams(List<TagTeam> tagTeams) {
        this.tagTeams = tagTeams;
    }

    /**
     * @return the television
     */
    public List<Television> getTelevision() {
        return television;
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
    public PromotionEventManager getPromotionEventManager() {
        return promotionEventManager;
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
    public BookingManager getBookingManager() {
        return bookingManager;
    }

    /**
     * @return the workerManager
     */
    public WorkerManager getWorkerManager() {
        return workerManager;
    }

}
