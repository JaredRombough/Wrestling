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
import wrestling.model.Worker;
import wrestling.model.modelView.SegmentView;
import wrestling.view.utility.Screen;
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
    private ScrollPane scrollPane;

    private SegmentView segmentView;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        flowPane.setAlignment(Pos.TOP_CENTER);
        flowPane.setHgap(10);
        flowPane.setVgap(10);
    }

    @Override
    public void setCurrent(Object obj) {
        if (obj instanceof SegmentView) {
            this.segmentView = (SegmentView) obj;
            updateLabels();
        }
    }

    @Override
    public void updateLabels() {

        if (segmentView != null) {
            segmentTitle.setText(gameController.getMatchManager().getMatchTitle(segmentView));
            populateView();
        }

    }

    private void populateView() {

        flowPane.getChildren().clear();
        for (List<Worker> team : segmentView.getTeams()) {
            List<Screen> workerCards = new ArrayList<>();
            for (Worker worker : team) {
                Screen card = ViewUtils.loadScreenFromResource(ScreenCode.RESULTS_CARD, mainApp, gameController);
                card.controller.setCurrent(worker);
                workerCards.add(card);
            }
            int maxColumns = getMaxColumns(workerCards.get(0).pane.getBoundsInParent().getWidth());
            GridPane teamCard = teamCard(workerCards, maxColumns);
            flowPane.getChildren().add(teamCard);
            if (segmentView.getTeams().indexOf(team) < segmentView.getTeams().size() - 1) {
                Screen intersertial = ViewUtils.loadScreenFromResource(ScreenCode.RESULTS_CARD, mainApp, gameController);
                intersertial.controller.setCurrent("versus");
                if (team.size() > maxColumns / 2) {
                    intersertial.pane.setPrefSize(teamCard.getBoundsInParent().getWidth(), 0.0);
                }
                flowPane.getChildren().add(intersertial.pane);
            }

        }

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
