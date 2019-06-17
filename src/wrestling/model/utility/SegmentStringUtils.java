/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wrestling.model.utility;

import java.util.ArrayList;
import java.util.List;
import wrestling.model.AngleParams;
import wrestling.model.Match;
import wrestling.model.modelView.SegmentTeam;
import wrestling.model.modelView.SegmentView;
import wrestling.model.modelView.WorkerGroup;
import wrestling.model.modelView.WorkerView;
import wrestling.model.segmentEnum.MatchRule;
import wrestling.model.segmentEnum.TeamType;

public final class SegmentStringUtils {

    public static String getMatchTitle(SegmentView segmentView) {
        List<SegmentTeam> teams = segmentView.getTeams();
        MatchRule rules = ((Match) segmentView.getSegment()).getSegmentParams().getMatchRule();

        String string = "";

        if (segmentView.getWorkers().isEmpty()) {
            return "Empty Match";
        }

        if (!segmentView.getTitleViews().isEmpty()) {
            string += ModelUtils.andItemsLongName(segmentView.getTitleViews());
            string += " ";
        }

        if (SegmentUtils.isHandicapMatch(segmentView)) {
            string += "Handicap";

        } else if (rules.equals(MatchRule.DEFAULT) && string.isEmpty()) {

            int teamsSize = segmentView.getMatchParticipantTeams().size();

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

    public static String getOfferString(SegmentView segmentView) {
        String string = "";
        switch (segmentView.getSegment().getSegmentParams().getJoinTeamType()) {
            case TAG_TEAM:
                string += " to form a new tag team";
                break;
            case NEW_STABLE:
                string += " to form a new stable";
                break;
            default:
                WorkerGroup stable = segmentView.getSegment().getSegmentParams().getJoinStable();
                if (stable != null) {
                    if (string.contains(stable.getName())) {
                        string += " to join them";
                    } else {
                        string += " to join " + segmentView.getSegment().getSegmentParams().getJoinStable().getName();
                    }
                }
        }

        return string += getResponseString(segmentView.getTeams(TeamType.OFFEREE), "offer");
    }

    public static String getChallengeString(SegmentView segmentView) {
        String string = " to a match";
        AngleParams params = (AngleParams) segmentView.getSegment().getSegmentParams();
        switch (params.getShowType()) {
            case TONIGHT:
                string += " tonight";
                break;
            default:
                string += String.format(" at %s", params.getChallengeSegment().getEventTemplate().getLongName());
                break;
        }

        return string += getResponseString(segmentView.getTeams(TeamType.CHALLENGED), "challenge");
    }

    private static String getResponseString(List<SegmentTeam> teams, String keyword) {
        String string = "";
        List<WorkerView> yes = new ArrayList<>();
        List<WorkerView> no = new ArrayList<>();
        List<WorkerView> push = new ArrayList<>();

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
