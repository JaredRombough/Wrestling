package wrestling.view;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import wrestling.model.Event;
import wrestling.model.Title;
import wrestling.model.Worker;
import wrestling.model.manager.EventManager;


/*
basic anchor pane for displaying a string on a label, used by browser
 */
public class SimpleDisplayController extends ControllerBase implements Initializable {

    private Object obj;
    private EventManager eventManager;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private TextArea textArea;

    @FXML
    private TextFlow textFlow;

    @FXML
    private Text displayTitle;

    @FXML
    private StackPane stackPane;

    @Override
    public void initializeMore() {
        this.eventManager = gameController.getEventManager();
    }

    @Override
    public void setCurrent(Object obj) {
        this.obj = obj;
        updateLabels();
    }

    @Override
    public void updateLabels() {

        String newText = "";

        //call the appropriate method based on object type
        if (obj instanceof Event) {
            newText = eventManager.generateSummaryString((Event) obj);
        } else if (obj instanceof Title) {
            newText = "insert title info here";
        } else if (obj instanceof Worker) {
            newText = gameController.getMatchManager().getMatchStringForMonths((Worker) obj, 3);
        }

        displayTitle.setText(obj != null ? obj.toString() : "");

        Text text = new Text();
        text.setText(newText);

        scrollPane.setContent(text);

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

}
