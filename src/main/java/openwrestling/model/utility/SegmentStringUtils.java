package openwrestling.model.utility;

import openwrestling.model.gameObjects.Segment;
import openwrestling.model.gameObjects.SegmentTeam;
import openwrestling.model.gameObjects.Stable;
import openwrestling.model.gameObjects.Worker;
import openwrestling.model.segmentEnum.MatchRule;
import openwrestling.model.segmentEnum.TeamType;

import java.util.ArrayList;
import java.util.List;

public final class SegmentStringUtils {

    public static String getMatchTitle(Segment segment) {
        List<SegmentTeam> teams = segment.getTeams();
        MatchRule rules = segment.getMatchRule();

        String string = "";

        if (segment.getWorkers().isEmpty()) {
            return "Empty Match";
        }

        if (!segment.getTitles().isEmpty()) {
            string += ModelUtils.andItemsLongName(segment.getTitles());
            string += " ";
        }

        if (SegmentUtils.isHandicapMatch(segment)) {
            string += "Handicap";

        } else if (rules.equals(MatchRule.DEFAULT) && string.isEmpty()) {

            int teamsSize = segment.getMatchParticipantTeams().size();

            switch (teamsSize) {
                case 2:
                    int teamSize = teams.get(0).getWorkers().size();
                    switch (teamSize) {
                        case 1:
                            string += "Singles";
                            break;
                        case 2:
                            string += "Tag Team";
                            break;
                        case 3:
                            string += "Six Man Tag Team";
                            break;
                        case 4:
                            string += "Eight Man Tag Team";
                            break;
                        case 5:
                            string += "Ten Man Tag Team";
                            break;
                        default:
                            string += String.format("%d Man Tag Team", teamSize * 2);
                            break;
                    }
                    break;
                default:
                    string += teamsSize + "-Way";
                    break;
            }
        } else if (!rules.equals(MatchRule.DEFAULT)) {
            string += rules.description();
        }

        if (string.lastIndexOf(' ') != string.length() - 1) {
            string += " ";
        }

        string += "Match";

        return string;
    }

    public static String getOfferString(Segment segment) {
        String string = "";
        switch (segment.getJoinTeamType()) {
            case TAG_TEAM:
                string += " to form a new tag team";
                break;
            case NEW_STABLE:
                string += " to form a new stable";
                break;
            default:
                Stable stable = segment.getJoinStable();
                if (stable != null) {
                    if (string.contains(stable.getName())) {
                        string += " to join them";
                    } else {
                        string += " to join " + segment.getJoinStable().getName();
                    }
                }
        }

        return string += getResponseString(segment.getTeams(TeamType.OFFEREE), "offer");
    }

    public static String getChallengeString(Segment segment) {
        String string = " to a match";
        switch (segment.getShowType()) {
            case TONIGHT:
                string += " tonight";
                break;
            default:
                string += String.format(" at %s", segment.getChallengeSegment().getEventTemplate().getLongName());
                break;
        }

        return string += getResponseString(segment.getTeams(TeamType.CHALLENGED), "challenge");
    }

    private static String getResponseString(List<SegmentTeam> teams, String keyword) {
        String string = "";
        List<Worker> yes = new ArrayList<>();
        List<Worker> no = new ArrayList<>();
        List<Worker> push = new ArrayList<>();

        teams.forEach(team -> {
            if (null != team.getResponse()) {
                switch (team.getResponse()) {
                    case YES:
                        yes.addAll(team.getWorkers());
                        break;
                    case NO:
                        no.addAll(team.getWorkers());
                        break;
                    case PUSH:
                        push.addAll(team.getWorkers());
                        break;
                    default:
                        break;
                }
            }
        });

        if (!yes.isEmpty()) {
            if (no.isEmpty() && push.isEmpty()) {
                string += String.format(". The %s is accepted", keyword);
            } else {
                string += String.format(". %s accept%s", ModelUtils.andItemsLongName(yes), yes.size() > 1 ? "" : "s");
            }
        }
        if (!no.isEmpty()) {
            if (yes.isEmpty() && push.isEmpty()) {
                string += String.format(". The %s is rejected", keyword);
            } else {
                string += String.format(". %s decline%s", ModelUtils.andItemsLongName(no), no.size() > 1 ? "" : "s");
            }
        }
        if (!push.isEmpty()) {
            if (yes.isEmpty() && no.isEmpty()) {
                string += String.format(". The %s is considered", keyword);
            } else {
                string += String.format(". %s consider%s it", ModelUtils.andItemsLongName(push), push.size() > 1 ? "" : "s");
            }
        }
        return string;
    }

}
