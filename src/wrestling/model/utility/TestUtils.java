package wrestling.model.utility;

import java.util.ArrayList;
import java.util.List;
import wrestling.model.Event;
import wrestling.model.MatchFinishes;
import wrestling.model.MatchRules;
import wrestling.model.Worker;
import wrestling.model.modelView.EventView;
import wrestling.model.modelView.SegmentView;
import wrestling.model.modelView.SegmentTeam;
import wrestling.model.segmentEnum.OutcomeType;
import wrestling.model.segmentEnum.TeamType;

public final class TestUtils {

    public static EventView testEventView(Event event, List<Worker> roster, boolean addImages) {

        if (addImages) {
            String imagePath = "worker%03d.jpg";
            int imagesAvailable = 8;
            for (int i = 1; i <= imagesAvailable; i++) {
                roster.get(i - 1).setImageString(String.format(imagePath, i));
            }
        }

        List<SegmentView> testSegments = new ArrayList<>();
        for (int teamSize = 1; teamSize < 10; teamSize++) {
            testSegments.add(testSegment(2, teamSize, roster));
            testSegments.add(testSegment(4, teamSize, roster));
        }

        int[] numbers = new int[]{3, 4, 5, 6, 10, 20, 30, 40};
        for (int n = 0; n < numbers.length; n++) {

            testSegments.add(testSegment(numbers[n], 1, roster));
        }

        return new EventView(event, testSegments);

    }

    private static SegmentView testSegment(int numberOfTeams, int teamSize, List<Worker> roster) {
        SegmentView segmentView = new SegmentView();
        List<SegmentTeam> teams = new ArrayList<>();

        int currentWorkerIndex = 0;

        for (int i = 0; i < numberOfTeams; i++) {
            SegmentTeam team = new SegmentTeam();

            for (int x = 0; x < teamSize; x++) {
                team.getWorkers().add(roster.get(currentWorkerIndex));
                currentWorkerIndex++;
            }

            if (i == 0) {
                team.setOutcome(OutcomeType.WINNER);
            } else {
                team.setOutcome(OutcomeType.LOSER);
            }

            teams.add(team);
        }
        segmentView.setTeams(teams);
        segmentView.setRules(MatchRules.DEFAULT);
        segmentView.setFinish(MatchFinishes.CLEAN);
        return segmentView;

    }

}
