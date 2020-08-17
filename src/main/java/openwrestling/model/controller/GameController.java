package openwrestling.model.controller;

import lombok.Getter;
import openwrestling.Logging;
import openwrestling.database.Database;
import openwrestling.manager.BankAccountManager;
import openwrestling.manager.BroadcastTeamManager;
import openwrestling.manager.ContractManager;
import openwrestling.manager.DateManager;
import openwrestling.manager.EntourageManager;
import openwrestling.manager.EventManager;
import openwrestling.manager.GameObjectManager;
import openwrestling.manager.GameSettingManager;
import openwrestling.manager.InjuryManager;
import openwrestling.manager.MonthlyReviewManager;
import openwrestling.manager.NewsManager;
import openwrestling.manager.PromotionManager;
import openwrestling.manager.RelationshipManager;
import openwrestling.manager.RosterSplitManager;
import openwrestling.manager.SegmentManager;
import openwrestling.manager.StableManager;
import openwrestling.manager.StaffManager;
import openwrestling.manager.TagTeamManager;
import openwrestling.manager.TitleManager;
import openwrestling.manager.WorkerManager;
import openwrestling.model.controller.nextDay.DailyContractUpdate;
import openwrestling.model.controller.nextDay.DailyEventBooker;
import openwrestling.model.controller.nextDay.DailyRelationshipUpdate;
import openwrestling.model.controller.nextDay.DailyTransactions;
import openwrestling.model.controller.nextDay.MonthlyReviewController;
import openwrestling.model.controller.nextDay.NextDayController;
import openwrestling.model.factory.ContractFactory;
import openwrestling.model.factory.EventFactory;
import openwrestling.model.factory.MatchFactory;
import openwrestling.model.factory.RandomGameAssetGenerator;
import openwrestling.model.gameObjects.Event;
import openwrestling.model.gameObjects.EventTemplate;
import openwrestling.model.gameObjects.Promotion;
import org.apache.logging.log4j.Level;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

import static openwrestling.model.factory.EventFactory.getInitialEventsForEventTemplate;

@Getter
public final class GameController extends Logging implements Serializable {

    private final ContractFactory contractFactory;
    private final EventFactory eventFactory;
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
    private final BroadcastTeamManager broadcastTeamManager;
    private final GameSettingManager gameSettingManager;
    private final MonthlyReviewManager monthlyReviewManager;
    private final NextDayController nextDayController;
    private final DailyEventBooker dailyEventBooker;
    private final DailyContractUpdate dailyContractUpdate;
    private final DailyTransactions dailyTransactions;
    private final DailyRelationshipUpdate dailyRelationshipUpdate;
    private final MonthlyReviewController monthlyReviewController;
    private final PromotionController promotionController;
    private final int EVENT_MONTHS = 6;
    private List<GameObjectManager> managers;

    public GameController(Database database, boolean randomGame) {

        dateManager = new DateManager(database);

        gameSettingManager = new GameSettingManager(database);

        bankAccountManager = new BankAccountManager(database);
        promotionManager = new PromotionManager(database, bankAccountManager, gameSettingManager);

        newsManager = new NewsManager(database);

        relationshipManager = new RelationshipManager(database);

        broadcastTeamManager = new BroadcastTeamManager(database);

        contractManager = new ContractManager(database, bankAccountManager);

        workerManager = new WorkerManager(database, contractManager);
        rosterSplitManager = new RosterSplitManager(database, workerManager);
        staffManager = new StaffManager(database, contractManager);
        titleManager = new TitleManager(database, dateManager, workerManager);
        stableManager = new StableManager(database, workerManager);
        entourageManager = new EntourageManager(database, workerManager);
        tagTeamManager = new TagTeamManager(database, workerManager);
        segmentManager = new SegmentManager(database, dateManager, tagTeamManager, stableManager);
        injuryManager = new InjuryManager(database, newsManager, workerManager, dateManager);
        monthlyReviewManager = new MonthlyReviewManager(database);

        eventManager = new EventManager(database,
                contractManager,
                dateManager,
                segmentManager);

        contractFactory = new ContractFactory(contractManager);

        matchFactory = new MatchFactory(dateManager, staffManager);

        eventFactory = new EventFactory(
                eventManager,
                matchFactory,
                titleManager,
                workerManager,
                tagTeamManager,
                stableManager,
                relationshipManager,
                newsManager);

        promotionController = new PromotionController(
                contractFactory,
                eventFactory,
                contractManager,
                dateManager,
                titleManager,
                workerManager,
                newsManager,
                staffManager);

        dailyEventBooker = DailyEventBooker.builder()
                .eventManager(eventManager)
                .dateManager(dateManager)
                .workerManager(workerManager)
                .promotionManager(promotionManager)
                .promotionController(promotionController)
                .build();

        dailyContractUpdate = DailyContractUpdate.builder()
                .contractFactory(contractFactory)
                .contractManager(contractManager)
                .promotionManager(promotionManager)
                .dateManager(dateManager)
                .workerManager(workerManager)
                .newsManager(newsManager)
                .build();

        dailyTransactions = DailyTransactions.builder()
                .contractManager(contractManager)
                .dateManager(dateManager)
                .build();

        dailyRelationshipUpdate = DailyRelationshipUpdate.builder()
                .relationshipManager(relationshipManager)
                .contractManager(contractManager)
                .dateManager(dateManager)
                .newsManager(newsManager)
                .build();

        monthlyReviewController = MonthlyReviewController.builder()
                .monthlyReviewManager(monthlyReviewManager)
                .bankAccountManager(bankAccountManager)
                .build();

        nextDayController = NextDayController.builder()
                .dailyContractUpdate(dailyContractUpdate)
                .dailyEventBooker(dailyEventBooker)
                .dateManager(dateManager)
                .eventManager(eventManager)
                .relationshipManager(relationshipManager)
                .bankAccountManager(bankAccountManager)
                .injuryManager(injuryManager)
                .newsManager(newsManager)
                .contractManager(contractManager)
                .dailyTransactions(dailyTransactions)
                .dailyRelationshipUpdate(dailyRelationshipUpdate)
                .dailyContractUpdate(dailyContractUpdate)
                .promotionManager(promotionManager)
                .monthlyReviewController(monthlyReviewController)
                .build();

        if (randomGame) {
            RandomGameAssetGenerator randomGameAssetGenerator = new RandomGameAssetGenerator(
                    contractFactory,
                    dateManager,
                    promotionManager,
                    workerManager,
                    staffManager,
                    bankAccountManager,
                    contractManager);
            randomGameAssetGenerator.preparePromotions();
        }

        managers = List.of(
                bankAccountManager,
                broadcastTeamManager,
                contractManager,
                dateManager,
                entourageManager,
                eventManager,
                injuryManager,
                newsManager,
                promotionManager,
                relationshipManager,
                segmentManager,
                staffManager,
                workerManager,
                stableManager,
                rosterSplitManager,
                tagTeamManager,
                titleManager,
                monthlyReviewManager
        );

    }

    public void initializeGameData() {
        List<EventTemplate> generatedEventTemplates = promotionManager.getPromotions().stream()
                .filter(promotion -> eventManager.getEventTemplates(promotion).isEmpty())
                .flatMap(promotion -> EventFactory.generateMonthlyEventTemplates(promotion, dateManager.today()).stream())
                .collect(Collectors.toList());
        eventManager.createEventTemplates(generatedEventTemplates);

        List<Event> initialEvents = eventManager.getEventTemplates().stream()
                .flatMap(eventTemplate -> getInitialEventsForEventTemplate(eventTemplate, dateManager.today()).stream())
                .collect(Collectors.toList());

        List<EventTemplate> updatedBookedUntilDates = initialEvents.stream()
                .map(Event::getEventTemplate)
                .collect(Collectors.toList());

        eventManager.updateEventTemplates(updatedBookedUntilDates);
        eventManager.createEvents(initialEvents);
        newsManager.addWelcomeNewsItem(promotionManager.getPlayerPromotion());
    }

    public void loadGameDataFromDatabase() {
        managers.forEach(GameObjectManager::selectData);
    }

    //only called by MainApp
    public void nextDay() {
        long start = System.currentTimeMillis();

        contractManager.dailyUpdate(dateManager.today());

        for (Promotion promotion : promotionManager.getPromotions()) {
            injuryManager.dailyUpdate(dateManager.today(), promotion);
        }

        logger.log(Level.DEBUG, String.format("nextDay before controller %d", System.currentTimeMillis() - start));

        nextDayController.nextDay();

        dateManager.nextDay();

        logger.log(Level.DEBUG, String.format("nextDay end total time %d", System.currentTimeMillis() - start));
    }


}
