package wrestling.model.manager;

import java.util.ArrayList;
import java.util.List;
import wrestling.model.Match;
import wrestling.model.segmentEnum.MatchFinish;
import wrestling.model.segmentEnum.MatchRule;
import wrestling.model.MatchTitle;
import wrestling.model.MatchWorker;
import wrestling.model.Title;
import wrestling.model.Worker;
import wrestling.model.interfaces.Segment;
import wrestling.model.modelView.SegmentView;
import wrestling.model.modelView.SegmentTeam;
import wrestling.model.segmentEnum.AngleType;
import wrestling.model.segmentEnum.SegmentType;
import wrestling.model.segmentEnum.TeamType;
import wrestling.model.utility.ModelUtils;

public class MatchManager {

    private final List<Match> matches;
    private final List<MatchWorker> matchWorkers;
    private final List<MatchTitle> matchTitles;
    private final List<SegmentView> segmentViews;
    private final DateManager dateManager;

    public MatchManager(DateManager dateManager) {
        matches = new ArrayList<>();
        matchWorkers = new ArrayList<>();
        matchTitles = new ArrayList<>();
        segmentViews = new ArrayList<>();
        this.dateManager = dateManager;
    }

    public void addMatchWorker(MatchWorker matchWorker) {
        matchWorkers.add(matchWorker);
    }

    public void addMatchTitle(MatchTitle matchTitle) {
        matchTitles.add(matchTitle);
    }

    public void addMatch(Match match) {
        matches.add(match);
    }

    public void addSegmentView(SegmentView segmentView) {
        segmentViews.add(segmentView);
    }

    public Title getTitle(Match match) {
        Title title = null;
        for (MatchTitle matchTitle : matchTitles) {
            if (matchTitle.getMatch().equals(match)) {
                title = matchTitle.getTitle();
            }
        }
        return title;
    }

    public List<Worker> getWinners(Match match) {
        List<Worker> winners = new ArrayList<>();
        for (MatchWorker matchWorker : matchWorkers) {
            if (matchWorker.getMatch().equals(match)
                    && matchWorker.getTeam() == 0) {
                winners.add(matchWorker.getWorker());
            }
        }
        return winners;
    }

    public List<Worker> getWorkers(Segment segment) {
        List<Worker> workers = new ArrayList<>();
        for (MatchWorker matchWorker : matchWorkers) {
            if (matchWorker.getMatch().equals(segment)) {
                workers.add(matchWorker.getWorker());
            }
        }
        return workers;
    }

    private List<MatchWorker> getMatchWorkers(Match match) {
        List<MatchWorker> getMatchWorkers = new ArrayList<>();
        matchWorkers.stream().filter((matchWorker) -> (matchWorker.getMatch().equals(match))).forEach((matchWorker) -> {
            getMatchWorkers.add(matchWorker);
        });
        return getMatchWorkers;
    }

    public String getMatchStringForMonths(Worker worker, int months) {

        StringBuilder sb = new StringBuilder();

        for (SegmentView segmentView : segmentViews) {
            if (segmentView.getWorkers().contains(worker)
                    && segmentView.getDate().isBefore(dateManager.today().minusMonths(months))) {
                sb.append(getSegmentString(segmentView));
                sb.append("\n");
            }
        }

        return sb.length() > 0 ? sb.toString() : "No recent matches";

    }

    public String getSegmentTitle(SegmentView segmentView) {
        if (segmentView.getSegmentType().equals(SegmentType.MATCH)) {
            return getMatchTitle(segmentView);
        }
        return "(Angle)\n";
    }

    public String getMatchTitle(SegmentView segmentView) {
        List<SegmentTeam> teams = segmentView.getTeams();
        MatchRule rules = segmentView.getRules();

        String string = "";

        if (segmentView.getWorkers().isEmpty()) {
            return "Empty Segment";
        }

        if (isHandicapMatch(segmentView)) {
            string += "Handicap Match\n";

        } else if (rules.equals(MatchRule.DEFAULT)) {

            int teamsSize = segmentView.getMatchParticipants().size();

            switch (teamsSize) {
                case 2:
                    int teamSize = teams.get(0).getWorkers().size();
                    switch (teamSize) {
                        case 1:
                            string += "Singles Match\n";
                            break;
                        case 2:
                            string += "Tag Team Match\n";
                            break;
                        case 3:
                            string += "Six Man Tag Team Match\n";
                            break;
                        case 4:
                            string += "Eight Man Tag Team Match\n";
                            break;
                        case 5:
                            string += "Ten Man Tag Team Match\n";
                            break;
                        default:
                            string += String.format("%d Man Tag Team Match\n", teamSize * 2);
                            break;
                    }
                    break;
                default:
                    string += teamsSize + "-Way Match\n";
                    break;
            }
        } else {
            string += rules.description() + " Match\n";
        }

        return string;
    }

    public String getSegmentString(SegmentView segmentView) {
        return segmentView.getSegmentType().equals(SegmentType.MATCH)
                ? getMatchString(segmentView)
                : getAngleString(segmentView);
    }

    public String getAngleString(SegmentView segmentView) {
        AngleType angleType = segmentView.getAngleType();
        List<SegmentTeam> mainTeam = segmentView.getTeams(angleType.mainTeamType());
        String mainTeamString;
        if (mainTeam.isEmpty()) {
            mainTeamString = "?";
        } else {
            mainTeamString = ModelUtils.slashNames(mainTeam.get(0).getWorkers());
        }

        return String.format(angleType.resultString(),
                mainTeamString,
                mainTeam.size() > 1 ? "" : "s",
                ModelUtils.andTeams(segmentView.getTeams(angleType.addTeamType())));

    }

    public String getMatchString(SegmentView segmentView) {
        List<SegmentTeam> teams = segmentView.getTeams();
        MatchFinish finish = segmentView.getFinish();
        int teamsSize = segmentView.getMatchParticipants().size();
        String matchString = getSegmentTitle(segmentView);

        if (segmentView.getWorkers().isEmpty()) {
            return matchString;
        }

        if (teamsSize > 1) {

            for (int t = 0; t < teamsSize; t++) {
                List<Worker> team = teams.get(t).getWorkers();

                matchString += ModelUtils.slashShortNames(team);

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
        if (matchString.isEmpty()) {

            matchString += "Empty Segment";
        }

        return matchString;

    }

    private boolean isHandicapMatch(SegmentView segmentView) {
        boolean handicap = false;

        int size = segmentView.getMatchParticipants().get(0).getWorkers().size();
        for (SegmentTeam team : segmentView.getMatchParticipants()) {
            if (team.getWorkers().size() != size && !team.getWorkers().isEmpty()) {
                handicap = true;
                break;

            }
        }
        return handicap;
    }

}
