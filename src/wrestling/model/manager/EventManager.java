package wrestling.model.manager;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import wrestling.model.Match;
import wrestling.model.Promotion;
import wrestling.model.Event;
import wrestling.model.EventWorker;
import wrestling.model.MatchEvent;
import wrestling.model.Worker;
import wrestling.model.interfaces.Segment;
import wrestling.model.interfaces.iEvent;
import wrestling.model.utility.ModelUtilityFunctions;

public class EventManager {

    private final List<Event> events;
    private final List<EventWorker> eventWorkers;
    private final List<MatchEvent> matchEvents;

    private final DateManager dateManager;
    private final MatchManager matchManager;
    private final ContractManager contractManager;

    private final transient Logger logger = LogManager.getLogger(getClass());

    public EventManager(
            ContractManager contractManager,
            DateManager dateManager,
            MatchManager matchManager) {
        events = new ArrayList<>();
        eventWorkers = new ArrayList<>();
        matchEvents = new ArrayList<>();
        this.matchManager = matchManager;
        this.contractManager = contractManager;
        this.dateManager = dateManager;
    }

    public void addEvent(Event event) {
        events.add(event);
    }

    public void addMatchEvent(MatchEvent matchEvent) {
        matchEvents.add(matchEvent);
    }

    public void addEventWorker(EventWorker eventWorker) {
        eventWorkers.add(eventWorker);
    }

    public List<Event> getEvents(Promotion promotion) {
        List<Event> promotionEvents = new ArrayList();
        events.stream().filter((event) -> (event.getPromotion().equals(promotion))).forEach((event) -> {
            promotionEvents.add(event);
        });
        return promotionEvents;
    }

    public Event getEventOnDate(Promotion promotion, LocalDate date) {
        for (Event event : events) {
            if (event.getDate().equals(date)
                    && event.getPromotion().equals(promotion)) {
                return event;
            }
        }
        return null;
    }

    public int eventsAfterDate(Promotion promotion, LocalDate date) {
        int futureEvents = 0;
        futureEvents = events.stream().filter((Event event) -> {
            return event.getPromotion().equals(promotion)
                    && event.getDate().isAfter(date);
        }).map((_item) -> 1).reduce(futureEvents, Integer::sum);
        return futureEvents;
    }

    public List<Segment> getMatches(iEvent event) {
        List<Segment> matches = new ArrayList<>();
        matchEvents.stream().filter((matchEvent) -> (matchEvent.getEvent().equals(event))).forEach((matchEvent) -> {
            matches.add(matchEvent.getMatch());
        });
        return matches;
    }

    //this will return a list of all workers currently booked
    //without any duplicates
    //so if a worker is in two different segments he is only on the list
    //one time. useful for cost calculation so we don't pay people
    //twice for the same show
    public List<Worker> allWorkers(List<Segment> segments) {

        List allWorkers = new ArrayList<>();
        for (Segment currentSegment : segments) {
            if (currentSegment instanceof Match) {
                allWorkers.addAll(matchManager.getWorkers((Match) currentSegment));
            }
        }

        //this should take the list of workers generated above
        //and convert it to a set, removing duplicates
        Set<Worker> allWorkersSet = new LinkedHashSet<>(allWorkers);
        //convert the set back to a list with no duplicates
        allWorkers = new ArrayList<>(allWorkersSet);

        return allWorkers;
    }

    //dynamic current cost calculation to be called while the player is booking
    public int calculateCost(iEvent event) {
        int currentCost = 0;

        for (Worker worker : allWorkers(getMatches(event))) {
            currentCost += contractManager.getContract(worker, event.getPromotion()).getAppearanceCost();
        }
        return currentCost;
    }

    //gross profit for the event
    public int gate(iEvent event) {

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

    public int attendance(iEvent event) {
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
        for (Worker worker : allWorkers(getMatches(event))) {

            if (worker.getPopularity() > ModelUtilityFunctions.maxPopularity(event.getPromotion()) - 10) {
                draws++;
            }
        }

        attendance += ModelUtilityFunctions.randRange(event.getPromotion().getLevel(), event.getPromotion().getLevel() * 15) * draws;

        return attendance;
    }

    public String generateSummaryString(Event event) {
        StringBuilder bld = new StringBuilder();

        if (event.getDate().isAfter(dateManager.today())) {
            bld.append("This event is in the future");
        } else if (event.getDate().equals(dateManager.today())) {
            bld.append("This event is scheduled for later today");
        }

        for (Segment segment : getMatches(event)) {
            if (segment instanceof Match) {
                if (!matchManager.getWorkers((Match) segment).isEmpty()) {
                    bld.append(matchManager.getMatchString((Match) segment));
                    bld.append("\n");
                    bld.append("Rating: ").append(((Match) segment).getRating());
                } else {
                    logger.log(Level.ERROR, "Encountered empty segment when constructing event summary string");
                }
            } else {
                logger.log(Level.ERROR, "Encountered invalid segment when constructing event summary string");
            }
            bld.append("\n");
        }

        bld.append("\n");

        bld.append("Total cost: $").append(event.getCost());
        bld.append("\n");
        bld.append("Attendance: ").append(event.getAttendance());
        bld.append("\n");
        bld.append("Gross profit: $").append(event.getGate());

        return bld.toString();
    }

    public void addInitialEvents(List<Promotion> promotions, Promotion playerPromotion) {
        for (Promotion promotion : promotions) {
            if (!promotion.equals(playerPromotion)) {
                addEvent(new Event(promotion, (dateManager.today()).plusDays(ModelUtilityFunctions.randRange(2, 7))));
            }
        }
    }
}
