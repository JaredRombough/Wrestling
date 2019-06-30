package wrestling.view.browser.controller;

import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import wrestling.model.EventTemplate;
import wrestling.model.constants.StringConstants;
import static wrestling.model.constants.StringConstants.ALL_ROSTER_SPLITS;
import static wrestling.model.constants.UIConstants.CALENDAR_ICON;
import static wrestling.model.constants.UIConstants.EDIT_ICON;
import wrestling.model.interfaces.iRosterSplit;
import wrestling.model.modelView.PromotionView;
import wrestling.model.modelView.StaffView;
import wrestling.model.modelView.WorkerGroup;
import wrestling.model.segmentEnum.BrowseMode;
import wrestling.model.segmentEnum.EventVenueSize;
import wrestling.model.utility.ModelUtils;
import wrestling.view.utility.GameScreen;
import wrestling.view.utility.ScreenCode;
import wrestling.view.utility.ViewUtils;
import static wrestling.view.utility.ViewUtils.updateRosterSplitComboBox;
import wrestling.view.utility.interfaces.ControllerBase;

public class EventTemplateController extends ControllerBase implements Initializable {

    private EventTemplate eventTemplate;

    @FXML
    private AnchorPane nameAnchor;

    @FXML
    private Label nextEventLabel;

    @FXML
    private Label durationLabel;

    @FXML
    private Label frequencyLabel;

    @FXML
    private Label broadcastTeamLabel;

    @FXML
    private Label broadcastLabel;

    @FXML
    private Label remainingLabel;

    @FXML
    private AnchorPane venueSizeAnchorPane;

    @FXML
    private GridPane gridPane;

    @FXML
    private Button calendarButton;

    @FXML
    private Button editBroadcastTeamButton;

    @FXML
    private ComboBox rosterSplitComboBox;

    private EditLabel editLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        calendarButton.setText(CALENDAR_ICON);
        calendarButton.setOnAction(e -> {
            mainApp.show(ScreenCode.CALENDAR,
                    gameController.getEventManager().getNextEvent(eventTemplate));
        });
        editBroadcastTeamButton.setText(EDIT_ICON);
        editBroadcastTeamButton.setOnAction(e -> {
            EditBroadcastTeamDialog dialog = new EditBroadcastTeamDialog();
            Optional<List<StaffView>> optionalResult = dialog.getDialog(
                    gameController,
                    playerPromotion(),
                    eventTemplate.getDefaultBroadcastTeam().isEmpty()
                            ? playerPromotion().getDefaultBroadcastTeam()
                            : eventTemplate.getDefaultBroadcastTeam()
            ).showAndWait();
            optionalResult.ifPresent((List<StaffView> broadcastTeam) -> {
                eventTemplate.setDefaultBroadcastTeam(broadcastTeam);
                updateLabels();
            });
        });
    }

    @Override
    public void initializeMore() {
        GameScreen screen = ViewUtils.loadScreenFromResource(ScreenCode.EDIT_LABEL, mainApp, gameController, nameAnchor);
        editLabel = (EditLabel) screen.controller;
    }

    @Override
    public void setCurrent(Object obj) {
        if (obj instanceof EventTemplate) {
            this.eventTemplate = (EventTemplate) obj;
            updateLabels();
        }
        gridPane.setVisible(obj != null);
    }

    @Override
    public void updateLabels() {
        editLabel.setCurrent(eventTemplate);

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

            updateRosterSplitComboBox(rosterSplitComboBox,
                    gameController.getStableManager().getRosterSplits(),
                    eventTemplate,
                    eventTemplate.getPromotion(),
                    playerPromotion());

            nextEventLabel.setText(ModelUtils.dateString(eventTemplate.getNextDate()));
            durationLabel.setText(ModelUtils.timeString(eventTemplate.getDefaultDuration()));
            frequencyLabel.setText(eventTemplate.getEventFrequency().toString());
            broadcastLabel.setText(eventTemplate.getEventBroadcast().toString());
            remainingLabel.setText(Integer.toString(eventTemplate.getEventsLeft()));
            broadcastTeamLabel.setText(eventTemplate.getDefaultBroadcastTeam().isEmpty()
                    ? ModelUtils.slashNames(eventTemplate.getPromotion().getDefaultBroadcastTeam(), "None")
                    : ModelUtils.slashNames(eventTemplate.getDefaultBroadcastTeam()));
        } else {
            editLabel.setCurrent(BrowseMode.EVENTS);
        }

    }

}
