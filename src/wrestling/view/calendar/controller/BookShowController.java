package wrestling.view.calendar.controller;

import java.net.URL;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import wrestling.model.Event;
import wrestling.model.EventTemplate;
import wrestling.model.segmentEnum.EventFrequency;
import wrestling.model.segmentEnum.EventVenueSize;
import wrestling.view.utility.ScreenCode;
import wrestling.view.utility.ViewUtils;
import wrestling.view.utility.interfaces.ControllerBase;

public class BookShowController extends ControllerBase implements Initializable {

    @FXML
    public Label dateLabel;

    @FXML
    public ScrollPane scrollPane;

    @FXML
    private Button confirmButton;

    @FXML
    private Button cancelButton;

    @FXML
    public AnchorPane anchorPane;

    private LocalDate currentDate;
    private boolean rescheduling;
    private Event eventToReschedule;
    private Text infoText;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        logger = LogManager.getLogger(getClass());
        ViewUtils.inititializeRegion(anchorPane);
        rescheduling = false;
        infoText = new Text();
        scrollPane.setContent(infoText);
    }

    @Override
    public void initializeMore() {
        cancelButton.setText(resx.getString("Cancel"));
    }

    @FXML
    private void handleButtonAction(ActionEvent event) {
        if (event.getSource() == confirmButton) {
            if (isRescheduling()) {
                confirmReschedule();
            } else {
                Event eventOnDate = gameController.getEventManager().getEventOnDate(playerPromotion(), currentDate);
                if (eventOnDate == null) {
                    bookShowOnDate();
                } else {
                    startReschedule(eventOnDate);
                }
            }
        } else if (event.getSource() == cancelButton) {
            if (isRescheduling()) {
                cancelReschedule();
            } else {
                cancelShow();
            }
        }
    }

    @Override
    public void setCurrent(Object obj) {
        if (obj instanceof LocalDate) {
            currentDate = (LocalDate) obj;
        } else {
            logger.log(Level.INFO, "Invalid object sent to controller", obj);
        }
        updateLabels();
    }

    @Override
    public void updateLabels() {
        String confirmButtonText = "";
        String cancelButtonText = "";
        String dateLabelContent = "";
        String infoTextContent = "";
        boolean confirmButtonDisable;
        boolean cancelButtonDisable;
        boolean cancelButtonVisible = true;
        boolean confirmButtonVisible = true;
        Event eventOnDate = gameController.getEventManager().getEventOnDate(playerPromotion(), currentDate);

        if (currentDate.isBefore(gameController.getDateManager().today())) {
            cancelButtonVisible = false;
            cancelButtonDisable = true;
            confirmButtonVisible = false;
            confirmButtonDisable = true;

        } else if (isRescheduling()) {
            dateLabelContent = String.format("Select new date for %s", eventToReschedule.toString());
            infoTextContent = String.format("Move %s from %s to %s", eventToReschedule.toString(), eventToReschedule.getDate(), currentDate);

            confirmButtonText = "Confirm";
            cancelButtonDisable = false;
            cancelButtonText = "Cancel";
            confirmButtonDisable = eventOnDate != null;

        } else if (eventOnDate == null) {
            dateLabelContent = "Book a new event";
            infoTextContent = String.format("Create a new event on %s?", currentDate);

            confirmButtonText = "Book";
            confirmButtonDisable = false;
            cancelButtonDisable = true;
            cancelButtonVisible = false;

        } else {
            dateLabelContent = String.format("Modify %s", eventOnDate.toString());
            infoTextContent = String.format("Cancel or reschedule %s on %s?",
                    eventOnDate.toString(), currentDate);
            confirmButtonText = "Reschedule";
            cancelButtonText = "Cancel Event";
            cancelButtonDisable = false;
            confirmButtonDisable = !gameController.getEventManager().canReschedule(eventOnDate);

        }

        dateLabel.setText(dateLabelContent);
        infoText.setText(infoTextContent);
        cancelButton.setDisable(cancelButtonDisable);
        cancelButton.setVisible(cancelButtonVisible);
        cancelButton.setText(cancelButtonText);
        confirmButton.setText(confirmButtonText);
        confirmButton.setDisable(confirmButtonDisable);
        confirmButton.setVisible(confirmButtonVisible);
    }

    private void bookShowOnDate() {
        Optional<EventTemplate> optionalResult = createShowDialog().showAndWait();
        optionalResult.ifPresent((EventTemplate template) -> {

            gameController.getEventManager().addEventTemplate(template);
            gameController.getPromotionController().bookEventTemplate(template, currentDate);

            mainApp.show(ScreenCode.CALENDAR,
                    gameController.getEventManager().getEventOnDate(
                            playerPromotion(), currentDate));
        });
    }

    private Dialog createShowDialog() {
        Dialog<EventTemplate> dialog = new Dialog<>();
        DialogPane dialogPane = dialog.getDialogPane();
        TextField eventName = new TextField();
        ComboBox<EventVenueSize> venueSize = new ComboBox(FXCollections.observableArrayList(EventVenueSize.values()));
        ComboBox<EventFrequency> frequency = new ComboBox(FXCollections.observableArrayList(EventFrequency.values()));
        ComboBox duration = new ComboBox(FXCollections.observableArrayList(
                Arrays.asList(30, 60, 90, 120, 180, 240, 300)));
        VBox vBox = new VBox(8);

        dialog.setTitle("Book Event");
        dialog.setHeaderText("Event Values");
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        venueSize.getSelectionModel().selectFirst();
        frequency.getSelectionModel().selectFirst();
        duration.getSelectionModel().selectFirst();
        Node okButton = dialog.getDialogPane().lookupButton(ButtonType.OK);
        okButton.setDisable(true);
        eventName.textProperty().addListener((observable, oldValue, newValue) -> {
            okButton.setDisable(newValue.trim().isEmpty());
        });

        ViewUtils.addRegionWrapperToVBox(eventName, "Event Name:", vBox);
        ViewUtils.addRegionWrapperToVBox(venueSize, "Venue Size:", vBox);
        ViewUtils.addRegionWrapperToVBox(frequency, "Frequency:", vBox);
        ViewUtils.addRegionWrapperToVBox(duration, "Duration (Minutes):", vBox);

        dialogPane.setContent(vBox);
        dialogPane.getStylesheets().add("style.css");

        Platform.runLater(eventName::requestFocus);

        dialog.setResultConverter((ButtonType button) -> {
            if (button == ButtonType.OK) {
                EventTemplate template = new EventTemplate();
                template.setName(eventName.getText());
                template.setEventVenueSize(venueSize.getValue());
                template.setEventFrequency(frequency.getValue());
                template.setDefaultDuration((int) duration.getValue());
                template.setPromotion(playerPromotion());
                template.setMonth(currentDate.getMonth());
                template.setDayOfWeek(currentDate.getDayOfWeek());
                template.setEventsLeft(52);
                return template;
            }
            return null;
        });
        return dialog;
    }

    public void startReschedule(Event event) {
        rescheduling = true;
        eventToReschedule = event;
        updateLabels();
    }

    private void confirmReschedule() {
        if (ViewUtils.generateConfirmationDialogue(String.format("Rescheduling %s from %s to %s", eventToReschedule.toString(), eventToReschedule.getDate(), currentDate),
                "Are you sure?")) {
            gameController.getEventManager().rescheduleEvent(eventToReschedule, currentDate);

            rescheduling = false;
            mainApp.show(ScreenCode.CALENDAR, eventToReschedule);
        }
    }

    public void cancelReschedule() {
        eventToReschedule = null;
        rescheduling = false;
        updateLabels();
    }

    private void cancelShow() {
        Event toCancel = gameController.getEventManager().getEventOnDate(playerPromotion(), currentDate);
        if (ViewUtils.generateConfirmationDialogue(String.format("Canceling %s on %s", toCancel.toString(), currentDate.toString()), "Are you sure?")) {
            gameController.getEventManager().cancelEvent(toCancel);
            mainApp.show(ScreenCode.CALENDAR, currentDate);
        }
    }

    /**
     * @return the confirmButton
     */
    public Button getConfirmButton() {
        return confirmButton;
    }

    /**
     * @return the rescheduling
     */
    public boolean isRescheduling() {
        return rescheduling;
    }

}
