package openwrestling.model.controller;

import java.io.IOException;
import java.io.Serializable;
import static java.lang.Math.random;
import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import org.apache.commons.lang3.RandomUtils;
import openwrestling.model.EventTemplate;
import openwrestling.model.NewsItem;
import static openwrestling.model.constants.Words.ACTIVITIES;
import static openwrestling.model.constants.Words.BODY_PARTS;
import openwrestling.model.factory.ContractFactory;
import openwrestling.model.factory.EventFactory;
import openwrestling.model.factory.MatchFactory;
import openwrestling.model.factory.PromotionFactory;
import openwrestling.model.factory.TitleFactory;
import openwrestling.model.manager.ContractManager;
import openwrestling.model.manager.DateManager;
import openwrestling.model.manager.EventManager;
import openwrestling.model.manager.InjuryManager;
import openwrestling.model.manager.NewsManager;
import openwrestling.model.manager.PromotionManager;
import openwrestling.model.manager.RelationshipManager;
import openwrestling.model.manager.SegmentManager;
import openwrestling.model.manager.StableManager;
import openwrestling.model.manager.StaffManager;
import openwrestling.model.manager.TagTeamManager;
import openwrestling.model.manager.TitleManager;
import openwrestling.model.manager.WorkerManager;
import openwrestling.model.modelView.PromotionView;
import openwrestling.model.segmentEnum.EventFrequency;
import openwrestling.view.utility.ViewUtils;

/**
 *
 * game controller handles game stuff
 */
public final class GameController implements Serializable {

    private final ContractFactory contractFactory;
    private final EventFactory eventFactory;
    private final PromotionFactory promotionFactory;
    private final TitleFactory titleFactory;
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

    private final PromotionController promotionController;

    private final int EVENT_MONTHS = 6;

    public GameController(boolean randomGame) throws IOException {
        //set the initial date here
        dateManager = new DateManager(LocalDate.of(2015, 1, 5));

        titleManager = new TitleManager(dateManager);

        promotionManager = new PromotionManager();
        newsManager = new NewsManager();
        staffManager = new StaffManager();
        stableManager = new StableManager();
        relationshipManager = new RelationshipManager();

        contractManager = new ContractManager(promotionManager, titleManager, newsManager, getRelationshipManager());

        tagTeamManager = new TagTeamManager(contractManager);

        segmentManager = new SegmentManager(dateManager, tagTeamManager, stableManager);

        injuryManager = new InjuryManager(newsManager);

        eventManager = new EventManager(
                contractManager,
                dateManager,
                segmentManager);

        titleFactory = new TitleFactory(titleManager);
        matchFactory = new MatchFactory(segmentManager, dateManager, injuryManager);

        workerManager = new WorkerManager(contractManager);
        contractFactory = new ContractFactory(contractManager);

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
                newsManager);

        promotionFactory = new PromotionFactory(
                contractFactory,
                dateManager,
                promotionManager,
                workerManager,
                staffManager);

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
        for (PromotionView promotion : promotionManager.getPromotions()) {
            if (promotion.getEventTemplates().isEmpty() && !promotion.equals(promotionManager.playerPromotion())) {
                eventFactory.createMonthlyEvents(promotion);
            }
        }
        initialBookEventTemplates(getDateManager().today());
    }

    //only called by MainApp
    public void nextDay() {

        contractManager.dailyUpdate(dateManager.today());

        for (PromotionView promotion : promotionManager.getPromotions()) {
            injuryManager.dailyUpdate(dateManager.today(), promotion);
            promotionController.trainerUpdate(promotion);
            if (dateManager.isPayDay()) {
                promotionController.payDay(promotion, dateManager.today());
            }
            if (!promotionManager.playerPromotion().equals(promotion)) {
                promotionController.dailyUpdate(promotion);
            }
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

    /**
     * @return the newsManager
     */
    public NewsManager getNewsManager() {
        return newsManager;
    }

    /**
     * @return the staffManager
     */
    public StaffManager getStaffManager() {
        return staffManager;
    }

    /**
     * @return the stableManager
     */
    public StableManager getStableManager() {
        return stableManager;
    }

    /**
     * @return the relationshipManager
     */
    public RelationshipManager getRelationshipManager() {
        return relationshipManager;
    }

}
