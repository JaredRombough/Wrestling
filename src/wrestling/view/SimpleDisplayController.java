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
import wrestling.model.GameController;
import wrestling.model.Title;
import wrestling.model.Worker;

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

    private GameController gc;

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
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < gc.getDirtSheet().getReports().size(); i++) {
                if (gc.getDirtSheet().getReports().get(i).getEventArchive() != null
                        && gc.getDirtSheet().getReports().get(i).getEventArchive().equals(ea)) {
                    sb.append(gc.getDirtSheet().getReports().get(i).toString());
                    sb.append("\n");
                }
            }
            newText = sb.toString();
        } else if (obj instanceof Title) {
            Title title = (Title) obj;
            newText += title.getWorkers() + " Day " + title.getDayWon() + " to today";
            newText += "\n";
            newText += title.getTitleHistory();
        } else if (obj instanceof Worker) {
            Worker w = (Worker) obj;
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < gc.getDirtSheet().getReports().size(); i++) {
                if (gc.getDirtSheet().getReports().get(i).getWorkers() != null
                        && gc.getDirtSheet().getReports().get(i).getWorkers().contains(w)) {
                    sb.append(gc.getDirtSheet().getReports().get(i).toString());
                    sb.append("\n");
                }
            }
            newText = sb.toString();
        }
        Text text = new Text();
        text.setText(newText);

        scrollPane.setContent(text);

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    /**
     * @param ds the ds to set
     */
    public void setGameController(GameController gc) {
        this.gc = gc;
    }

}
