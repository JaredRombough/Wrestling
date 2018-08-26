package wrestling.view;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.text.Text;
import wrestling.model.Event;
import wrestling.model.manager.EventManager;
import wrestling.model.modelView.TitleView;
import wrestling.model.modelView.WorkerView;
import wrestling.view.utility.interfaces.ControllerBase;


/*
basic anchor pane for displaying a string on a label, used by browser
 */
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

        String newText = "";

        //call the appropriate method based on object type
        if (obj instanceof Event) {
            newText = eventManager.generateSummaryString((Event) obj);
        } else if (obj instanceof TitleView) {
            newText = gameController.getTitleManager().getTitleReignStrings(((TitleView) obj).getTitle());
        } else if (obj instanceof WorkerView) {
            newText = gameController.getSegmentManager().getMatchStringForMonths((WorkerView) obj, 3);
        } else {
            newText = obj == null ? "" : obj.toString();
        }

        displayTitle.setText(obj != null ? obj.toString() : defaultTitle);

        Text text = new Text();
        text.setText(newText);

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
