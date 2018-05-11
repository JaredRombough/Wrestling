package wrestling.view.browser.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import wrestling.model.EventTemplate;
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
    private ComboBox<EventVenueSize> venueSizeComboBox;

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

            venueSizeAnchorPane.getChildren().clear();

            if (eventTemplate.getPromotion().equals(playerPromotion())) {
                ViewUtils.anchorRegionToParent(venueSizeAnchorPane, venueSizeComboBox);
                venueSizeComboBox.setItems(FXCollections.observableArrayList(EventVenueSize.values()));
                venueSizeComboBox.getSelectionModel().select(eventTemplate.getEventVenueSize());
            } else {
                Label label = new Label(eventTemplate.getEventVenueSize().toString());
                label.getStyleClass().add("workerStat");
                ViewUtils.anchorRegionToParent(venueSizeAnchorPane, label);

            }

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
            remainingLabel.setText(Integer.toString(eventTemplate.getEventsLeft()));
        }

    }

}
