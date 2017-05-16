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
import wrestling.model.EventArchive;
import wrestling.model.Title;

/*
basic anchor pane for displaying a string on a label, used by browser
 */
public class SimpleDisplayController extends Controller implements Initializable {

    private Object obj;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private TextArea textArea;

    @FXML
    private TextFlow textFlow;

    @FXML
    private StackPane stackPane;

    @Override
    void initializeMore() {

    }

    @Override
    void setCurrent(Object obj) {
        this.obj = obj;
        updateLabels();
    }

    @Override
    void updateLabels() {

        String newText = "";

        //call the appropriate method based on object type
        if (obj instanceof EventArchive) {
            EventArchive ea = (EventArchive) obj;
            newText += ea.getSummary();
        } else if (obj instanceof Title) {
            Title title = (Title) obj;
            newText += title.getWorkers() + " Day " + title.getDayWon() + " to today";
            newText += "\n";
            newText += title.getTitleHistory();
        }
        Text text = new Text();
        text.setText(newText);

        scrollPane.setContent(text);

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

}
