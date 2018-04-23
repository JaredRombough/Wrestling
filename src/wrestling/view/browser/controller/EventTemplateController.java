package wrestling.view.browser.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import wrestling.model.EventTemplate;
import wrestling.model.utility.ModelUtils;
import wrestling.view.utility.interfaces.ControllerBase;

public class EventTemplateController extends ControllerBase implements Initializable {

    private EventTemplate eventTemplate;

    @FXML
    private Label nameLabel;

    @FXML
    private Label nextEventLabel;

    @FXML
    private Label durationLabel;

    @FXML
    private Label frequencyLabel;

    @FXML
    private Label broadcastLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @Override
    public void setCurrent(Object obj) {
        if (obj instanceof EventTemplate) {
            this.eventTemplate = (EventTemplate) obj;
            updateLabels();
        }
    }

    @Override
    public void updateLabels() {
        if (eventTemplate != null) {
            nameLabel.setText(eventTemplate.toString());
            nextEventLabel.setText(ModelUtils.dateString(eventTemplate.getNextDate()));
            durationLabel.setText(ModelUtils.timeString(eventTemplate.getDefaultDuration()));
            frequencyLabel.setText(eventTemplate.getEventFrequency().toString());
            broadcastLabel.setText(eventTemplate.getEventBroadcast().toString());

        }

    }

}
