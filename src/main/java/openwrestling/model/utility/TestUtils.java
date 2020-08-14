package openwrestling.model.utility;

import openwrestling.model.gameObjects.Event;
import openwrestling.model.gameObjects.Segment;
import openwrestling.model.gameObjects.SegmentTeam;
import openwrestling.model.gameObjects.Worker;
import openwrestling.model.segmentEnum.OutcomeType;
import openwrestling.model.segmentEnum.SegmentType;

import java.util.ArrayList;
import java.util.List;

public final class TestUtils {

    public static Event testEventView(Event event, List<Worker> roster, boolean addImages) {

        if (addImages) {
            String imagePath = "worker%02d.jpg";
            int imagesAvailable = 8;
            for (int i = 1; i <= imagesAvailable; i++) {
                String path = String.format(imagePath, i);
                roster.get(i - 1).setImageString(path);
            }
        }

        List<Segment> testSegments = new ArrayList<>();
        for (int teamSize = 1; teamSize < 10; teamSize++) {
            testSegments.add(testSegment(2, teamSize, roster));
            testSegments.add(testSegment(4, teamSize, roster));
        }

        int[] numbers = new int[]{3, 4, 5, 6, 10, 20, 30, 40};
        for (int n = 0; n < numbers.length; n++) {

            testSegments.add(testSegment(numbers[n], 1, roster));
        }
        event.setSegments(testSegments);

        return event;

    }

    private static Segment testSegment(int numberOfTeams, int teamSize, List<Worker> roster) {
        Segment segment = new Segment(SegmentType.MATCH);
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
        segment.setSegmentTeams(teams);
        return segment;

    }

}
