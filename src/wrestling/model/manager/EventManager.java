package wrestling.model.manager;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import wrestling.model.Contract;
import wrestling.model.Event;
import wrestling.model.EventName;
import wrestling.model.EventWorker;
import wrestling.model.Match;
import wrestling.model.MatchEvent;
import wrestling.model.Promotion;
import wrestling.model.Worker;
import wrestling.model.interfaces.Segment;
import wrestling.model.interfaces.iEvent;
import wrestling.model.modelView.EventView;
import wrestling.model.modelView.SegmentView;
import wrestling.model.utility.ModelUtils;

public class EventManager {

    private final List<Event> events;
    private final List<EventWorker> eventWorkers;
    private final List<MatchEvent> matchEvents;
    private final List<EventName> eventNames;
    private final List<EventView> eventViews;

    private final DateManager dateManager;
    private final SegmentManager matchManager;
    private final ContractManager contractManager;

    private final transient Logger logger = LogManager.getLogger(getClass());

    public EventManager(
            ContractManager contractManager,
            DateManager dateManager,
            SegmentManager matchManager) {
        events = new ArrayList<>();
        eventWorkers = new ArrayList<>();
        matchEvents = new ArrayList<>();
        eventNames = new ArrayList<>();
        eventViews = new ArrayList<>();
        this.matchManager = matchManager;
        this.contractManager = contractManager;
        this.dateManager = dateManager;
    }

    public void addEventNames(List<EventName> names) {
        eventNames.addAll(names);
    }

    public void addEvent(Event event) {
        if (!events.contains(event)) {
            events.add(event);
        }
    }

    public void addEventView(EventView eventView) {
        eventViews.add(eventView);
    }

    public void addMatchEvent(MatchEvent matchEvent) {
        matchEvents.add(matchEvent);
    }

    public void addEventWorker(EventWorker eventWorker) {
        eventWorkers.add(eventWorker);
    }

    public boolean hasEvent(Event event) {
        return events.contains(event);
    }

    public void cancelEvent(Event eventToCancel) {
        for (Iterator<MatchEvent> iter = matchEvents.listIterator(); iter.hasNext();) {
            MatchEvent matchEvent = iter.next();
            if (matchEvent.getEvent().equals(eventToCancel)) {
                iter.remove();
            }
        }
        for (Iterator<EventWorker> iter = eventWorkers.listIterator(); iter.hasNext();) {
            EventWorker eventWorker = iter.next();
            if (eventWorker.getEvent().equals(eventToCancel)) {
                iter.remove();
            }
        }
        for (Iterator<Event> iter = events.listIterator(); iter.hasNext();) {
            Event event = iter.next();
            if (event.equals(eventToCancel)) {
                iter.remove();
            }
        }
    }

    public EventName getEventName(Promotion promotion, Month month) {
        for (EventName eventName : eventNames) {
            if (eventName.getPromotion().equals(promotion)
                    && eventName.getMonth().equals(month)) {
                return eventName;
            }
        }
        return null;
    }

    public List<Event> getEvents(Promotion promotion) {
        List<Event> promotionEvents = new ArrayList();
        events.stream().filter((event) -> (event.getPromotion().equals(promotion))).forEach((event) -> {
            promotionEvents.add(event);
        });
        return promotionEvents;
    }

    public Event getNextEvent(Promotion promotion, LocalDate startDate) {
        Event event = null;
        int futureDaysToSearch = 180;
        LocalDate currentDate = startDate;
        for (int i = 0; i < futureDaysToSearch; i++) {
            event = getEventOnDate(promotion, currentDate);
            if (event != null) {
                break;
            }
            currentDate = LocalDate.from(currentDate).plusDays(1);
        }
        return event;
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

    public EventView getEventView(Event event) {
        for (EventView eventView : eventViews) {
            if (event.equals(eventView.getEvent())) {
                return eventView;
            }
        }
        return null;
    }

    public List<Event> getEventsOnDate(LocalDate date) {
        List<Event> eventsOnDate = new ArrayList<>();
        for (Event event : events) {
            if (event.getDate().equals(date)) {
                eventsOnDate.add(event);
            }
        }
        return eventsOnDate;
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
                allWorkers.addAll(matchManager.getWorkers(currentSegment));
            }
        }

        //this should take the list of workers generated above
        //and convert it to a set, removing duplicates
        Set<Worker> allWorkersSet = new LinkedHashSet<>(allWorkers);
        //convert the set back to a list with no duplicates
        allWorkers = new ArrayList<>(allWorkersSet);

        return allWorkers;
    }

    public List<Worker> allWorkers(Event event) {
        List allWorkers = new ArrayList<>();
        for (EventWorker eventWorker : eventWorkers) {
            if (eventWorker.getEvent().equals(event)) {
                allWorkers.add(eventWorker.getWorker());
            }
        }
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

    //dynamic current cost calculation to be called while the player is booking
    public int calculateCost(List<Segment> segments, Promotion promotion) {
        int currentCost = 0;

        for (Worker worker : allWorkers(segments)) {
            currentCost += contractManager.getContract(worker, promotion).getAppearanceCost();
        }
        return currentCost;
    }

    //gross profit for the event
    public int calculateGate(iEvent event) {

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

        return calculateAttendance(event) * ticketPrice;
    }

    //gross profit for the event
    public int calculateGate(List<Segment> segments, Promotion promotion) {

        int ticketPrice = 0;

        switch (promotion.getLevel()) {
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

        return calculateAttendance(segments, promotion) * ticketPrice;
    }

    public int calculateAttendance(iEvent event) {
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

            if (worker.getPopularity() > ModelUtils.maxPopularity(event.getPromotion()) - 10) {
                draws++;
            }
        }

        attendance += ModelUtils.randRange(event.getPromotion().getLevel(), event.getPromotion().getLevel() * 15) * draws;

        return attendance;
    }

    public int calculateAttendance(List<Segment> segments, Promotion promotion) {
        int attendance = 0;

        switch (promotion.getLevel()) {
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
        for (Worker worker : allWorkers(segments)) {

            if (worker.getPopularity() > ModelUtils.maxPopularity(promotion) - 10) {
                draws++;
            }
        }

        attendance += ModelUtils.randRange(promotion.getLevel(), promotion.getLevel() * 15) * draws;

        return attendance;
    }

    private String futureEventString(Event event) {
        StringBuilder sb = new StringBuilder();
        sb.append("Workers booked:");
        List<Worker> workers = allWorkers(event);
        for (Worker worker : workers) {
            sb.append("\n");
            sb.append(worker.getName());
        }
        return sb.toString();
    }

    public String generateSummaryString(Event event) {
        EventView eventView = getEventView(event);

        StringBuilder sb = new StringBuilder();

        if (event.getDate().isAfter(dateManager.today())) {
            return sb.append("This event is in the future\n").append(futureEventString(event)).toString();
        }

        if (event.getDate().equals(dateManager.today())) {
            return sb.append("This event is scheduled for later today\n").append(futureEventString(event)).toString();
        }

        for (SegmentView segmentView : eventView.getSegments()) {
            if (!segmentView.getWorkers().isEmpty()) {
                sb.append(matchManager.getSegmentString(segmentView));
                sb.append("\n");
                sb.append("Rating: ").append((segmentView.getSegment()).getRating());
            } else {
                logger.log(Level.ERROR, "Encountered empty segment when constructing event summary string");
            }

            sb.append("\n");
        }

        sb.append("\n");

        sb.append("Total cost: $").append(event.getCost());
        sb.append("\n");
        sb.append("Attendance: ").append(event.getAttendance());
        sb.append("\n");
        sb.append("Gross profit: $").append(event.getGate());

        return sb.toString();
    }

    //checks if a worker is booked at all on a given date
    public boolean isBooked(Worker worker, LocalDate date) {
        boolean isBooked = false;

        for (EventWorker eventWorker : eventWorkers) {
            if (eventWorker.getEvent().getDate().equals(date)
                    && eventWorker.getWorker().equals(worker)) {
                isBooked = true;
                break;
            }
        }

        return isBooked;
    }

    private EventWorker getBooking(Worker worker, LocalDate date) {
        EventWorker workerBooking = null;
        for (EventWorker eventWorker : eventWorkers) {
            if (eventWorker.getEvent().getDate().equals(date)
                    && eventWorker.getWorker().equals(worker)) {
                workerBooking = eventWorker;
                break;
            }
        }
        return workerBooking;
    }

    //checks if a worker is booked on a certain date
    //returns false if the booking is with the given promotion
    public boolean isAvailable(Worker worker, LocalDate date, Promotion promotion) {
        boolean isAvailable = true;
        EventWorker eventWorker = getBooking(worker, date);
        if (eventWorker != null && !eventWorker.getEvent().getPromotion().equals(promotion)) {
            isAvailable = false;
        }
        return isAvailable;
    }

    public List<Worker> getAvailableRoster(Promotion promotion, LocalDate date) {

        List<Worker> roster = new ArrayList<>();
        for (Contract contract : contractManager.getContracts(promotion)) {
            if (contract.isActive() && isAvailable(contract.getWorker(), date, promotion)) {
                roster.add(contract.getWorker());
            }

        }

        return roster;
    }

    public List<Worker> getUnavailableRoster(Promotion promotion, LocalDate date) {

        List<Worker> roster = new ArrayList<>();
        for (Contract contract : contractManager.getContracts(promotion)) {
            if (contract.isActive() && isAvailable(contract.getWorker(), date, promotion)) {
                roster.add(contract.getWorker());
            }

        }

        return roster;
    }

    public boolean canReschedule(Event event) {
        return event != null && event.getTelevision() == null;
    }

}
