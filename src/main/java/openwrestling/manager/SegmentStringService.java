package openwrestling.manager;

import lombok.Getter;
import openwrestling.model.SegmentItem;
import openwrestling.model.gameObjects.Event;
import openwrestling.model.gameObjects.Promotion;
import openwrestling.model.gameObjects.Segment;
import openwrestling.model.gameObjects.SegmentTeam;
import openwrestling.model.gameObjects.Stable;
import openwrestling.model.gameObjects.TagTeam;
import openwrestling.model.gameObjects.Worker;
import openwrestling.model.segment.constants.AngleType;
import openwrestling.model.segment.constants.MatchFinish;
import openwrestling.model.segment.constants.SegmentType;
import openwrestling.model.segment.constants.TeamType;
import openwrestling.model.utility.ModelUtils;
import openwrestling.model.utility.SegmentStringUtils;
import openwrestling.view.utility.ViewUtils;
import org.apache.commons.collections4.CollectionUtils;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import static java.time.temporal.ChronoUnit.DAYS;
import static openwrestling.model.segment.constants.MatchFinish.DRAW;
import static openwrestling.model.segment.constants.TeamType.LOSER;
import static openwrestling.model.segment.constants.TeamType.WINNER;
import static openwrestling.model.utility.ModelUtils.currencyString;
import static openwrestling.model.utility.ModelUtils.slashShortNames;

@Getter
public class SegmentStringService implements Serializable {

    private final SegmentManager segmentManager;
    private final TagTeamManager tagTeamManager;
    private final StableManager stableManager;
    private final EventManager eventManager;
    private final ContractManager contractManager;

    public SegmentStringService(SegmentManager segmentManager,
                                TagTeamManager tagTeamManager,
                                StableManager stableManager,
                                EventManager eventManager,
                                ContractManager contractManager) {
        this.segmentManager = segmentManager;
        this.tagTeamManager = tagTeamManager;
        this.stableManager = stableManager;
        this.eventManager = eventManager;
        this.contractManager = contractManager;
    }


    public String generateSummaryString(Event event, LocalDate today) {
        StringBuilder sb = new StringBuilder();

        if (event.getDate().isAfter(today)) {
            return sb.append("This event is in the future.\n").toString();
        }

        if (event.getDate().equals(today)) {
            return sb.append("This event is scheduled for later today.\n").toString();
        }


        List<Segment> segments = segmentManager.getSegments(event);
        for (Segment segment : segments) {
            if (!segment.getWorkers().isEmpty()) {
                sb.append(getIsolatedSegmentString(segment, event));
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


    public String getOverallWorkerRecord(Worker worker, Promotion promotion) {
        List<Segment> matches = segmentManager.getMatches(worker, promotion);

        int wins = 0;
        int losses = 0;
        int draw = 0;

        for (Segment match : matches) {
            if (DRAW.equals(match.getMatchFinish())) {
                draw++;
            } else {
                if (match.getWinners().contains(worker)) {
                    wins++;
                } else {
                    losses++;
                }
            }
        }

        return String.format("Record: %d-%d-%d", wins, losses, draw);
    }

    public String getWorkerRecord(Worker worker, Promotion promotion, int teamSize) {
        List<Segment> matches = segmentManager.getMatches(worker, promotion).stream()
                .filter(segment -> segment.isMatchWithTwoTeamsOfSize(teamSize))
                .collect(Collectors.toList());

        int wins = 0;
        int losses = 0;
        int draw = 0;

        for (Segment match : matches) {
            if (DRAW.equals(match.getMatchFinish())) {
                draw++;
            } else {
                if (match.getWinners().contains(worker)) {
                    wins++;
                } else {
                    losses++;
                }
            }
        }

        return String.format("Record: %d-%d-%d", wins, losses, draw);
    }

    public String getWorkerStreak(Worker worker, Promotion promotion) {
        List<Segment> matches = segmentManager.getMatches(worker, promotion).stream()
                .sorted(Comparator.comparing(Segment::getDate).reversed())
                .collect(Collectors.toList());

        int wins = 0;
        int losses = 0;
        int draw = 0;
        TeamType lastTeamType = null;

        for (Segment match : matches) {
            if (DRAW.equals(match.getMatchFinish())) {
                if (lastTeamType != null && !DRAW.equals(lastTeamType)) {
                    break;
                }
                draw++;
                lastTeamType = TeamType.DRAW;
            } else {
                if (match.getWinners().contains(worker)) {
                    if (lastTeamType != null && !WINNER.equals(lastTeamType)) {
                        break;
                    }
                    wins++;
                    lastTeamType = WINNER;
                } else {
                    if (lastTeamType != null && !LOSER.equals(lastTeamType)) {
                        break;
                    }
                    losses++;
                    lastTeamType = LOSER;
                }
            }
        }

        if (lastTeamType == null) {
            return "";
        }

        int streakTotal;
        String description;

        switch (lastTeamType) {
            case LOSER:
                streakTotal = losses;
                description = streakTotal > 1 ? "losses" : "loss";
                break;
            case WINNER:
                streakTotal = wins;
                description = streakTotal > 1 ? "wins" : "win";
                break;
            case DRAW:
                streakTotal = draw;
                description = streakTotal > 1 ? "draws" : "draw";
                break;
            default:
                streakTotal = 0;
                description = "matches";
        }

        return String.format("%d %s", streakTotal, description);
    }

    public String getPercentOfShowsString(Worker worker, Promotion promotion, LocalDate today) {
        LocalDate contractStartDate = contractManager.getContract(worker, promotion).getStartDate();

        float totalEvents = eventManager.getEventsBetweenDates(promotion, contractStartDate, today).size();

        if (totalEvents == 0) {
            return "";
        }

        float totalAppearances = segmentManager.getSegmentsBetweenDates(worker, promotion, contractStartDate, today).stream()
                .map(segment -> segment.getEvent().getEventID())
                .collect(Collectors.toCollection(HashSet::new))
                .size();

        float percentOfTotal = 100 * totalAppearances / totalEvents;

        return String.format("Appears on %.0f%% of shows", percentOfTotal);
    }

    public String getMissedShowStreakString(Worker worker, Promotion promotion, LocalDate today) {
        LocalDate contractStartDate = contractManager.getContract(worker, promotion).getStartDate();

        float totalEvents = eventManager.getEventsBetweenDates(promotion, contractStartDate, today).size();

        if (totalEvents == 0) {
            return "";
        }

        HashSet<Long> appearedAtEventIDs = segmentManager.getSegmentsBetweenDates(worker, promotion, contractStartDate, today).stream()
                .map(segment -> segment.getEvent().getEventID())
                .collect(Collectors.toCollection(HashSet::new));

        List<Event> events = eventManager.getEventsBetweenDates(promotion, contractStartDate, today).stream()
                .sorted(Comparator.comparing(Event::getDate).reversed())
                .collect(Collectors.toList());

        int missedStreak = 0;

        for (Event event : events) {
            if (!appearedAtEventIDs.contains(event.getEventID())) {
                missedStreak++;
            } else break;
        }

        if (missedStreak == 0) {
            return "";
        }

        return String.format("Missed %d show%s in a row",
                missedStreak,
                missedStreak > 1 ? "s" : ""
        );
    }

    public String getSegmentTitle(Segment segment) {
        if (segment.getSegmentType().equals(SegmentType.MATCH)) {
            return SegmentStringUtils.getMatchTitle(segment);
        }
        return getAngleTitle(segment);
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

    public String getSegmentStringForWorkerInfo(Segment segment, Event event, LocalDate date) {
        long daysAgo = DAYS.between(segment.getDate(), date);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(String.format("%s (%d day%s ago)",
                event.getName(),
                daysAgo,
                daysAgo > 1 ? "s" : ""
        ));

        return stringBuilder.toString();
    }

    public String getSegmentString(Segment segment) {
        return segment.getSegmentType().equals(SegmentType.MATCH)
                ? getMatchString(segment)
                : getAngleString(segment);
    }

    public String generateTeamName(List<? extends SegmentItem> segmentItems, TeamType teamType) {
        return generateTeamName(segmentItems, false, teamType);
    }

    public String generateTeamName(List<? extends SegmentItem> segmentItems) {
        return generateTeamName(segmentItems, false, TeamType.DEFAULT);
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

    private String getMatchString(Segment segment) {
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

        if (finish != null && finish.equals(DRAW)) {
            matchString = matchString.replace("def.", "drew");
        }

        if (matchString.isEmpty()) {

            matchString += "Empty Match";
        }

        return matchString;

    }

    private String getAngleTitle(Segment segment) {
        return segment.getAngleType().description();
    }


    private String getAngleString(Segment segment) {
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

    private String generateTeamName(List<? extends SegmentItem> segmentItems, boolean verbose, TeamType teamType) {
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

}
