package wrestling.view;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import wrestling.model.EventArchive;
import wrestling.model.Title;

/*
basic anchor pane for displaying a string on a label, used by browser
*/
public class SimpleDisplayController extends Controller implements Initializable {

    private Object obj;

    @FXML
    private Label label;

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
            newText += title.getWorker();
        }
        label.setText(newText);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

}
