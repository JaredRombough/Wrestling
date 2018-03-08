package wrestling.model.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import wrestling.model.Match;
import wrestling.model.MatchFinishes;
import wrestling.model.MatchRules;
import wrestling.model.MatchTitle;
import wrestling.model.MatchWorker;
import wrestling.model.Title;
import wrestling.model.Worker;
import wrestling.model.interfaces.MatchRule;
import wrestling.model.interfaces.Segment;
import wrestling.model.modelView.SegmentView;
import wrestling.model.modelView.SegmentTeam;
import wrestling.model.utility.ModelUtilityFunctions;
import wrestling.view.event.TeamType;

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

    public List<SegmentTeam> getTeams(Match match) {
        List<SegmentTeam> teams = new ArrayList<>();
        SegmentTeam team = new SegmentTeam(new ArrayList<>(), TeamType.DEFAULT);
        teams.add(team);

        List<MatchWorker> allMatchWorkers = getMatchWorkers(match);

        Collections.sort(allMatchWorkers, (MatchWorker o1, MatchWorker o2) -> o1.getTeam() - o2.getTeam());

        int lastTeam = !allMatchWorkers.isEmpty() ? allMatchWorkers.get(0).getTeam() : 0;
        for (MatchWorker matchWorker : allMatchWorkers) {
            if (team.getWorkers().isEmpty() || lastTeam == matchWorker.getTeam()) {
                team.getWorkers().add(matchWorker.getWorker());
                lastTeam = matchWorker.getTeam();
            } else {
                team = new SegmentTeam(new ArrayList<>(), TeamType.DEFAULT);
                teams.add(team);
                team.getWorkers().add(matchWorker.getWorker());
            }
        }
        return teams;
    }

    public String getMatchStringForMonths(Worker worker, int months) {

        StringBuilder sb = new StringBuilder();

        for (SegmentView segmentView : segmentViews) {
            if (segmentView.getWorkers().contains(worker)
                    && segmentView.getDate().isBefore(dateManager.today().minusMonths(months))) {
                sb.append(getMatchString(segmentView));
                sb.append("\n");
            }
        }

        return sb.length() > 0 ? sb.toString() : "No recent matches";

    }

    public String getMatchTitle(SegmentView segmentView) {
        List<SegmentTeam> teams = segmentView.getTeams();
        MatchRules rules = segmentView.getRules();

        String string = "";

        if (isHandicapMatch(teams)) {
            string += "Handicap Match\n";

        } else if (rules.equals(MatchRules.DEFAULT)) {

            int teamsSize = teamsSizeNoInterference(segmentView);

            switch (teamsSize) {
                case 0:
                    string += "Empty Segment";
                    break;
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

    public String getMatchString(SegmentView segmentView) {
        List<SegmentTeam> teams = segmentView.getTeams();
        MatchFinishes finish = segmentView.getFinish();
        int teamsSize = teamsSizeNoInterference(segmentView);

        String string = new String();

        if (teamsSize > 1) {

            string += getMatchTitle(segmentView);

            for (int t = 0; t < teamsSize; t++) {
                List<Worker> team = teams.get(t).getWorkers();

                string += ModelUtilityFunctions.slashShortNames(team);

                if (t == 0 && !string.isEmpty()) {
                    string += " def. ";

                } else if (t < teamsSize - 1 && !string.isEmpty()) {
                    string += ", ";
                }

            }

            switch (finish) {
                case COUNTOUT:
                    string += " by Countout";
                    break;
                case DQINTERFERENCE:
                case DQ:
                    string += " by DQ";
                    break;
                default:
                    break;

            }

        } else {
            //probable placeholder
            string += !teams.isEmpty() ? teams.get(0) : "";
        }
        if (string.isEmpty()) {

            string += "Empty Segment";
        }

        return string;

    }

    private boolean isHandicapMatch(List<SegmentTeam> teams) {
        boolean handicap = false;

        int size = teams.get(0).getWorkers().size();
        for (SegmentTeam team : teams) {
            if (team.getType().equals(TeamType.DEFAULT)
                    && team.getWorkers().size() != size && !team.getWorkers().isEmpty()) {
                handicap = true;
                break;

            }
        }
        return handicap;
    }

    private int teamsSizeNoInterference(SegmentView segmentView) {
        int teamsSize = 0;
        for (SegmentTeam team : segmentView.getTeams()) {
            if (team.getType().equals(TeamType.DEFAULT)) {
                teamsSize++;
            }
        }
        return teamsSize;
    }

}
