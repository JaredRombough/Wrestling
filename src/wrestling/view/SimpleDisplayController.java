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
import wrestling.model.dirt.EventArchive;
import wrestling.model.controller.GameController;
import wrestling.model.Title;
import wrestling.model.Worker;
import wrestling.model.dirt.Dirt;
import wrestling.model.dirt.SegmentRecord;
import wrestling.model.dirt.TitleRecord;


/*
basic anchor pane for displaying a string on a label, used by browser
 */
public class SimpleDisplayController extends ControllerBase implements Initializable {

    private Object obj;

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
    public void setCurrent(Object obj) {
        this.obj = obj;
        updateLabels();
    }

    @Override
    public void updateLabels() {

        String newText = "";

        //call the appropriate method based on object type
        if (obj instanceof EventArchive) {
            StringBuilder sb = new StringBuilder();
            for (Dirt dirt : gameController.getDirtSheet().getReports()) {
                if (dirt instanceof SegmentRecord
                        && ((SegmentRecord) dirt).getEventArchive().equals(obj)) {
                    sb.append(dirt.toString());
                    sb.append("\n");
                }
            }
            newText = sb.toString();
        } else if (obj instanceof Title) {
            StringBuilder sb = new StringBuilder();
            for (Dirt dirt : gameController.getDirtSheet().getReports()) {
                if (dirt instanceof TitleRecord
                        && ((TitleRecord) dirt).getTitle().equals(obj)) {
                    sb.append(dirt.toString());
                    sb.append("\n");
                }
            }
            newText = sb.toString();
        } else if (obj instanceof Worker) {
            Worker w = (Worker) obj;
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < gameController.getDirtSheet().getReports().size(); i++) {
                if (gameController.getDirtSheet().getReports().get(i).getWorkers() != null
                        && gameController.getDirtSheet().getReports().get(i).getWorkers().contains(w)) {
                    sb.append(gameController.getDirtSheet().getReports().get(i).toString());
                    sb.append("\n");
                }
            }
            newText = sb.toString();
        }

        if (obj != null && displayTitle != null && !obj.toString().equals(displayTitle.getText())) {
            displayTitle.setText(obj.toString());
        }

        Text text = new Text();
        text.setText(newText);

        scrollPane.setContent(text);

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

}
