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

    public List<List<Worker>> getTeams(Match match) {
        List<List<Worker>> teams = new ArrayList<>();
        List<Worker> team = new ArrayList<>();
        teams.add(team);

        List<MatchWorker> allMatchWorkers = getMatchWorkers(match);

        Collections.sort(allMatchWorkers, (MatchWorker o1, MatchWorker o2) -> o1.getTeam() - o2.getTeam());

        int lastTeam = !allMatchWorkers.isEmpty() ? allMatchWorkers.get(0).getTeam() : 0;
        for (MatchWorker matchWorker : allMatchWorkers) {
            if (team.isEmpty() || lastTeam == matchWorker.getTeam()) {
                team.add(matchWorker.getWorker());
                lastTeam = matchWorker.getTeam();
            } else {
                team = new ArrayList<>();
                teams.add(team);
                team.add(matchWorker.getWorker());
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

    public String getMatchString(Match match) {
        return getMatchString(getTeams(match), match.getRules(), match.getFinish());
    }

    public String getMatchString(SegmentView segment) {
        return getMatchString(segment.getTeams(), segment.getRules(), segment.getFinish());
    }

    private String getMatchString(List<List<Worker>> teams, MatchRule rules, MatchFinishes finish) {

        String string = new String();

        if (teams.size() > 1) {

            if (isHandicapMatch(teams)) {
                string += "Handicap Match\n";

            } else if (rules.equals(MatchRules.DEFAULT)) {

                if (teams.size() == 2) {

                    switch (teams.get(0).size()) {
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
                            break;
                    }

                } else {
                    string += teams.size() + "-Way Match\n";
                }
            } else {
                string += rules.description() + " Match\n";
            }

            for (int t = 0; t < teams.size(); t++) {
                List<Worker> team = teams.get(t);

                for (int i = 0; i < team.size(); i++) {
                    string += team.get(i).getShortName();

                    if (team.size() > 1 && i < team.size() - 1) {
                        string += "/";
                    }

                }

                if (t == 0 && !string.isEmpty()) {
                    string += " def. ";

                } else if (t < teams.size() - 1 && !string.isEmpty()) {
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

    private boolean isHandicapMatch(List<List<Worker>> teams) {
        boolean handicap = false;

        int size = teams.get(0).size();
        for (List<Worker> team : teams) {
            if (team.size() != size && !team.isEmpty()) {
                handicap = true;
                break;

            }
        }
        return handicap;
    }

}
