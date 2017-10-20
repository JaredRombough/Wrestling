package wrestling.model.factory;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import wrestling.model.Contract;
import wrestling.model.dirt.EventArchive;
import wrestling.model.dirt.EventType;
import wrestling.model.controller.GameController;
import wrestling.model.Match;
import wrestling.model.Promotion;
import wrestling.model.Segment;
import wrestling.model.Television;
import wrestling.model.Title;
import wrestling.model.dirt.SegmentRecord;
import wrestling.model.Worker;
import wrestling.model.utility.ModelUtilityFunctions;

/**
 * an Event has a date, promotion, a list of segments (matches etc.) this class
 * figures out attendance, gate processes contracts
 *
 */
public class EventFactory {

    private EventArchive createEvent(final List<Segment> segments, LocalDate date, Promotion promotion, EventType eventType) {

        TempEvent event = new TempEvent(segments, date, promotion);

        //this is all that will remain of the event
        EventArchive eventArchive = new EventArchive(
                allWorkers(segments),
                promotion,
                eventType,
                calculateCost(event),
                gate(event),
                attendance(event));

        gameController.getDirtSheet().newDirt(eventArchive);

        processSegments(event, eventArchive);

        promotion.getController().gainPopularity();
        promotion.bankAccount().addFunds(gate(event), 'e', date);

        processContracts(event);

        return eventArchive;

    }

    public void createEvent(final List<Segment> segments, LocalDate date, Promotion promotion) {
        createEvent(segments, date, promotion, EventType.LIVEEVENT);
    }

    public void createEvent(final List<Segment> segments, LocalDate date, Promotion promotion, Television television) {
        createEvent(segments, date, promotion, EventType.TELEVISION).setTelevision(television);
    }

    private String generateSummaryString(TempEvent event) {
        StringBuilder bld = new StringBuilder();

        for (Segment segment : event.getSegments()) {

            if (segment.isComplete()) {
                bld.append(segment.toString());
                if (segment instanceof Match) {

                    bld.append("\n");
                    bld.append("Rating: ").append(((Match) segment).segmentRating());

                }
                bld.append("\n");

            }
        }

        bld.append("\n");

        bld.append("Total cost: $").append(calculateCost(event));
        bld.append("\n");
        bld.append("Attendance: ").append(attendance(event));
        bld.append("\n");
        bld.append("Gross profit: $").append(gate(event));
        bld.append("\n");
        bld.append("Roster size: ").append(gameController.getContractManager().getFullRoster(event.getPromotion()).size());
        bld.append("\n");
        bld.append("Promotion Level: ").append(event.getPromotion().getLevel()).append(" (").append(event.getPromotion().getPopulatirty()).append(")");

        return bld.toString();
    }

    private int attendance(TempEvent event) {
        int attendance = 0;

        switch (event.getPromotion().getLevel()) {
            case 1:
                attendance += 20;
                break;
            case 2:
                attendance += 50;
                break;
            case 3:
                attendance += 100;
                break;
            case 4:
                attendance += 250;
                break;
            case 5:
                attendance += 4000;
                break;
            default:
                break;
        }

        //how many workers are draws?
        int draws = 0;
        for (Worker worker : allWorkers(event.getSegments())) {

            if (worker.getPopularity() > ModelUtilityFunctions.maxPopularity(event.getPromotion()) - 10) {
                draws++;
            }
        }

        attendance += ModelUtilityFunctions.randRange(event.getPromotion().getLevel(), event.getPromotion().getLevel() * 15) * draws;

        return attendance;
    }

    /*
    runs through all contracts associated with the event
    and takes money from the promotion accordingly
    also notifies contracts of appearances
     */
    private void processContracts(TempEvent event) {

        for (Worker worker : allWorkers(event.getSegments())) {

            Contract c = worker.getController().getContract(event.getPromotion());
            if (!gameController.getContractManager().appearance(event.getDate(), c)) {
                gameController.getContractFactory().reportExpiration(c);
            }

        }

    }

    private void processSegments(TempEvent event, EventArchive ea) {
        for (Segment segment : event.getSegments()) {

            if (segment.isComplete()) {

                if (segment instanceof Match) {
                    for (Worker w : ((Match) segment).getWinner()) {
                        w.getController().gainPopularity();
                    }
                }
                gameController.getDirtSheet().newDirt(new SegmentRecord(processSegment(segment),
                        segment.allWorkers(),
                        event.getPromotion(),
                        ea));

            }

        }

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
        Title title = match.getTitle();
        List<Worker> winner = match.getWinner();

        if (title != null) {

            if (title.isVacant()) {

                gameController.getTitleFactory().awardTitle(title, winner, gameController.date());
                sb.append(ModelUtilityFunctions.slashNames(winner))
                        .append(winner.size() > 1 ? " win the vacant  " : " wins the vacant  ")
                        .append(title.getName()).append(" title");
            } else {
                for (Worker worker : title.getWorkers()) {
                    if (!winner.contains(worker)) {
                        sb.append(ModelUtilityFunctions.slashNames(winner))
                                .append(winner.size() > 1 ? " defeat " : " defeats ")
                                .append(ModelUtilityFunctions.slashNames(title.getWorkers())).append(" for the ")
                                .append(title.getName()).append(" title");
                        gameController.getTitleFactory().titleChange(title, winner, gameController.date());

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

        winnerPop = winnerPop / winner.size();

        for (List<Worker> team : match.getTeams()) {

            if (!team.equals(winner)) {
                int teamPop = 0;

                for (Worker worker : team) {
                    teamPop += worker.getPopularity();
                }

                teamPop = teamPop / winner.size();

                if (teamPop > winnerPop) {
                    for (Worker worker : winner) {
                        worker.getController().gainPopularity();
                    }

                    for (Worker worker : team) {
                        if (ModelUtilityFunctions.randRange(1, 3) == 1) {
                            worker.getController().losePopularity();
                        }

                    }
                } else {
                    for (Worker worker : winner) {
                        if (ModelUtilityFunctions.randRange(1, 3) == 1) {
                            worker.getController().gainPopularity();
                        }
                    }
                }

            }
        }

        return sb.toString().isEmpty() ? toString().replace("\n", " ") : sb.toString();
    }

    //this will return a list of all workers currently booked
    //without any duplicates
    //so if a worker is in two different segments he is only on the list
    //one time. useful for cost calculation so we don't pay people
    //twice for the same show
    private List<Worker> allWorkers(List<Segment> segments) {

        List allWorkers = new ArrayList<>();
        for (Segment currentSegment : segments) {
            allWorkers.addAll(currentSegment.allWorkers());

        }

        //this should take the list of workers generated above
        //and convert it to a set, removing duplicates
        Set<Worker> allWorkersSet = new LinkedHashSet<>(allWorkers);
        //convert the set back to a list with no duplicates
        allWorkers = new ArrayList<Worker>(allWorkersSet);

        return allWorkers;
    }

    //dynamic current cost calculation to be called while the player is booking
    private int calculateCost(TempEvent event) {

        int currentCost = 0;

        for (Worker worker : allWorkers(event.getSegments())) {

            currentCost += worker.getController().getContract(event.getPromotion()).getAppearanceCost();
        }

        return currentCost;
    }

    //gross profit for the event
    private int gate(TempEvent event) {

        int ticketPrice = 0;

        switch (event.getPromotion().getLevel()) {
            case 1:
                ticketPrice += 5;
                break;
            case 2:
                ticketPrice += 10;
                break;
            case 3:
                ticketPrice += 15;
                break;
            case 4:
                ticketPrice += 20;
                break;
            case 5:
                ticketPrice += 35;
                break;
            default:
                break;
        }

        return attendance(event) * ticketPrice;
    }

    private final GameController gameController;

    public EventFactory(GameController gc) {
        this.gameController = gc;
    }

    //class to temporarily hold event info to make things cleaner
    private class TempEvent {

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
        public LocalDate getDate() {
            return date;
        }

        /**
         * @return the promotion
         */
        public Promotion getPromotion() {
            return promotion;
        }
    }
}
