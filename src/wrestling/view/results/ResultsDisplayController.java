package wrestling.view.results;

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
import wrestling.model.Match;
import wrestling.model.Worker;
import wrestling.model.interfaces.Segment;
import wrestling.model.modelView.EventView;
import wrestling.model.modelView.SegmentView;
import wrestling.model.modelView.SegmentTeam;
import wrestling.model.utility.ModelUtils;
import wrestling.model.segmentEnum.TeamType;
import wrestling.view.utility.Screen;
import wrestling.view.utility.ScreenCode;
import wrestling.view.utility.ViewUtils;
import wrestling.view.utility.interfaces.ControllerBase;

public class ResultsDisplayController extends ControllerBase implements Initializable {

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private AnchorPane additionalInfo;

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
        flowPane.setAlignment(Pos.TOP_CENTER);
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
        };

    }

    @Override
    public void updateLabels() {

        if (segmentView != null) {
            segmentTitle.setText(gameController.getMatchManager().getMatchTitle(segmentView));
            Segment segment = segmentView.getSegment();
            summaryText.setText((segment instanceof Match ? "Match" : "Segment")
                    + String.format(" rating: %d", segment.getRating()));
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
        List<SegmentTeam> defaultTeams = segmentView.getTeams(TeamType.DEFAULT);

        for (SegmentTeam team : defaultTeams) {
            List<Screen> workerCards = new ArrayList<>();
            for (Worker worker : team.getWorkers()) {
                Screen card = ViewUtils.loadScreenFromResource(ScreenCode.RESULTS_CARD, mainApp, gameController);
                card.controller.setCurrent(worker);
                workerCards.add(card);
            }
            if (!workerCards.isEmpty()) {
                int maxColumns = getMaxColumns(workerCards.get(0).pane.getBoundsInParent().getWidth());
                GridPane teamCard = teamCard(workerCards, maxColumns);
                flowPane.getChildren().add(teamCard);
                if (defaultTeams.size() > 1 && defaultTeams.indexOf(team) < defaultTeams.size() - 1) {
                    addIntersertial(team.getWorkers().size(), teamCard.getBoundsInParent().getWidth(), maxColumns);
                }
            }

        }

        List<SegmentTeam> interferenceTeams = segmentView.getTeams(TeamType.INTERFERENCE);
        if (!interferenceTeams.isEmpty()) {
            addInterferenceNote(interferenceTeams);
        }

    }

    private void addInterferenceNote(List<SegmentTeam> interferenceTeams) {
        StringBuilder sb = new StringBuilder();
        for (SegmentTeam team : interferenceTeams) {
            sb.append(String.format("%s interfered %s the match, attacking %s %s\n",
                    ModelUtils.slashNames(team.getWorkers()),
                    team.getTiming().result(),
                    team.getTarget().toString(),
                    team.getSuccess().result()));
        }
        Text text = new Text(sb.toString());
        additionalInfo.getChildren().add(text);
    }

    private void addIntersertial(int teamSize, double cardWidth, int maxColumns) {
        Screen intersertial = ViewUtils.loadScreenFromResource(ScreenCode.RESULTS_CARD, mainApp, gameController);
        intersertial.controller.setCurrent("versus");
        if (teamSize > maxColumns / 2) {
            intersertial.pane.setPrefSize(cardWidth, 0.0);
        }
        flowPane.getChildren().add(intersertial.pane);
    }

    private int getMaxColumns(double cardWidth) {
        int emptyColumnsPadding = 3;
        return (int) Math.round(mainApp.getCurrentStageWidth() / cardWidth) - emptyColumnsPadding;
    }

    private GridPane teamCard(List<Screen> workerCards, int maxColumns) {

        if (workerCards.isEmpty()) {
            return null;
        }

        GridPane gridPane = new GridPane();
        int row = 0;
        int column = 0;
        for (Screen card : workerCards) {
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
