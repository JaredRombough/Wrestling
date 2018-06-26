package wrestling.model.factory;

import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import wrestling.model.Event;
import wrestling.model.EventTemplate;
import wrestling.model.EventWorker;
import wrestling.model.Match;
import wrestling.model.MatchEvent;
import wrestling.model.Promotion;
import wrestling.model.Worker;
import wrestling.model.controller.PromotionController;
import wrestling.model.interfaces.Segment;
import wrestling.model.interfaces.iEvent;
import wrestling.model.manager.ContractManager;
import wrestling.model.manager.EventManager;
import wrestling.model.manager.PromotionManager;
import wrestling.model.manager.SegmentManager;
import wrestling.model.manager.TitleManager;
import wrestling.model.manager.WorkerManager;
import wrestling.model.modelView.EventView;
import wrestling.model.modelView.SegmentView;
import wrestling.model.modelView.TitleView;
import wrestling.model.segmentEnum.EventVenueSize;
import wrestling.model.segmentEnum.TeamType;

/**
 * an Event has a date, promotion, a list of segments (matches etc.) this class
 * figures out attendance, gate processes contracts
 *
 */
public class EventFactory {

    private final ContractManager contractManager;
    private final EventManager eventManager;
    private final TitleManager titleManager;
    private final WorkerManager workerManager;
    private final SegmentManager matchManager;
    private final MatchFactory matchFactory;
    private final PromotionManager promotionManager;

    public EventFactory(
            ContractManager contractManager,
            EventManager eventManager,
            MatchFactory matchFactory,
            SegmentManager matchManager,
            PromotionManager promotionManager,
            TitleManager titleManager,
            WorkerManager workerManager) {
        this.contractManager = contractManager;
        this.eventManager = eventManager;
        this.matchFactory = matchFactory;
        this.matchManager = matchManager;
        this.promotionManager = promotionManager;
        this.titleManager = titleManager;
        this.workerManager = workerManager;
    }

    public void processEventView(EventView eventView, boolean processSegments,
            PromotionController promotionController) {

        if (processSegments) {
            for (SegmentView segmentView : eventView.getSegmentViews()) {
                segmentView.setSegment(processSegmentView(eventView, segmentView));
            }
        }

        Event event = eventView.getEvent();
        List<Segment> segments = segmentsFromSegmentViews(eventView.getSegmentViews());

        setEventStats(event, segments);

        promotionManager.getBankAccount(event.getPromotion()).addFunds(
                eventManager.calculateGate(event), 'e', eventView.getEvent().getDate());

        for (Worker worker : eventManager.allWorkers(segments)) {
            EventWorker eventWorker = new EventWorker(event, worker);
            eventManager.addEventWorker(eventWorker);
        }
        processContracts(event, segments);

        eventManager.addEventView(eventView);

        promotionController.updateEventTemplate(eventView);
    }

    public void createMonthlyEvents(Promotion promotion) {

        Month month = Month.JANUARY;
        for (int i = 0; i < 12; i++) {

            EventTemplate template = new EventTemplate();
            template.setPromotion(promotion);
            template.setMonth(month);
            template.setName(promotion.getShortName() + " "
                    + month.toString().substring(0, 1).toUpperCase()
                    + month.toString().toLowerCase().substring(1)
                    + " Event");
            month = month.plus(1);
            if (promotion.getLevel() > 4) {
                template.setEventVenueSize(EventVenueSize.LARGE);
            } else if (promotion.getLevel() <= 4
                    && promotion.getLevel() >= 3) {
                template.setEventVenueSize(EventVenueSize.MEDIUM);
            } else {
                template.setEventVenueSize(EventVenueSize.SMALL);
            }
            eventManager.addEventTemplate(template);

        }
    }

    private void setEventStats(Event event, List<Segment> segments) {
        event.setCost(eventManager.calculateCost(segments, event.getPromotion()));
        event.setGate(eventManager.calculateGate(segments, event.getPromotion()));
        event.setAttendance(eventManager.calculateAttendance(segments, event.getPromotion()));
    }

    private List<Segment> segmentsFromSegmentViews(List<SegmentView> segmentViews) {
        List<Segment> segments = new ArrayList<>();
        for (SegmentView segmentView : segmentViews) {
            segments.add(segmentView.getSegment());
        }
        return segments;
    }

    private void processContracts(iEvent event, List<Segment> segments) {
        eventManager.allWorkers(segments).stream().map((worker) -> contractManager.getContract(worker, event.getPromotion())).forEach((contract) -> {
            contractManager.appearance(event.getDate(), contract);
        });
    }

    public Segment processSegmentView(EventView eventView, SegmentView segmentView) {
        segmentView.setEventView(eventView);
        Segment segment = matchFactory.saveSegment(segmentView);
        if (segment instanceof Match) {
            eventManager.addMatchEvent(new MatchEvent((Match) segment, eventView.getEvent()));
            List<Worker> winners = matchManager.getWinners((Match) segment);
            winners.stream().forEach((w) -> {
                workerManager.gainPopularity(w);
            });
            if (!segmentView.getTitleViews().isEmpty() && !winners.isEmpty()) {
                processTitleChanges(segmentView, winners);
            }
        }
        return segment;
    }

    private void processTitleChanges(SegmentView segmentView, List<Worker> winners) {
        for (TitleView titleView : segmentView.getTitleViews()) {
            if (!winners.equals(titleView.getChampions())) {
                titleManager.titleChange(
                        titleView.getTitle(),
                        winners);
            }
        }
    }

}
