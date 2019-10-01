package openwrestling.view;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.text.Text;
import openwrestling.model.Event;
import openwrestling.model.NewsItem;
import openwrestling.manager.EventManager;
import openwrestling.model.gameObjects.Title;
import openwrestling.model.gameObjects.Worker;
import openwrestling.view.utility.interfaces.ControllerBase;

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
            List<NewsItem> newsItems = gameController.getNewsManager().getNews(obj, gameController.getDateManager().today().minusMonths(12), gameController.getDateManager().today());
            StringBuilder sb = new StringBuilder();
            newsItems.forEach(item -> {
                sb.append(item.getSummary());
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
