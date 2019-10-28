package openwrestling.view.results.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import openwrestling.model.gameObjects.Event;
import openwrestling.model.gameObjects.Worker;
import openwrestling.model.modelView.SegmentTeam;
import openwrestling.model.modelView.Segment;
import openwrestling.model.segmentEnum.SegmentType;
import openwrestling.model.segmentEnum.TeamType;
import openwrestling.model.segmentEnum.TimingType;
import openwrestling.model.utility.ModelUtils;
import openwrestling.view.utility.GameScreen;
import openwrestling.view.utility.ScreenCode;
import openwrestling.view.utility.ViewUtils;
import openwrestling.view.utility.interfaces.ControllerBase;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ResultsDisplayController extends ControllerBase implements Initializable {

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private FlowPane flowPane;

    @FXML
    private Text segmentTitle;

    @FXML
    private Text summaryText;

    @FXML
    private ScrollPane scrollPane;

    private Segment segment;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        flowPane.setAlignment(Pos.CENTER);
        flowPane.setHgap(10);
        flowPane.setVgap(10);
        summaryText.setText("");
        segmentTitle.setText("");
    }

    @Override
    public void setCurrent(Object obj) {
        if (obj instanceof Segment) {
            this.segment = (Segment) obj;
            updateLabels();
        } else if (obj instanceof Event) {
            showEventSummary(((Event) obj));
        }
    }

    @Override
    public void updateLabels() {

        if (segment != null) {
            segmentTitle.setText(gameController.getSegmentManager().getSegmentTitle(segment));
            StringBuilder sb = new StringBuilder();

            for (SegmentTeam team : segment.getTeams(TeamType.INTERFERENCE)) {
                if (team.getTiming().equals(TimingType.BEFORE)) {
                    sb.append(getInterferenceNote(team));
                }
            }

            sb.append(gameController.getSegmentManager().getSegmentString(segment));
            sb.append("\n");

            for (SegmentTeam team : segment.getTeams(TeamType.INTERFERENCE)) {
                if (team.getTiming().equals(TimingType.DURING)) {
                    sb.append(getInterferenceNote(team));
                }
            }

            for (SegmentTeam team : segment.getTeams(TeamType.INTERFERENCE)) {
                if (team.getTiming().equals(TimingType.AFTER)) {
                    sb.append(getInterferenceNote(team));
                }
            }

            boolean isMatch = segment.getSegmentType().equals(SegmentType.MATCH);

            sb.append(isMatch ? "Match" : "Segment");
            sb.append(String.format(" rating: %s", isMatch
                    ? ViewUtils.intToStars(segment.getWorkRating())
                    : String.format("%d%%", segment.getWorkRating())));
            sb.append("\n")
                    .append(String.format("Crowd reaction: %d%%", segment.getCrowdRating()));
            if (isMatch) {
                sb.append("\n");
                sb.append((String.format("Referee: %s", segment.getReferee() != null ? segment.getReferee().toString() : " None")));
            }
            sb.append("\n");
            //TODO
//            if (!segmentView.getBroadcastTeam().isEmpty()) {
//                sb.append("\n");
//                sb.append(String.format("Broadcast Team: %s", ModelUtils.slashNames(segmentView.getBroadcastTeam())));
//            }

            summaryText.setText(sb.toString());
            populateView();
        }
    }

    private void showEventSummary(Event event) {
        segmentTitle.setText("Event Summary");
        Text text = new Text();
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Attendance: %d\n", event.getAttendance()));
        sb.append(String.format("Gate: %d\n", event.getGate()));
        sb.append(String.format("Costs: %d\n", event.getCost()));
        text.setText(sb.toString());
        flowPane.getChildren().add(text);

    }

    private void populateView() {

        flowPane.getChildren().clear();

        if (segment.getSegmentType().equals(SegmentType.MATCH)) {
            populateMatch();
        } else {
            populateAngle();
        }
    }

    private void populateMatch() {
        List<SegmentTeam> defaultTeams = segment.getMatchParticipantTeams();
        for (SegmentTeam team : defaultTeams) {
            List<GameScreen> workerCards = new ArrayList<>();
            for (Worker worker : team.getWorkers()) {
                GameScreen card = ViewUtils.loadScreenFromFXML(ScreenCode.RESULTS_CARD, mainApp, gameController);
                card.controller.setCurrent(worker);
                workerCards.add(card);
            }
            if (!workerCards.isEmpty()) {
                int maxColumns = getMaxColumns(workerCards.get(0).pane.getBoundsInParent().getWidth());
                GridPane teamCard = teamCard(workerCards, maxColumns);
                flowPane.getChildren().add(teamCard);
                if (defaultTeams.size() > 1 && defaultTeams.indexOf(team) < defaultTeams.size() - 1) {
                    addIntersertial(team, teamCard.getBoundsInParent().getWidth(), maxColumns);
                }
            }
        }
    }

    private void populateAngle() {
        List<SegmentTeam> teams = segment.getTeams();
        for (SegmentTeam team : teams) {
            List<GameScreen> workerCards = new ArrayList<>();
            for (Worker worker : team.getWorkers()) {
                GameScreen card = ViewUtils.loadScreenFromFXML(ScreenCode.RESULTS_CARD, mainApp, gameController);
                card.controller.setCurrent(worker);
                workerCards.add(card);
            }
            if (!workerCards.isEmpty()) {
                int maxColumns = getMaxColumns(workerCards.get(0).pane.getBoundsInParent().getWidth());
                GridPane teamCard = teamCard(workerCards, maxColumns);
                flowPane.getChildren().add(teamCard);
            }
        }
    }

    private String getInterferenceNote(SegmentTeam team) {
        String segmentTypeString = segment.getSegmentType().equals(SegmentType.MATCH)
                ? "match"
                : gameController.getSegmentManager().getSegmentTitle(segment);

        return String.format("%s interfered %s the %s, attacking %s %s\n",
                ModelUtils.slashNames(team.getWorkers()),
                team.getTiming().result(),
                segmentTypeString,
                team.getTarget().toString(),
                team.getSuccess().result());
    }

    private void addIntersertial(SegmentTeam team, double cardWidth, int maxColumns) {
        GameScreen intersertial = ViewUtils.loadScreenFromFXML(ScreenCode.RESULTS_CARD, mainApp, gameController);
        String text = matchIntersertialString(team);
        intersertial.controller.setCurrent(text);
        if (team.getWorkers().size() > maxColumns / 2) {
            intersertial.pane.setPrefSize(cardWidth, 0.0);
        }
        flowPane.getChildren().add(intersertial.pane);
    }

    private String matchIntersertialString(SegmentTeam team) {
        String text = "";
        switch (team.getType()) {
            case DRAW:
                text = "drew";
                break;
            case WINNER:
                if (team.getWorkers().size() > 1) {
                    text = "defeat";
                } else {
                    text = "defeats";
                }
                break;
            case LOSER:
                text = "and";
                break;
            default:
                text = "versus";
                break;
        }
        return text;
    }

    private int getMaxColumns(double cardWidth) {
        int emptyColumnsPadding = 3;
        return (int) Math.round(mainApp.getCurrentStageWidth() / cardWidth) - emptyColumnsPadding;
    }

    private GridPane teamCard(List<GameScreen> workerCards, int maxColumns) {

        if (workerCards.isEmpty()) {
            return null;
        }

        GridPane gridPane = new GridPane();
        int row = 0;
        int column = 0;
        for (GameScreen card : workerCards) {
            gridPane.add(card.pane, column, row);
            column++;
            if (column > maxColumns) {
                column = 0;
                row++;
            }
        }

        return gridPane;

    }

}
