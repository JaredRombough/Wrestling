package openwrestling.manager;

import lombok.Getter;
import openwrestling.database.Database;
import openwrestling.database.queries.SegmentTeamQuery;
import openwrestling.model.SegmentItem;
import openwrestling.model.gameObjects.Event;
import openwrestling.model.gameObjects.EventTemplate;
import openwrestling.model.gameObjects.Segment;
import openwrestling.model.gameObjects.SegmentTeam;
import openwrestling.model.gameObjects.SegmentTemplate;
import openwrestling.model.gameObjects.Stable;
import openwrestling.model.gameObjects.TagTeam;
import openwrestling.model.gameObjects.Worker;
import openwrestling.model.segment.constants.AngleType;
import openwrestling.model.segment.constants.MatchFinish;
import openwrestling.model.segment.constants.SegmentType;
import openwrestling.model.segment.constants.ShowType;
import openwrestling.model.segment.constants.TeamType;
import openwrestling.model.utility.ModelUtils;
import openwrestling.model.utility.SegmentStringUtils;
import openwrestling.view.utility.ViewUtils;
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

import static openwrestling.model.utility.ModelUtils.slashShortNames;

@Getter
public class SegmentManager extends GameObjectManager implements Serializable {

    private final DateManager dateManager;
    private final TagTeamManager tagTeamManager;
    private final StableManager stableManager;
    private final Map<Long, Segment> segmentMap;
    private List<SegmentTemplate> segmentTemplates;

    public SegmentManager(Database database, DateManager dateManager, TagTeamManager tagTeamManager, StableManager stableManager) {
        super(database);
        segmentMap = new HashMap<>();
        segmentTemplates = new ArrayList<>();
        this.dateManager = dateManager;
        this.tagTeamManager = tagTeamManager;
        this.stableManager = stableManager;
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

    public List<Segment> getRecentSegments(Worker worker) {
        int segmentLimit = 10;
        return getSegments().stream()
                .filter(segment -> CollectionUtils.isNotEmpty(segment.getWorkers()) && segment.getWorkers().contains(worker))
                .sorted(Comparator.comparing(segment -> ((Segment) segment).getEvent().getDate()).reversed())
                .limit(segmentLimit)
                .collect(Collectors.toList());
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
            if (segment.getDate().isAfter(earliestDate) && segment.getSegmentType().equals(SegmentType.MATCH)) {
                weekMatches.add(segment);
            }
        }
        weekMatches.sort((Segment sv1, Segment sv2)
                -> sv2.getWorkRating() - sv1.getWorkRating());

        int actualTotal = Math.min(totalMatches, weekMatches.size());

        return new ArrayList<>(weekMatches.subList(0, actualTotal));
    }

    public String getSegmentTitle(Segment segment) {
        if (segment.getSegmentType().equals(SegmentType.MATCH)) {
            return SegmentStringUtils.getMatchTitle(segment);
        }
        return getAngleTitle(segment);
    }

    private String getAngleTitle(Segment segment) {
        return segment.getAngleType().description();
    }

    public String getIsolatedSegmentString(Segment segment, Event event) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(event.getVerboseEventTitle());
        stringBuilder.append("\n");
        stringBuilder.append(getSegmentString(segment));
        stringBuilder.append("\n");
        stringBuilder.append(segment.getSegmentType().equals(SegmentType.MATCH)
                ? ViewUtils.intToStars(segment.getWorkRating())
                : "Rating: " + segment.getWorkRating() + "%");

        return stringBuilder.toString();
    }

    public String getSegmentStringForWorkerOverview(Segment segment, Event event) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getSegmentString(segment));
        stringBuilder.append("\t ");
        stringBuilder.append(event.getVerboseEventTitle());
        return stringBuilder.toString();
    }

    public String getSegmentString(Segment segment) {
        return segment.getSegmentType().equals(SegmentType.MATCH)
                ? getMatchString(segment)
                : getAngleString(segment);
    }

    public String getAngleString(Segment segment) {
        AngleType angleType = segment.getAngleType();
        List<SegmentTeam> mainTeam = segment.getSegmentTeams(angleType.mainTeamType());
        String mainTeamString;
        String pluralString;
        if (mainTeam.isEmpty()) {
            mainTeamString = "?";
            pluralString = "";
        } else {
            mainTeamString = generateTeamName(mainTeam.get(0).getWorkers(), true, mainTeam.get(0).getType());
            pluralString = mainTeam.get(0).getWorkers().size() > 1 ? "" : "s";
        }
        List<String> andTeamNames = new ArrayList<>();
        for (SegmentTeam tesm : segment.getSegmentTeams(angleType.addTeamType())) {
            andTeamNames.add(generateTeamName(tesm.getWorkers()));
        }

        String string = String.format(angleType.resultString(),
                mainTeamString,
                pluralString,
                ModelUtils.joinGrammatically(andTeamNames));

        if (angleType.equals(AngleType.PROMO) && segment.getSegmentTeams(TeamType.PROMO_TARGET).isEmpty()) {
            string = string.split("targeting")[0];
            string = string.replace(" targeting", "");
        }

        if (angleType.equals(AngleType.OFFER)) {
            string += SegmentStringUtils.getOfferString(segment);
        } else if (angleType.equals(AngleType.CHALLENGE)) {
            string += SegmentStringUtils.getChallengeString(segment);
        }

        return string;

    }

    public String generateTeamName(List<? extends SegmentItem> segmentItems, TeamType teamType) {
        return generateTeamName(segmentItems, false, teamType);
    }

    public String generateTeamName(List<? extends SegmentItem> segmentItems) {
        return generateTeamName(segmentItems, false, TeamType.DEFAULT);
    }

    public String generateTeamName(List<? extends SegmentItem> segmentItems, boolean verbose) {
        return generateTeamName(segmentItems, verbose, TeamType.DEFAULT);
    }

    public String generateTeamName(List<? extends SegmentItem> segmentItems, boolean verbose, TeamType teamType) {
        if (!segmentItems.isEmpty()) {
            if (segmentItems.size() == 2) {
                String tagTeam = getTagTeamName(segmentItems);
                if (!tagTeam.isEmpty()) {
                    return tagTeam;
                }
            } else if (segmentItems.size() > 1 && !TeamType.OFFEREE.equals(teamType) && !TeamType.OFFERER.equals(teamType)) {
                for (Stable stable : stableManager.getStables()) {
                    if (stable.getWorkers().containsAll(segmentItems)) {
                        return stable.getName();
                    }
                }
            }
            return verbose ? ModelUtils.slashNames(segmentItems) : slashShortNames(segmentItems);
        } else {
            return "(Empty Team)";
        }
    }

    public String getTagTeamName(List<? extends SegmentItem> segmentItems) {
        for (TagTeam tagTeam : tagTeamManager.getTagTeams()) {
            if (tagTeam.getSegmentItems().containsAll(segmentItems)) {
                return tagTeam.getName();
            }
        }
        return String.format("");
    }

    public String getVsMatchString(Segment segment) {
        List<SegmentTeam> teams = segment.getSegmentTeams();
        int teamsSize = segment.getMatchParticipantTeams().size();
        String matchString = "";

        if (teamsSize > 1) {

            for (int t = 0; t < teamsSize; t++) {
                List<Worker> team = teams.get(t).getWorkers();

                matchString += generateTeamName(team, false, teams.get(t).getType());

                if (CollectionUtils.isNotEmpty(teams.get(t).getEntourage())) {
                    matchString += " w/ " + slashShortNames(teams.get(t).getEntourage());
                }

                if (t == 0 && !matchString.isEmpty()) {
                    matchString += " vs ";

                } else if (t < teamsSize - 1 && !matchString.isEmpty()) {
                    matchString += ", ";
                }

            }
        } else {
            //probable placeholder
            matchString += !teams.isEmpty() ? teams.get(0) : "";
        }

        if (matchString.isEmpty()) {

            matchString += "Empty Match";
        }

        return matchString;

    }

    public String getMatchString(Segment segment) {
        List<SegmentTeam> teams = segment.getSegmentTeams();
        MatchFinish finish = segment.getMatchFinish();
        int teamsSize = segment.getMatchParticipantTeams().size();
        String matchString = "";

        if (teamsSize > 1) {

            for (int t = 0; t < teamsSize; t++) {
                List<Worker> team = teams.get(t).getWorkers();

                matchString += generateTeamName(team, false, teams.get(t).getType());

                if (CollectionUtils.isNotEmpty(teams.get(t).getEntourage())) {
                    matchString += " w/ " + slashShortNames(teams.get(t).getEntourage());
                }

                if (t == 0 && !matchString.isEmpty()) {
                    matchString += " def. ";

                } else if (t < teamsSize - 1 && !matchString.isEmpty()) {
                    matchString += ", ";
                }

            }

            switch (finish) {
                case COUNTOUT:
                    matchString += " by Countout";
                    break;
                case DQINTERFERENCE:
                case DQ:
                    matchString += " by DQ";
                    break;
                default:
                    break;

            }

        } else {
            //probable placeholder
            matchString += !teams.isEmpty() ? teams.get(0) : "";
        }

        if (finish != null && finish.equals(MatchFinish.DRAW)) {
            matchString = matchString.replace("def.", "drew");
        }

        if (matchString.isEmpty()) {

            matchString += "Empty Match";
        }

        return matchString;

    }
}
