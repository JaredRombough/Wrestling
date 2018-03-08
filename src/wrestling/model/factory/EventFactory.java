package wrestling.model.factory;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import wrestling.model.Event;
import wrestling.model.EventWorker;
import wrestling.model.Match;
import wrestling.model.MatchEvent;
import wrestling.model.Promotion;
import wrestling.model.Worker;
import wrestling.model.interfaces.Segment;
import wrestling.model.interfaces.iEvent;
import wrestling.model.manager.ContractManager;
import wrestling.model.manager.EventManager;
import wrestling.model.manager.MatchManager;
import wrestling.model.manager.PromotionManager;
import wrestling.model.manager.TitleManager;
import wrestling.model.manager.WorkerManager;
import wrestling.model.modelView.EventView;
import wrestling.model.modelView.SegmentView;

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
    private final MatchManager matchManager;
    private final MatchFactory matchFactory;
    private final PromotionManager promotionManager;

    public EventFactory(
            ContractManager contractManager,
            EventManager eventManager,
            MatchFactory matchFactory,
            MatchManager matchManager,
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

    public void processEventView(EventView eventView, LocalDate date, boolean processSegments) {

        if (processSegments) {
            for (SegmentView segmentView : eventView.getSegments()) {
                segmentView.setSegment(processSegmentView(eventView.getEvent(), segmentView));
            }
        }

        Event event = eventView.getEvent();
        List<Segment> segments = segmentViewsToSegments(eventView.getSegments());

        setEventStats(event, segments);

        promotionManager.getBankAccount(event.getPromotion()).addFunds(eventManager.calculateGate(event), 'e', date);

        for (Worker worker : eventManager.allWorkers(segments)) {
            EventWorker eventWorker = new EventWorker(event, worker);
            eventManager.addEventWorker(eventWorker);
        }
        processContracts(event, segments);
    }

    public Event createFutureEvent(Promotion promotion, LocalDate date) {
        Event event = new Event(promotion, date);
        eventManager.addEvent(event);
        return event;
    }

    private void setEventStats(Event event, List<Segment> segments) {
        event.setCost(eventManager.calculateCost(segments, event.getPromotion()));
        event.setGate(eventManager.calculateGate(segments, event.getPromotion()));
        event.setAttendance(eventManager.calculateAttendance(segments, event.getPromotion()));
    }

    private List<Segment> segmentViewsToSegments(List<SegmentView> tempSegments) {
        List<Segment> segments = new ArrayList<>();
        for (SegmentView tempSegment : tempSegments) {
            segments.add(EventFactory.this.processSegmentView(tempSegment));
        }
        return segments;
    }

    private Segment processSegmentView(SegmentView segmentView) {
        return matchFactory.processMatch(segmentView.getTeams(), segmentView.getRules(), segmentView.getFinish());

    }

    private void processContracts(iEvent event, List<Segment> segments) {
        eventManager.allWorkers(segments).stream().map((worker) -> contractManager.getContract(worker, event.getPromotion())).forEach((contract) -> {
            contractManager.appearance(event.getDate(), contract);
        });
    }

    public Segment processSegmentView(Event event, SegmentView segmentView) {
        Segment segment = EventFactory.this.processSegmentView(segmentView);
        if (segment instanceof Match) {
            eventManager.addMatchEvent(new MatchEvent((Match) segment, event));
            matchManager.getWinners((Match) segment).stream().forEach((w) -> {
                workerManager.gainPopularity(w);
            });
        }
        return segment;
    }

}
