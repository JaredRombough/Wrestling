package wrestling.view.results.controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import wrestling.model.Event;
import wrestling.model.modelView.WorkerView;
import wrestling.model.interfaces.Segment;
import wrestling.model.modelView.EventView;
import wrestling.model.modelView.SegmentTeam;
import wrestling.model.modelView.SegmentView;
import wrestling.model.segmentEnum.SegmentType;
import wrestling.model.segmentEnum.TeamType;
import wrestling.model.segmentEnum.TimingType;
import wrestling.model.utility.ModelUtils;
import wrestling.view.utility.GameScreen;
import wrestling.view.utility.ScreenCode;
import wrestling.view.utility.ViewUtils;
import wrestling.view.utility.interfaces.ControllerBase;

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

    private SegmentView segmentView;

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
        if (obj instanceof SegmentView) {
            this.segmentView = (SegmentView) obj;
            updateLabels();
        } else if (obj instanceof EventView) {
            showEventSummary(((EventView) obj).getEvent());
        }
    }

    @Override
    public void updateLabels() {

        if (segmentView != null) {
            segmentTitle.setText(gameController.getSegmentManager().getSegmentTitle(segmentView));
            Segment segment = segmentView.getSegment();
            StringBuilder sb = new StringBuilder();

            for (SegmentTeam team : segmentView.getTeams(TeamType.INTERFERENCE)) {
                if (team.getTiming().equals(TimingType.BEFORE)) {
                    sb.append(getInterferenceNote(team));
                }
            }

            sb.append(gameController.getSegmentManager().getSegmentString(segmentView));
            sb.append("\n");

            for (SegmentTeam team : segmentView.getTeams(TeamType.INTERFERENCE)) {
                if (team.getTiming().equals(TimingType.DURING)) {
                    sb.append(getInterferenceNote(team));
                }
            }

            for (SegmentTeam team : segmentView.getTeams(TeamType.INTERFERENCE)) {
                if (team.getTiming().equals(TimingType.AFTER)) {
                    sb.append(getInterferenceNote(team));
                }
            }

            boolean isMatch = segmentView.getSegmentType().equals(SegmentType.MATCH);

            sb.append(isMatch ? "Match" : "Segment");
            sb.append(String.format(" rating: %s", isMatch
                    ? ViewUtils.intToStars(segment.getWorkRating())
                    : String.format("%d%%", segment.getWorkRating())));
            sb.append("\n")
                    .append(String.format(" crowd reaction: %d%%", segment.getCrowdRating()));
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

        if (segmentView.getSegmentType().equals(SegmentType.MATCH)) {
            populateMatch();
        } else {
            populateAngle();
        }
    }

    private void populateMatch() {
        List<SegmentTeam> defaultTeams = segmentView.getMatchParticipantTeams();
        for (SegmentTeam team : defaultTeams) {
            List<GameScreen> workerCards = new ArrayList<>();
            for (WorkerView worker : team.getWorkers()) {
                GameScreen card = ViewUtils.loadScreenFromResource(ScreenCode.RESULTS_CARD, mainApp, gameController);
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
        List<SegmentTeam> teams = segmentView.getTeams();
        for (SegmentTeam team : teams) {
            List<GameScreen> workerCards = new ArrayList<>();
            for (WorkerView worker : team.getWorkers()) {
                GameScreen card = ViewUtils.loadScreenFromResource(ScreenCode.RESULTS_CARD, mainApp, gameController);
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
        String segmentTypeString = segmentView.getSegmentType().equals(SegmentType.MATCH)
                ? "match"
                : gameController.getSegmentManager().getSegmentTitle(segmentView);

        return String.format("%s interfered %s the %s, attacking %s %s\n",
                ModelUtils.slashNames(team.getWorkers()),
                team.getTiming().result(),
                segmentTypeString,
                team.getTarget().toString(),
                team.getSuccess().result());
    }

    private void addIntersertial(SegmentTeam team, double cardWidth, int maxColumns) {
        GameScreen intersertial = ViewUtils.loadScreenFromResource(ScreenCode.RESULTS_CARD, mainApp, gameController);
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
