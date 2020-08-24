package openwrestling.view;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.text.Text;
import openwrestling.manager.EventManager;
import openwrestling.model.gameObjects.Event;
import openwrestling.model.gameObjects.Segment;
import openwrestling.model.gameObjects.Title;
import openwrestling.model.gameObjects.Worker;
import openwrestling.view.utility.interfaces.ControllerBase;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class SimpleDisplayController extends ControllerBase implements Initializable {

    private Object obj;
    private EventManager eventManager;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private Text displayTitle;

    @FXML
    private Button actionButton;

    private String defaultTitle;

    @Override
    public void initializeMore() {
        this.eventManager = gameController.getEventManager();
    }

    @Override
    public void setCurrent(Object obj) {
        this.obj = obj;
        updateLabels();
    }

    public void setDefaultTitle(String string) {
        defaultTitle = string;
    }

    @Override
    public void updateLabels() {
        displayTitle.setText(obj != null ? obj.toString() : defaultTitle);

        String newText;

        if (obj instanceof Event) {
            newText = eventManager.generateSummaryString((Event) obj);
        } else if (obj instanceof Title) {
            newText = gameController.getTitleManager().getTitleReignStrings(((Title) obj));
        } else if (obj instanceof Worker) {
            displayTitle.setText("");
            List<Segment> recentSegments = gameController.getSegmentManager().getRecentSegments((Worker) obj);
            StringBuilder sb = new StringBuilder();
            recentSegments.forEach(segment -> {
                sb.append(gameController.getSegmentManager().getSegmentStringForWorkerOverview(segment, segment.getEvent()));
                sb.append("\n");
            });
            newText = sb.toString();
        } else {
            newText = obj == null ? "" : obj.toString();
        }

        Text text = new Text();
        text.setText(newText);
        text.wrappingWidthProperty().bind(scrollPane.widthProperty().subtract(20));

        scrollPane.setContent(text);

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        defaultTitle = "";
        getActionButton().setVisible(false);
    }

    /**
     * @return the actionButton
     */
    public Button getActionButton() {
        return actionButton;
    }

}
