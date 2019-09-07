package openwrestling.model.controller;

import lombok.Getter;
import openwrestling.manager.PromotionManager;
import openwrestling.manager.RosterSplitManager;
import openwrestling.manager.StableManager;
import openwrestling.manager.WorkerManager;
import openwrestling.model.EventTemplate;
import openwrestling.model.factory.ContractFactory;
import openwrestling.model.factory.EventFactory;
import openwrestling.model.factory.MatchFactory;
import openwrestling.model.factory.PromotionFactory;
import openwrestling.model.factory.TitleFactory;
import openwrestling.model.gameObjects.Promotion;
import openwrestling.model.manager.BankAccountManager;
import openwrestling.manager.ContractManager;
import openwrestling.model.manager.DateManager;
import openwrestling.model.manager.EventManager;
import openwrestling.model.manager.InjuryManager;
import openwrestling.model.manager.NewsManager;
import openwrestling.model.manager.RelationshipManager;
import openwrestling.model.manager.SegmentManager;
import openwrestling.model.manager.StaffManager;
import openwrestling.model.manager.TagTeamManager;
import openwrestling.model.manager.TitleManager;
import openwrestling.model.segmentEnum.EventFrequency;

import java.io.IOException;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.YearMonth;

@Getter
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
    private final BankAccountManager bankAccountManager;
    private final RosterSplitManager rosterSplitManager;

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
        bankAccountManager = new BankAccountManager();
        rosterSplitManager = new RosterSplitManager();

        contractManager = new ContractManager(promotionManager,
                titleManager,
                newsManager,
                relationshipManager,
                bankAccountManager);

        workerManager = new WorkerManager(contractManager);
        tagTeamManager = new TagTeamManager(contractManager, workerManager);
        segmentManager = new SegmentManager(dateManager, tagTeamManager, stableManager);
        injuryManager = new InjuryManager(newsManager, workerManager);
        contractFactory = new ContractFactory(contractManager);

        eventManager = new EventManager(
                contractManager,
                dateManager,
                segmentManager);

        titleFactory = new TitleFactory(titleManager);
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
                bankAccountManager);

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
            if (promotion.getEventTemplates().isEmpty() && !promotion.equals(promotionManager.getPlayerPromotion())) {
                eventFactory.createMonthlyEvents(promotion);
            }
        }
        initialBookEventTemplates(getDateManager().today());
    }

    //only called by MainApp
    public void nextDay() {

        contractManager.dailyUpdate(dateManager.today());

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

        if (dateManager.today().getDayOfMonth() == 1) {
            bookEventTemplatesFuture(dateManager.today().minusMonths(1).getMonth().getValue());
        }
        dateManager.nextDay();
    }

    public void bookEventTemplatesFuture(int month) {
        YearMonth thisMonthNextYear = YearMonth.of(dateManager.today().plusYears(1).getYear(), month);
        for (EventTemplate eventTemplate : eventManager.getActiveEventTemplatesFuture(thisMonthNextYear)) {
            if (eventTemplate.getMonth() == month) {
                promotionController.bookEventTemplate(eventTemplate,
                        thisMonthNextYear);
            }

        }
    }

    public void initialBookEventTemplates(LocalDate startDate) {
        YearMonth yearMonth = YearMonth.of(startDate.getYear(), startDate.getMonth());
        for (int i = 0; i < 12; i++) {
            for (EventTemplate eventTemplate : eventManager.getEventTemplates()) {
                if (eventTemplate.getMonth() == yearMonth.getMonth().getValue()
                        || eventTemplate.getEventFrequency().equals(EventFrequency.WEEKLY)) {
                    promotionController.bookEventTemplate(eventTemplate, yearMonth);
                }
            }
            yearMonth = yearMonth.plusMonths(1);
        }
    }

}
