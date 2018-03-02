package wrestling.view.event;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import wrestling.model.Worker;
import wrestling.view.utility.Screen;
import wrestling.view.utility.ScreenCode;
import wrestling.view.utility.ViewUtils;
import wrestling.view.utility.interfaces.ControllerBase;

public class AnglePaneController extends ControllerBase implements Initializable {

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private VBox vBox;

    private Screen teamPane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    @Override
    public void initializeMore() {
        teamPane = ViewUtils.loadScreenFromResource(ScreenCode.TEAM_PANE, mainApp, gameController);
        vBox.getChildren().add(teamPane.pane);
    }

    public void removeWorker(Worker worker) {
        ((TeamPaneController) teamPane.controller).removeWorker(worker);
    }

    public List<Worker> getWorkers() {
        return ((TeamPaneController) teamPane.controller).getWorkers();
    }

    public void setTeamNameLabel(String teamName) {
        ((TeamPaneController) teamPane.controller).setTeamNameLabel(teamName);
    }

    public void setTeamNumber(int teamNumber) {
        ((TeamPaneController) teamPane.controller).setTeamNumber(teamNumber);
    }

    public void setEventScreenController(EventScreenController eventScrenController) {
        ((TeamPaneController) teamPane.controller).setEventScreenController(eventScrenController);
    }

    public void setSegmentPaneController(SegmentPaneController segmentPaneController) {
        ((TeamPaneController) teamPane.controller).setSegmentPaneController(segmentPaneController);
    }

}
