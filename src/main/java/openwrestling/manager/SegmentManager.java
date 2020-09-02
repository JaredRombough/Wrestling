package openwrestling.manager;

import lombok.Getter;
import openwrestling.database.Database;
import openwrestling.database.queries.SegmentTeamQuery;
import openwrestling.model.gameObjects.Event;
import openwrestling.model.gameObjects.EventTemplate;
import openwrestling.model.gameObjects.Promotion;
import openwrestling.model.gameObjects.Segment;
import openwrestling.model.gameObjects.SegmentTeam;
import openwrestling.model.gameObjects.SegmentTemplate;
import openwrestling.model.gameObjects.Worker;
import openwrestling.model.segment.constants.AngleType;
import openwrestling.model.segment.constants.SegmentType;
import openwrestling.model.segment.constants.ShowType;
import org.apache.commons.collections4.CollectionUtils;

import java.io.Serializable;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static openwrestling.model.segment.constants.SegmentType.MATCH;

@Getter
public class SegmentManager extends GameObjectManager implements Serializable {

    private final DateManager dateManager;
    private final Map<Long, Segment> segmentMap;
    private List<SegmentTemplate> segmentTemplates;

    public SegmentManager(Database database, DateManager dateManager) {
        super(database);
        segmentMap = new HashMap<>();
        segmentTemplates = new ArrayList<>();
        this.dateManager = dateManager;
    }

    @Override
    public void selectData() {
        List<Segment> segments = getDatabase().selectAll(Segment.class);
        segments.forEach(segment -> segmentMap.put(segment.getSegmentID(), segment));
        List<SegmentTeam> segmentTeams = getDatabase().querySelect(new SegmentTeamQuery());
        segmentTeams.stream()
                .collect(Collectors.groupingBy(segmentTeam -> segmentTeam.getSegment().getSegmentID()))
                .forEach((segmentID, teams) -> segmentMap.get(segmentID).setSegmentTeams(teams));

        segmentTemplates = getDatabase().selectAll(SegmentTemplate.class);
    }

    public List<Segment> getSegments() {
        return new ArrayList<>(segmentMap.values());
    }

    public void deleteSegmentTemplates(EventTemplate eventTemplate) {
        getSegmentTemplates(eventTemplate)
                .forEach(segmentTemplate ->
                        getDatabase().deleteByID(SegmentTemplate.class, segmentTemplate.getSegmentTemplateID())
                );
        this.segmentTemplates = getDatabase().selectAll(SegmentTemplate.class);
    }

    public List<Segment> createSegments(List<Segment> segments, List<Event> events) {
        List<SegmentTemplate> segmentTemplates = segments.stream()
                .filter(segment -> SegmentType.ANGLE.equals(segment.getSegmentType()) &&
                        AngleType.CHALLENGE.equals(segment.getAngleType()) &&
                        !ShowType.TONIGHT.equals(segment.getShowType()) &&
                        segment.getChallengeSegment() != null)
                .map(Segment::getChallengeSegment)
                .collect(Collectors.toList());

        if (CollectionUtils.isNotEmpty(segmentTemplates)) {
            if (this.segmentTemplates == null) {
                this.segmentTemplates = new ArrayList<>();
            }
            this.segmentTemplates.addAll(getDatabase().insertList(segmentTemplates));
        }


        List<Segment> savedSegments = getDatabase().insertList(segments);

        List<SegmentTeam> segmentTeamsToSave = new ArrayList<>();
        for (int i = 0; i < savedSegments.size(); i++) {
            List<SegmentTeam> segmentTeams = segments.get(i).getSegmentTeams();
            Segment savedSegment = savedSegments.get(i);
            segmentTeams.forEach(segmentTeam -> segmentTeam.setSegment(savedSegment));
            segmentTeamsToSave.addAll(segmentTeams);
        }

        List<SegmentTeam> savedSegmentTeams = getDatabase().insertList(segmentTeamsToSave);

        savedSegments.forEach(segment -> {
            segment.setSegmentTeams(
                    savedSegmentTeams.stream()
                            .filter(segmentTeam -> segment.equals(segmentTeam.getSegment()))
                            .collect(Collectors.toList())
            );
            segment.setEvent(
                    events.stream()
                            .filter(event -> event.equals(segment.getEvent()))
                            .findFirst()
                            .orElseThrow()
            );
            segmentMap.put(segment.getSegmentID(), segment);
        });

        return savedSegments;
    }

    public List<Segment> getSegments(Event event) {
        return getSegments().stream()
                .filter(segment -> segment.getEvent().getEventID() == event.getEventID())
                .collect(Collectors.toList());
    }

    public List<Segment> getMatches(Worker worker, Promotion promotion) {
        return getSegments().stream()
                .filter(segment -> promotion.equals(segment.getPromotion()))
                .filter(segment -> MATCH.equals(segment.getSegmentType()))
                .filter(segment -> CollectionUtils.isNotEmpty(segment.getWorkers()) && segment.getWorkers().contains(worker))
                .collect(Collectors.toList());
    }

    public List<Segment> getRecentSegments(Worker worker) {
        int segmentLimit = 10;
        return getSegments().stream()
                .filter(segment -> CollectionUtils.isNotEmpty(segment.getWorkers()) && segment.getWorkers().contains(worker))
                .sorted(Comparator.comparing(segment -> ((Segment) segment).getEvent().getDate()).reversed())
                .limit(segmentLimit)
                .collect(Collectors.toList());
    }

    public Segment getLastSegment(Worker worker, Promotion promotion) {
        int segmentLimit = 1;
        return getSegments().stream()
                .filter(segment -> CollectionUtils.isNotEmpty(segment.getWorkers()) && segment.getWorkers().contains(worker))
                .filter(segment -> promotion.equals(segment.getPromotion()))
                .sorted(Comparator.comparing(segment -> ((Segment) segment).getEvent().getDate()).reversed())
                .limit(segmentLimit)
                .findFirst()
                .orElse(null);
    }

    public List<SegmentTemplate> getSegmentTemplates(EventTemplate eventTemplate) {
        return segmentTemplates.stream()
                .filter(segment -> segment.getEventTemplate().getEventTemplateID() == eventTemplate.getEventTemplateID())
                .collect(Collectors.toList());
    }

    public List<Worker> getWorkers(Segment segment) {
        return segment.getSegmentTeams().stream()
                .flatMap(segmentTeam -> segmentTeam.getWorkers().stream())
                .collect(Collectors.toList());
    }


    public List<Segment> getTopMatches(LocalDate localDate, ChronoUnit unit, int units, int totalMatches) {

        LocalDate earliestDate = localDate;
        switch (unit) {
            case FOREVER:
                earliestDate = LocalDate.MIN;
                break;
            case WEEKS:
                for (int i = 0; i < units; i++) {
                    earliestDate = localDate.with(TemporalAdjusters.previous(DayOfWeek.SUNDAY));
                }
                break;
            case YEARS:
                earliestDate = localDate.withDayOfYear(1);
                if (units > 1) {
                    earliestDate = earliestDate.minusYears(units);
                }
                break;
            case MONTHS:
                earliestDate = localDate.withDayOfMonth(1);
                if (units > 1) {
                    earliestDate = earliestDate.minusMonths(units);
                }
                break;
        }
        List<Segment> weekMatches = new ArrayList<>();
        for (Segment segment : getSegments()) {
            if (segment.getDate().isAfter(earliestDate) && segment.getSegmentType().equals(MATCH)) {
                weekMatches.add(segment);
            }
        }
        weekMatches.sort((Segment sv1, Segment sv2)
                -> sv2.getWorkRating() - sv1.getWorkRating());

        int actualTotal = Math.min(totalMatches, weekMatches.size());

        return new ArrayList<>(weekMatches.subList(0, actualTotal));
    }

}
