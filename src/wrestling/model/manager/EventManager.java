package wrestling.model.manager;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import static java.time.temporal.TemporalAdjusters.firstInMonth;
import static java.time.temporal.TemporalAdjusters.lastDayOfMonth;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang3.RandomUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import wrestling.model.Contract;
import wrestling.model.Event;
import wrestling.model.EventTemplate;
import wrestling.model.EventWorker;
import wrestling.model.Match;
import wrestling.model.MatchEvent;
import wrestling.model.interfaces.Segment;
import wrestling.model.interfaces.iEvent;
import wrestling.model.modelView.EventView;
import wrestling.model.modelView.PromotionView;
import wrestling.model.modelView.SegmentView;
import wrestling.model.modelView.WorkerView;
import wrestling.model.segmentEnum.EventRecurrence;
import wrestling.model.utility.ModelUtils;
import wrestling.model.utility.StaffUtils;
import wrestling.view.utility.ViewUtils;

public class EventManager implements Serializable {

    private final List<Event> events;
    private final List<EventWorker> eventWorkers;
    private final List<MatchEvent> matchEvents;
    private final List<EventTemplate> eventTemplates;
    private final List<EventView> eventViews;

    private final DateManager dateManager;
    private final SegmentManager segmentManager;
    private final ContractManager contractManager;

    private final transient Logger logger = LogManager.getLogger(getClass());

    public EventManager(
            ContractManager contractManager,
            DateManager dateManager,
            SegmentManager segmentManager) {
        events = new ArrayList<>();
        eventWorkers = new ArrayList<>();
        matchEvents = new ArrayList<>();
        eventTemplates = new ArrayList<>();
        eventViews = new ArrayList<>();
        this.segmentManager = segmentManager;
        this.contractManager = contractManager;
        this.dateManager = dateManager;
    }

    public void addEventTemplates(List<EventTemplate> templates) {
        templates.forEach(template -> addEventTemplate(template));
    }

    public void addEventTemplate(EventTemplate template) {
        getEventTemplates().add(template);
        template.getPromotion().addEventTemplate(template);
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

    public void rescheduleEvent(Event event, LocalDate newDate) {
        EventTemplate template = event.getEventTemplate();
        event.setDate(newDate);
        updateFirstAndLastEvents(template);
    }

    public void updateEventName(EventTemplate eventTemplate) {
        for (Event event : events) {
            if (event.getEventTemplate().equals(eventTemplate)) {
                event.setName(eventTemplate.getName());
            }
        }
    }

    private void updateFirstAndLastEvents(EventTemplate template) {
        if (template != null) {
            Event event = getNextEvent(template,
                    dateManager.today());
            if (event != null) {
                template.setNextDate(getNextEvent(template,
                        dateManager.today()).getDate());
                template.setBookedUntil(getLastEvent(template).getDate());
            } else {
                template.setNextDate(LocalDate.MIN);
                template.setBookedUntil(LocalDate.MIN);
            }

        }
    }

    public void cancelEvent(Event eventToCancel) {
        EventTemplate template = eventToCancel.getEventTemplate();

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

        if (template != null) {
            template.setEventsLeft(template.getEventsLeft() - 1);
            updateFirstAndLastEvents(template);
        }

    }

    public List<Event> getEvents(PromotionView promotion) {
        List<Event> promotionEvents = new ArrayList();
        events.stream().filter((event) -> (event.getPromotion().equals(promotion))).forEach((event) -> {
            promotionEvents.add(event);
        });
        return promotionEvents;
    }

    public Event getNextEvent(EventTemplate template) {
        for (Event event : events) {
            if (event.getEventTemplate().equals(template)
                    && event.getDate().equals(template.getNextDate())) {
                return event;
            }
        }

        return null;
    }

    public Event getNextEvent(EventTemplate template, LocalDate startDate) {
        List<Event> templateEvents = new ArrayList<>();
        for (Event event : getEventsForTemplate(template)) {
            if (event.getDate().isAfter(startDate)) {
                templateEvents.add(event);
            }
        }

        templateEvents.sort(
                (o1, o2) -> o1.getDate().compareTo(o2.getDate()));
        return templateEvents.isEmpty()
                ? null : templateEvents.get(0);
    }

    public List<Event> getEventsForTemplate(EventTemplate template) {
        List<Event> templateEvents = new ArrayList<>();
        for (Event event : events) {
            if (event.getEventTemplate().equals(template)) {
                templateEvents.add(event);
            }
        }
        return templateEvents;
    }

    public Event getLastEvent(EventTemplate template) {
        List<Event> templateEvents = getEventsForTemplate(template);
        templateEvents.sort(
                (o1, o2) -> o1.getDate().compareTo(o2.getDate()));
        return templateEvents.isEmpty()
                ? null : templateEvents.get(templateEvents.size() - 1);
    }

    public List<EventTemplate> getActiveEventTemplatesFuture(YearMonth yearMonth) {
        List<EventTemplate> activeEvents = new ArrayList();
        eventTemplates.stream().filter((event) -> (!event.getEventRecurrence().equals(EventRecurrence.LIMITED)
                || getEventsLeftFuture(event, yearMonth) > 0)).forEach((event) -> {
            activeEvents.add(event);
        });
        return activeEvents;
    }

    public int getEventsLeftFuture(EventTemplate eventTemplate, YearMonth yearMonth) {
        if (!eventTemplate.getEventRecurrence().equals(EventRecurrence.LIMITED)) {
            return eventTemplate.getEventsLeft();
        }

        LocalDate presentLast = dateManager.today().minusMonths(1);
        presentLast = presentLast.with(lastDayOfMonth());

        LocalDate futureFirst = LocalDate.of(yearMonth.getYear(), yearMonth.getMonth(), 1);
        futureFirst = futureFirst.with(firstInMonth(eventTemplate.getDayOfWeek()));

        return eventTemplate.getEventsLeft()
                - Math.toIntExact(
                        ChronoUnit.WEEKS.between(presentLast, futureFirst));
    }

    public List<EventView> getEventViews(PromotionView promotion) {
        List<EventView> promotionEvents = new ArrayList();
        eventViews.stream().filter((eventView)
                -> (eventView.getEvent().getPromotion().equals(promotion))).forEach((event) -> {
            promotionEvents.add(event);
        });
        return promotionEvents;
    }

    public Event getNextEvent(PromotionView promotion, LocalDate startDate) {
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

    public Event getEventOnDate(PromotionView promotion, LocalDate date) {
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

    public int eventsAfterDate(PromotionView promotion, LocalDate date) {
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
    public List<WorkerView> allWorkers(List<Segment> segments) {

        List allWorkers = new ArrayList<>();
        for (Segment currentSegment : segments) {
            if (currentSegment instanceof Match) {
                allWorkers.addAll(segmentManager.getWorkers(currentSegment));
            }
        }

        //this should take the list of workers generated above
        //and convert it to a set, removing duplicates
        Set<WorkerView> allWorkersSet = new LinkedHashSet<>(allWorkers);
        //convert the set back to a list with no duplicates
        allWorkers = new ArrayList<>(allWorkersSet);

        return allWorkers;
    }

    public List<WorkerView> allWorkers(Event event) {
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

        for (WorkerView worker : allWorkers(getMatches(event))) {
            currentCost += contractManager.getContract(worker, event.getPromotion()).getAppearanceCost();
        }
        return currentCost;
    }

    //dynamic current cost calculation to be called while the player is booking
    public int calculateCost(List<Segment> segments, PromotionView promotion) {
        int currentCost = 0;

        for (WorkerView worker : allWorkers(segments)) {
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
    public int calculateGate(List<Segment> segments, PromotionView promotion) {

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
        for (WorkerView worker : allWorkers(getMatches(event))) {

            if (worker.getPopularity() > ModelUtils.maxPopularity(event.getPromotion()) - 10) {
                draws++;
            }
        }

        attendance += RandomUtils.nextInt(event.getPromotion().getLevel(), event.getPromotion().getLevel() * 15) * draws;

        return attendance;
    }

    public int calculateAttendance(List<Segment> segments, PromotionView promotion) {
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
        for (WorkerView worker : allWorkers(segments)) {

            if (worker.getPopularity() > ModelUtils.maxPopularity(promotion) - 10) {
                draws++;
            }
        }

        attendance += RandomUtils.nextInt(promotion.getLevel(), promotion.getLevel() * 15) * draws;

        attendance += attendance * StaffUtils.getCoverageAttendanceModifier(promotion);

        return attendance;
    }

    private String futureEventString(Event event) {
        StringBuilder sb = new StringBuilder();
        sb.append("Workers booked:");
        List<WorkerView> workers = allWorkers(event);
        for (WorkerView worker : workers) {
            sb.append("\n");
            sb.append(worker.getName());
        }
        return sb.toString();
    }

    public String generateSummaryString(Event event) {
        EventView eventView = getEventView(event);

        StringBuilder sb = new StringBuilder();

        if (event.getDate().isAfter(dateManager.today())) {
            return sb.append("This event is in the future.\n").append(futureEventString(event)).toString();
        }

        if (event.getDate().equals(dateManager.today())) {
            return sb.append("This event is scheduled for later today.\n").append(futureEventString(event)).toString();
        }
        if (eventView != null) {
            for (SegmentView segmentView : eventView.getSegmentViews()) {
                if (!segmentView.getWorkers().isEmpty()) {
                    sb.append(segmentManager.getIsolatedSegmentString(segmentView));
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
        } else {
            sb.append("Event information not available.\n");
        }

        return sb.toString();
    }

    //checks if a worker is booked at all on a given date
    public boolean isBooked(WorkerView worker, LocalDate date) {
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

    private EventWorker getBooking(WorkerView worker, LocalDate date) {
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
    public boolean isAvailable(WorkerView worker, LocalDate date, PromotionView promotion) {
        EventWorker eventWorker = getBooking(worker, date);
        if (eventWorker == null) {
            return false;
        }
        if (!eventWorker.getEvent().getPromotion().equals(promotion)) {
            return false;
        }
        return eventWorker.getWorker().getInjury() == null;
    }

    public List<WorkerView> getAvailableRoster(PromotionView promotion, LocalDate date) {

        List<WorkerView> roster = new ArrayList<>();
        for (Contract contract : contractManager.getContracts(promotion)) {
            if (contract.isActive() && isAvailable(contract.getWorker(), date, promotion)) {
                roster.add(contract.getWorker());
            }

        }

        return roster;
    }

    public List<WorkerView> getUnavailableRoster(PromotionView promotion, LocalDate date) {

        List<WorkerView> roster = new ArrayList<>();
        for (Contract contract : contractManager.getContracts(promotion)) {
            if (contract.isActive() && isAvailable(contract.getWorker(), date, promotion)) {
                roster.add(contract.getWorker());
            }

        }

        return roster;
    }

    public boolean canReschedule(Event event) {
        //return event != null && event.getTelevision() == null;
        return true;
    }

    /**
     * @return the eventTemplates
     */
    public List<EventTemplate> getEventTemplates() {
        return eventTemplates;
    }

}
