/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wrestling.model.utility;

import java.util.List;
import wrestling.model.Match;
import wrestling.model.modelView.SegmentTeam;
import wrestling.model.modelView.SegmentView;
import wrestling.model.segmentEnum.MatchRule;

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
    
    

}
