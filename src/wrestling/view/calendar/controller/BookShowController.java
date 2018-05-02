package wrestling.view.calendar.controller;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import wrestling.model.Event;
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
            dateLabelContent = "Modify existing event";
            infoTextContent = String.format("Cancel or reschedule event on %s?", currentDate);

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
        if (ViewUtils.generateConfirmationDialogue("Booking a new show on " + currentDate, "Are you sure?")) {
            Event event = gameController.getEventFactory().createFutureEvent(playerPromotion(), currentDate);
            mainApp.show(ScreenCode.CALENDAR, event);
        }
    }

    public void startReschedule(Event event) {
        rescheduling = true;
        eventToReschedule = event;
        updateLabels();
    }

    private void confirmReschedule() {
        if (ViewUtils.generateConfirmationDialogue(String.format("Rescheduling %s from %s to %s", eventToReschedule.toString(), eventToReschedule.getDate(), currentDate),
                "Are you sure?")) {
            eventToReschedule.setDate(currentDate);
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
