package openwrestling.model.manager;

import lombok.Getter;
import openwrestling.database.Database;
import openwrestling.manager.StableManager;
import openwrestling.manager.TagTeamManager;
import openwrestling.model.SegmentItem;
import openwrestling.model.gameObjects.Event;
import openwrestling.model.gameObjects.Stable;
import openwrestling.model.gameObjects.TagTeam;
import openwrestling.model.gameObjects.Worker;
import openwrestling.model.modelView.Segment;
import openwrestling.model.modelView.SegmentTeam;
import openwrestling.model.segmentEnum.AngleType;
import openwrestling.model.segmentEnum.MatchFinish;
import openwrestling.model.segmentEnum.SegmentType;
import openwrestling.model.segmentEnum.TeamType;
import openwrestling.model.utility.ModelUtils;
import openwrestling.model.utility.SegmentStringUtils;
import openwrestling.view.utility.ViewUtils;

import java.io.Serializable;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static openwrestling.model.utility.ModelUtils.slashShortNames;

@Getter
public class SegmentManager implements Serializable {

    private List<Segment> segments;
    private final DateManager dateManager;
    private final TagTeamManager tagTeamManager;
    private final StableManager stableManager;

    public SegmentManager(DateManager dateManager, TagTeamManager tagTeamManager, StableManager stableManager) {
        segments = new ArrayList<>();
        this.dateManager = dateManager;
        this.tagTeamManager = tagTeamManager;
        this.stableManager = stableManager;
    }

    public List<Segment> createSegments(List<Segment> segments) {
        List<Segment> savedSegments = Database.insertOrUpdateList(segments);
        this.segments.addAll(savedSegments);
        return savedSegments;
    }

    public List<Segment> getSegments(Event event) {
        return segments.stream()
                .filter(segment -> segment.getEvent().getEventID() == event.getEventID())
                .collect(Collectors.toList());
    }

    public void addSegment(Segment segment) {
        segments.add(segment);
    }


    public List<Worker> getWorkers(Segment segment) {
        return segment.getTeams().stream()
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
        for (Segment segment : segments) {
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

    public String getIsolatedSegmentString(Segment segment) {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(segment.getEvent().getVerboseEventTitle());
        stringBuilder.append("\n");
        stringBuilder.append(getSegmentString(segment));
        stringBuilder.append("\n");
        stringBuilder.append(segment.getSegmentType().equals(SegmentType.MATCH)
                ? ViewUtils.intToStars(segment.getWorkRating())
                : "Rating: " + segment.getWorkRating() + "%");

        return stringBuilder.toString();
    }

    public String getSegmentString(Segment segment) {
        return getSegmentString(segment, false);
    }

    public String getSegmentString(Segment segment, boolean verbose) {
        String segmentString = segment.getSegmentType().equals(SegmentType.MATCH)
                ? getMatchString(segment, verbose)
                : getAngleString(segment);
        if (verbose) {
            segmentString += " @ " + segment.getEvent().toString();
        }
        return segmentString;
    }

    public String getAngleString(Segment segment) {
        AngleType angleType = segment.getAngleType();
        List<SegmentTeam> mainTeam = segment.getTeams(angleType.mainTeamType());
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
        for (SegmentTeam tesm : segment.getTeams(angleType.addTeamType())) {
            andTeamNames.add(generateTeamName(tesm.getWorkers()));
        }

        String string = String.format(angleType.resultString(),
                mainTeamString,
                pluralString,
                ModelUtils.joinGrammatically(andTeamNames));

        if (angleType.equals(AngleType.PROMO) && segment.getTeams(TeamType.PROMO_TARGET).isEmpty()) {
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
        List<SegmentTeam> teams = segment.getTeams();
        int teamsSize = segment.getMatchParticipantTeams().size();
        String matchString = "";

        if (teamsSize > 1) {

            for (int t = 0; t < teamsSize; t++) {
                List<Worker> team = teams.get(t).getWorkers();

                matchString += generateTeamName(team, false, teams.get(t).getType());

                if (!teams.get(t).getEntourage().isEmpty()) {
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

    public String getMatchString(Segment segment, boolean verbose) {
        List<SegmentTeam> teams = segment.getTeams();
        MatchFinish finish = segment.getMatchFinish();
        int teamsSize = segment.getMatchParticipantTeams().size();
        String matchString = "";

        if (teamsSize > 1) {

            for (int t = 0; t < teamsSize; t++) {
                List<Worker> team = teams.get(t).getWorkers();

                matchString += generateTeamName(team, verbose, teams.get(t).getType());

                if (!teams.get(t).getEntourage().isEmpty()) {
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
