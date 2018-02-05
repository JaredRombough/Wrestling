package wrestling.model.factory;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import wrestling.model.Event;
import wrestling.model.EventType;
import wrestling.model.EventWorker;
import wrestling.model.Match;
import wrestling.model.MatchEvent;
import wrestling.model.Promotion;
import wrestling.model.Title;
import wrestling.model.Worker;
import wrestling.model.interfaces.Segment;
import wrestling.model.interfaces.iEvent;
import wrestling.model.manager.ContractManager;
import wrestling.model.manager.EventManager;
import wrestling.model.manager.MatchManager;
import wrestling.model.manager.PromotionManager;
import wrestling.model.manager.TitleManager;
import wrestling.model.manager.WorkerManager;
import wrestling.model.modelView.SegmentView;
import wrestling.model.utility.ModelUtilityFunctions;

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

    private void createEvent(final List<Segment> segments, LocalDate date, Promotion promotion, EventType eventType) {

        TempEvent tempEvent = new TempEvent(segments, date, promotion);

        Event event = new Event(
                promotion,
                date,
                eventType,
                eventManager.calculateCost(tempEvent),
                eventManager.gate(tempEvent),
                eventManager.attendance(tempEvent));

        processEvent(event, segments, date, promotion);
    }

    private void createEvent(Event event, final List<Segment> segments, LocalDate date, Promotion promotion, EventType eventType) {

        TempEvent tempEvent = new TempEvent(segments, date, promotion);

        event.setCost(eventManager.calculateCost(tempEvent));
        event.setGate(eventManager.gate(tempEvent));
        event.setAttendance(eventManager.attendance(tempEvent));

        processEvent(event, segments, date, promotion);
    }

    private void processEvent(Event event, final List<Segment> segments, LocalDate date, Promotion promotion) {
        processSegments(event, segments);
        promotionManager.getBankAccount(promotion).addFunds(eventManager.gate(event), 'e', date);
        eventManager.addEvent(event);
        for (Worker worker : eventManager.allWorkers(segments)) {
            EventWorker eventWorker = new EventWorker(event, worker);
            eventManager.addEventWorker(eventWorker);
        }
        processContracts(event, segments);
    }

    public void createEventFromTemp(Event event, final List<SegmentView> segments, LocalDate date, Promotion promotion) {
        createEvent(event, convertTempToSegment(segments), date, promotion, EventType.LIVEEVENT);
    }

    public void createEvent(Event event, final List<Segment> segments, LocalDate date, Promotion promotion) {
        createEvent(event, segments, date, promotion, EventType.LIVEEVENT);
    }

    private List<Segment> convertTempToSegment(List<SegmentView> tempSegments) {
        List<Segment> segments = new ArrayList<>();
        for (SegmentView tempSegment : tempSegments) {
            segments.add(matchFactory.CreateMatch(tempSegment.getTeams(), tempSegment.getRules(), tempSegment.getFinish()));
        }
        return segments;
    }

    /*
    runs through all contracts associated with the event
    and takes money from the promotion accordingly
    also notifies contracts of appearances
     */
    private void processContracts(iEvent event, List<Segment> segments) {
        eventManager.allWorkers(segments).stream().map((worker) -> contractManager.getContract(worker, event.getPromotion())).forEach((contract) -> {
            contractManager.appearance(event.getDate(), contract);
        });
    }

    private void processSegments(Event event, List<Segment> segments) {
        segments.stream().filter((segment) -> (segment instanceof Match)).forEach(((segment) -> {
            eventManager.addMatchEvent(new MatchEvent((Match) segment, event));
            matchManager.getWinners((Match) segment).stream().forEach((w) -> {
                workerManager.gainPopularity(w);
            });
        }));
    }

    private String processSegment(Segment segment) {

        String string = "";

        if (segment instanceof Match) {
            string = processMatch((Match) segment);
        }

        return string;

    }

    private String processMatch(Match match) {

        StringBuilder sb = new StringBuilder();
        Title title = matchManager.getTitle(match);
        List<Worker> winner = matchManager.getWinners(match);
        List<Worker> champs = titleManager.getCurrentChampionWorkers(title);

        if (title != null) {

            if (champs.isEmpty()) {

                titleManager.awardTitle(title, winner);
                sb.append(ModelUtilityFunctions.slashNames(winner))
                        .append(winner.size() > 1 ? " win the vacant  " : " wins the vacant  ")
                        .append(title.getName()).append(" title");
            } else {
                for (Worker worker : champs) {
                    if (!winner.contains(worker)) {
                        sb.append(ModelUtilityFunctions.slashNames(winner))
                                .append(winner.size() > 1 ? " defeat " : " defeats ")
                                .append(ModelUtilityFunctions.slashNames(champs)).append(" for the ")
                                .append(title.getName()).append(" title");
                        titleManager.titleChange(title, winner);

                        break;
                    }

                    sb.append(ModelUtilityFunctions.slashNames(winner)).append(" defends the  ").append(title.getName()).append(" title");
                }
            }
        }
        int winnerPop = 0;

        //calculate the average popularity of the winning team
        //but should it be max popularity?
        for (Worker worker : winner) {
            winnerPop += worker.getPopularity();
        }

        winnerPop /= winner.size();

        for (List<Worker> team : matchManager.getTeams(match)) {

            if (!team.equals(winner)) {
                int teamPop = 0;

                for (Worker worker : team) {
                    teamPop += worker.getPopularity();
                }

                teamPop /= winner.size();

                if (teamPop > winnerPop) {
                    for (Worker worker : winner) {
                        workerManager.gainPopularity(worker);
                    }

                    for (Worker worker : team) {
                        if (ModelUtilityFunctions.randRange(1, 3) == 1) {
                            workerManager.losePopularity(worker);
                        }

                    }
                } else {
                    for (Worker worker : winner) {
                        if (ModelUtilityFunctions.randRange(1, 3) == 1) {
                            workerManager.gainPopularity(worker);
                        }
                    }
                }

            }
        }

        return sb.toString().isEmpty() ? match.toString().replace("\n", " ") : sb.toString();
    }

    //class to temporarily hold event info to make things cleaner
    private class TempEvent implements iEvent {

        private final List<Segment> segments;
        private final LocalDate date;
        private final Promotion promotion;

        public TempEvent(List<Segment> segments, LocalDate date, Promotion promotion) {
            this.segments = new ArrayList<>(segments);
            this.date = date;
            this.promotion = promotion;
        }

        /**
         * @return the segments
         */
        public List<Segment> getSegments() {
            return segments;
        }

        /**
         * @return the date
         */
        @Override
        public LocalDate getDate() {
            return date;
        }

        /**
         * @return the promotion
         */
        @Override
        public Promotion getPromotion() {
            return promotion;
        }
    }
}
