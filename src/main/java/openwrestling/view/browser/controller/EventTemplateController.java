package openwrestling.view.browser.controller;

import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import openwrestling.model.gameObjects.EventTemplate;
import openwrestling.model.gameObjects.StaffMember;
import openwrestling.model.segment.constants.BrowseMode;
import openwrestling.model.segment.constants.EventVenueSize;
import openwrestling.model.segment.constants.StaffType;
import openwrestling.model.utility.ModelUtils;
import openwrestling.view.utility.GameScreen;
import openwrestling.view.utility.ScreenCode;
import openwrestling.view.utility.ViewUtils;
import openwrestling.view.utility.interfaces.ControllerBase;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import static openwrestling.model.constants.UIConstants.CALENDAR_ICON;
import static openwrestling.model.constants.UIConstants.EDIT_ICON;
import static openwrestling.view.utility.ViewUtils.updateRosterSplitComboBox;

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
                    gameController.getEventManager().getNextEvent(eventTemplate, gameController.getDateManager().today()));
        });
        editBroadcastTeamButton.setText(EDIT_ICON);
        editBroadcastTeamButton.setOnAction(e -> {
            showEditDefaultBroadcastTeamDialog().ifPresent((List<StaffMember> broadcastTeam) -> {
                gameController.getBroadcastTeamManager().setDefaultBroadcastTeam(eventTemplate, broadcastTeam);
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

            comboBox.getSelectionModel()
                    .selectedItemProperty()
                    .addListener((ChangeListener<EventVenueSize>) (observable, oldValue, newValue) -> eventTemplate.setEventVenueSize(newValue));

            updateRosterSplitComboBox(rosterSplitComboBox,
                    gameController.getRosterSplitManager().getRosterSplits(),
                    eventTemplate,
                    eventTemplate.getPromotion(),
                    playerPromotion());

            nextEventLabel.setText(ModelUtils.dateString(
                    gameController.getEventManager().getNextEvent(eventTemplate, gameController.getDateManager().today()).getDate())
            );
            durationLabel.setText(ModelUtils.timeString(eventTemplate.getDefaultDuration()));
            frequencyLabel.setText(eventTemplate.getEventFrequency().toString());
            broadcastLabel.setText(eventTemplate.getEventBroadcast().toString());
            broadcastTeamLabel.setText(gameController.getBroadcastTeamManager().getDefaultBroadcastTeam(eventTemplate).isEmpty()
                    ? ModelUtils.slashNames(gameController.getBroadcastTeamManager().getDefaultBroadcastTeam(eventTemplate.getPromotion()), "None")
                    : ModelUtils.slashNames(gameController.getBroadcastTeamManager().getDefaultBroadcastTeam(eventTemplate)));
        } else {
            editLabel.setCurrent(BrowseMode.EVENTS);
        }

    }

    private Optional<List<StaffMember>> showEditDefaultBroadcastTeamDialog() {
        EditBroadcastTeamDialog dialog = new EditBroadcastTeamDialog();
        List<StaffMember> defaultBroadCastTeam = gameController.getBroadcastTeamManager().getDefaultBroadcastTeam(eventTemplate).isEmpty()
                ? gameController.getBroadcastTeamManager().getDefaultBroadcastTeam(playerPromotion())
                : gameController.getBroadcastTeamManager().getDefaultBroadcastTeam(eventTemplate);
        return dialog.getDialog(
                gameController.getStaffManager().getStaff(StaffType.BROADCAST, playerPromotion()),
                defaultBroadCastTeam,
                eventTemplate.getName()
        ).showAndWait();
    }

}
