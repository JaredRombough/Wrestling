package wrestling.view;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import wrestling.model.EventArchive;
import wrestling.model.Title;

/*
basic anchor pane for displaying a string on a label, used by browser
 */
public class SimpleDisplayController extends Controller implements Initializable {

    private Object obj;

    private Text text;

    @FXML
    private ScrollPane scrollPane;

    @Override
    void initializeMore() {

        text = new Text();

        scrollPane.setContent(text);

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

        text.setText(newText);

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

}
