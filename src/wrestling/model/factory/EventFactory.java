package wrestling.model.factory;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import wrestling.model.EventArchive;
import wrestling.model.Match;
import wrestling.model.MatchRecord;
import wrestling.model.Promotion;
import wrestling.model.Segment;
import wrestling.model.Worker;
import wrestling.model.utility.UtilityFunctions;

/**
 * an Event has a date, promotion, a list of segments (matches etc.) this class
 * figures out attendance, gate processes contracts
 *
 */
public final class EventFactory {

    private EventFactory() {
        throw new IllegalAccessError("Utility class");
    }

    public static void createEvent(final List<Segment> segments, LocalDate date, Promotion promotion) {

        TempEvent event = new TempEvent(segments, date, promotion);

        processSegments(event);

        promotion.gainPopularity();
        promotion.bankAccount().addFunds(gate(event), 'e', date);

        //this is all that will remain of the event
        EventArchive eventArchive = new EventArchive(
                promotion.getName(),
                calculateCost(event),
                gate(event),
                attendance(event),
                date,
                generateSummaryString(event));

        promotion.archiveEvent(eventArchive);

        processContracts(event);

    }

    //class to temporarily hold event info to make things cleaner
    private static class TempEvent {

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

    private static String generateSummaryString(TempEvent event) {
        StringBuilder bld = new StringBuilder();

        for (Segment segment : event.getSegments()) {

            if (segment.isComplete()) {
                bld.append(segment.toString());
                if (segment instanceof Match) {

                    bld.append("\n");
                    bld.append("Rating: " + ((Match) segment).segmentRating());

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
        bld.append("Roster size: ").append(event.getPromotion().getFullRoster().size());
        bld.append("\n");
        bld.append("Promotion Level: ").append(event.getPromotion().getLevel()).append(" (").append(event.getPromotion().getPopulatirty()).append(")");

        return bld.toString();
    }

    private static int attendance(TempEvent event) {
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

            if (worker.getPopularity() > event.getPromotion().maxPopularity() - 10) {
                draws++;
            }
        }

        attendance += UtilityFunctions.randRange(event.getPromotion().getLevel(), event.getPromotion().getLevel() * 15) * draws;

        return attendance;
    }

    /*
    runs through all contracts associated with the event
    and takes money from the promotion accordingly
    also notifies contracts of appearances
     */
    private static void processContracts(TempEvent event) {

        for (Worker worker : allWorkers(event.getSegments())) {

            worker.getContract(event.getPromotion()).appearance(event.getDate());

        }

    }

    private static void processSegments(TempEvent event) {
        for (Segment segment : event.getSegments()) {
            if (segment.isComplete()) {
                segment.processSegment(event.getDate());

                if (segment.getClass().equals(Match.class
                )) {
                    for (Worker worker : segment.allWorkers()) {
                        worker.addMatchRecord(new MatchRecord(segment.toString(), event.getDate()));
                    }
                }
            }

        }

    }

    //this will return a list of all workers currently booked
    //without any duplicates
    //so if a worker is in two different segments he is only on the list
    //one time. useful for cost calculation so we don't pay people
    //twice for the same show
    private static List<Worker> allWorkers(List<Segment> segments) {

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
    private static int calculateCost(TempEvent event) {

        int currentCost = 0;

        for (Worker currentWorker : allWorkers(event.getSegments())) {

            currentCost += currentWorker.getContract(event.getPromotion()).getAppearanceCost();
        }

        return currentCost;
    }

    //gross profit for the event
    private static int gate(TempEvent event) {

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
}
