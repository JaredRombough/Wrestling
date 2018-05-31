package wrestling.view.browser.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import wrestling.model.EventTemplate;
import wrestling.model.Promotion;
import wrestling.model.segmentEnum.EventVenueSize;
import wrestling.model.utility.ModelUtils;
import wrestling.view.utility.ScreenCode;
import wrestling.view.utility.ViewUtils;
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

    @FXML
    private Label remainingLabel;

    @FXML
    private AnchorPane venueSizeAnchorPane;

    @FXML
    private Button calendarButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        calendarButton.setOnAction(e -> {
            mainApp.show(ScreenCode.CALENDAR,
                    gameController.getEventManager().getNextEvent(eventTemplate));

        });

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

            ComboBox comboBox = ViewUtils.updatePlayerComboBox(
                    venueSizeAnchorPane,
                    eventTemplate.getPromotion().equals(playerPromotion()),
                    EventVenueSize.values(),
                    eventTemplate.getEventVenueSize());

            comboBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<EventVenueSize>() {
                @Override
                public void changed(ObservableValue<? extends EventVenueSize> observable, EventVenueSize oldValue, EventVenueSize newValue) {
                    eventTemplate.setEventVenueSize(newValue);
                }
            });

            nameLabel.setText(eventTemplate.toString());
            nextEventLabel.setText(ModelUtils.dateString(eventTemplate.getNextDate()));
            durationLabel.setText(ModelUtils.timeString(eventTemplate.getDefaultDuration()));
            frequencyLabel.setText(eventTemplate.getEventFrequency().toString());
            broadcastLabel.setText(eventTemplate.getEventBroadcast().toString());
            remainingLabel.setText(Integer.toString(eventTemplate.getEventsLeft()));
        }

    }

}
