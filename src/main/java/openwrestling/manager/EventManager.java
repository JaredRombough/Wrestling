package openwrestling.manager;

import lombok.Getter;
import openwrestling.database.Database;
import openwrestling.model.gameObjects.Event;
import openwrestling.model.gameObjects.EventTemplate;
import openwrestling.model.gameObjects.Promotion;
import openwrestling.model.gameObjects.Segment;
import openwrestling.model.gameObjects.Worker;
import openwrestling.model.segment.constants.SegmentType;
import openwrestling.model.utility.ModelUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static openwrestling.model.utility.ModelUtils.currencyString;

@Getter
public class EventManager extends GameObjectManager implements Serializable {

    private final DateManager dateManager;
    private final SegmentManager segmentManager;
    private final ContractManager contractManager;
    private final transient Logger logger = LogManager.getLogger(getClass());
    private Map<Long, EventTemplate> eventTemplateMap;
    private Map<Long, Event> eventMap;

    public EventManager(Database database,
                        ContractManager contractManager,
                        DateManager dateManager,
                        SegmentManager segmentManager) {
        super(database);
        eventTemplateMap = new HashMap<>();
        eventMap = new HashMap<>();
        this.segmentManager = segmentManager;
        this.contractManager = contractManager;
        this.dateManager = dateManager;
    }

    public List<Event> getEvents() {
        return new ArrayList<>(eventMap.values());
    }

    public List<Event> getEvents(Promotion promotion) {
        return new ArrayList<>(eventMap.values()).stream()
                .filter(event -> event.getPromotion().equals(promotion))
                .collect(Collectors.toList());
    }

    public List<Event> getPastEvents(Promotion promotion, LocalDate date) {
        return new ArrayList<>(eventMap.values()).stream()
                .filter(event -> event.getPromotion().equals(promotion))
                .filter(event -> event.getDate().isBefore(date))
                .collect(Collectors.toList());
    }

    public List<Event> getFutureEvents(Promotion promotion, LocalDate date) {
        return new ArrayList<>(eventMap.values()).stream()
                .filter(event -> event.getPromotion().equals(promotion))
                .filter(event -> event.getDate().isAfter(date) || event.getDate().equals(date))
                .collect(Collectors.toList());
    }

    public List<EventTemplate> getEventTemplates() {
        return new ArrayList<>(eventTemplateMap.values());
    }

    @Override
    public void selectData() {
        List<Event> events = getDatabase().selectAll(Event.class);
        events.forEach(event -> {
            eventMap.put(event.getEventID(), event);
        });
        List<EventTemplate> eventTemplates = getDatabase().selectAll(EventTemplate.class);
        eventTemplates.forEach(eventTemplate -> {
            eventTemplateMap.put(eventTemplate.getEventTemplateID(), eventTemplate);
        });
    }

    public List<EventTemplate> createEventTemplates(List<EventTemplate> eventTemplates) {
        List<EventTemplate> saved = getDatabase().insertList(eventTemplates);
        saved.forEach(savedEvent -> eventTemplateMap.put(savedEvent.getEventTemplateID(), savedEvent));
        return saved;
    }

    public void updateEventTemplates(List<EventTemplate> eventTemplates) {
        getDatabase().updateList(eventTemplates);
        eventTemplates.forEach(updatedEvent -> eventTemplateMap.put(updatedEvent.getEventTemplateID(), updatedEvent));
    }

    public void createEvents(List<Event> events) {
        List<Event> eventsWithSegments = new ArrayList<>();
        List<Event> eventsWithoutSegments = new ArrayList<>();
        List<Segment> segmentsToSave = new ArrayList<>();

        events.forEach(event -> {
            if (CollectionUtils.isNotEmpty(event.getSegments())) {
                eventsWithSegments.add(event);
            } else {
                eventsWithoutSegments.add(event);
            }
        });

        List<Event> savedEventsWithoutSegments = getDatabase().insertList(eventsWithoutSegments);
        savedEventsWithoutSegments.forEach(event -> eventMap.put(event.getEventID(), event));

        List<Event> savedEventsWithSegments = getDatabase().insertList(eventsWithSegments);
        savedEventsWithSegments.forEach(event -> eventMap.put(event.getEventID(), event));

        for (int i = 0; i < savedEventsWithSegments.size(); i++) {
            List<Segment> segments = eventsWithSegments.get(i).getSegments();
            Event savedEvent = savedEventsWithSegments.get(i);
            segments.forEach(segment -> segment.setEvent(savedEvent));
            segmentsToSave.addAll(segments);
        }

        segmentManager.createSegments(segmentsToSave);
    }

    public Event refreshEvent(Event event) {
        return eventMap.get(event.getEventID());
    }

    public void rescheduleEvent(Event event, LocalDate newDate) {
        event.setDate(newDate);
    }

    public void updateEventName(EventTemplate eventTemplate) {
        for (Event event : getEvents()) {
            if (event.getEventTemplate().equals(eventTemplate)) {
                event.setName(eventTemplate.getName());
            }
        }
    }

    public void cancelEvent(Event eventToCancel) {
        eventMap.remove(eventToCancel.getEventID());
    }

    public Event getNextEvent(EventTemplate template, LocalDate startDate) {
        List<Event> templateEvents = new ArrayList<>();
        for (Event event : getEventsForTemplate(template)) {
            if (event.getDate().isAfter(startDate)) {
                templateEvents.add(event);
            }
        }

        templateEvents.sort(
                Comparator.comparing(Event::getDate));
        return templateEvents.isEmpty()
                ? null : templateEvents.get(0);
    }

    public List<Event> getEventsForTemplate(EventTemplate template) {
        List<Event> templateEvents = new ArrayList<>();
        for (Event event : getEvents()) {
            if (event.getEventTemplate().equals(template)) {
                templateEvents.add(event);
            }
        }
        return templateEvents;
    }

    public Event getLastEvent(EventTemplate template) {
        List<Event> templateEvents = getEventsForTemplate(template);
        templateEvents.sort(
                Comparator.comparing(Event::getDate));
        return templateEvents.isEmpty()
                ? null : templateEvents.get(templateEvents.size() - 1);
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
        for (Event event : getEvents()) {
            if (event.getDate().equals(date)
                    && event.getPromotion().equals(promotion)) {
                return event;
            }
        }
        return null;
    }

    public List<Event> getEventsOnDate(LocalDate date) {
        List<Event> eventsOnDate = new ArrayList<>();
        for (Event event : getEvents()) {
            if (event.getDate().equals(date)) {
                eventsOnDate.add(event);
            }
        }
        return eventsOnDate;
    }

    public int eventsAfterDate(Promotion promotion, LocalDate date) {
        int futureEvents = 0;
        futureEvents = getEvents().stream().filter((Event event) -> {
            return event.getPromotion().equals(promotion)
                    && event.getDate().isAfter(date);
        }).map((_item) -> 1).reduce(futureEvents, Integer::sum);
        return futureEvents;
    }

    //this will return a list of all workers currently booked
    //without any duplicates
    //so if a worker is in two different segments he is only on the list
    //one time. useful for cost calculation so we don't pay people
    //twice for the same show
    public List<Worker> allWorkers(List<Segment> segments) {

        List allWorkers = new ArrayList<>();
        for (Segment currentSegment : segments) {
            if (SegmentType.MATCH.equals(currentSegment.getSegmentType())) {
                allWorkers.addAll(segmentManager.getWorkers(currentSegment));
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
    public int calculateCost(Event event) {
        int currentCost = 0;

        List<Segment> matches = event.getSegments().stream()
                .filter(segment -> SegmentType.MATCH.equals(segment.getSegmentType()))
                .collect(Collectors.toList());

        for (Worker worker : allWorkers(matches)) {
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
    public int calculateGate(Event event) {

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

    public int calculateAttendance(Event event) {
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
        long draws = event.getSegments().stream()
                .flatMap(segment -> segment.getSegmentTeams().stream())
                .flatMap(segmentTeam -> segmentTeam.getWorkers().stream())
                .filter(worker -> worker.getPopularity() > ModelUtils.maxPopularity(event.getPromotion()) - 10)
                .count();

        attendance += RandomUtils.nextInt(event.getPromotion().getLevel(), event.getPromotion().getLevel() * 15) * draws;

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

        attendance += RandomUtils.nextInt(promotion.getLevel(), promotion.getLevel() * 15) * draws;

        return attendance;
    }

    public int calculateRating(List<Segment> segments, int duration) {
        Integer totalCrowd = segments.stream()
                .map(segment -> (segment.getSegmentLength() * segment.getCrowdRating()))
                .reduce(0, Integer::sum);

        Integer totalWork = segments.stream()
                .map(segment -> (segment.getSegmentLength() * segment.getWorkRating()))
                .reduce(0, Integer::sum);

        return (totalCrowd + totalWork) / duration / 2;
    }

    public String generateSummaryString(Event event) {
        StringBuilder sb = new StringBuilder();

        if (event.getDate().isAfter(dateManager.today())) {
            return sb.append("This event is in the future.\n").toString();
        }

        if (event.getDate().equals(dateManager.today())) {
            return sb.append("This event is scheduled for later today.\n").toString();
        }


        List<Segment> segments = segmentManager.getSegments(event);
        for (Segment segment : segments) {
            if (!segment.getWorkers().isEmpty()) {
                sb.append(segmentManager.getIsolatedSegmentString(segment, event));
            } else {
                logger.log(Level.ERROR, "Encountered empty segment when constructing event summary string");
            }

            sb.append("\n");
        }

        sb.append("\n");

        sb.append(String.format("Total cost: %s", currencyString(event.getCost())));
        sb.append("\n");
        sb.append("Attendance: ").append(event.getAttendance());
        sb.append("\n");
        sb.append(String.format("Gross profit: %s", currencyString(event.getGate())));
        sb.append("\n");
        sb.append("Rating: ").append(event.getRating());

        return sb.toString();
    }

    public boolean canReschedule(Event event) {
        //return event != null && event.getTelevision() == null;
        return true;
    }

    public List<EventTemplate> getEventTemplates(Promotion promotion) {
        return getEventTemplates().stream()
                .filter(eventTemplate -> promotion.getPromotionID() == eventTemplate.getPromotion().getPromotionID())
                .collect(Collectors.toList());
    }


}
