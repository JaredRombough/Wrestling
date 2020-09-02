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
import java.util.ArrayList;
import java.util.List;

import static openwrestling.model.segment.constants.MatchFinish.DRAW;
import static openwrestling.model.utility.ModelUtils.slashShortNames;

@Getter
public class SegmentStringService implements Serializable {

    private final SegmentManager segmentManager;
    private final TagTeamManager tagTeamManager;
    private final StableManager stableManager;

    public SegmentStringService(SegmentManager segmentManager, TagTeamManager tagTeamManager, StableManager stableManager) {
        this.segmentManager = segmentManager;
        this.tagTeamManager = tagTeamManager;
        this.stableManager = stableManager;
    }

    public String getWorkerRecord(Worker worker, Promotion promotion) {
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

    public String getSegmentStringForWorkerInfo(Segment segment, Event event) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(String.format("%s (%s)", event.getName(), event.getDate()));
        stringBuilder.append("\n");
        stringBuilder.append(getSegmentString(segment));
        stringBuilder.append("\n");
        stringBuilder.append(segment.getSegmentType().equals(SegmentType.MATCH)
                ? ViewUtils.intToStars(segment.getWorkRating())
                : "Rating: " + segment.getWorkRating() + "%");

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
