package wrestling.view;

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
import wrestling.view.interfaces.ControllerBase;
import wrestling.view.utility.ScreenCode;
import wrestling.view.utility.ViewUtils;

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
        String confirmButtonText;
        String cancelButtonText = "";
        boolean confirmButtonDisable;
        boolean cancelButtonDisable;
        boolean cancelButtonVisible = true;
        Event eventOnDate = gameController.getEventManager().getEventOnDate(playerPromotion(), currentDate);

        if (isRescheduling()) {
            dateLabel.setText("Select new date for " + eventToReschedule.toString());
            infoText.setText("Move " + eventToReschedule.toString() + " from " + eventToReschedule.getDate() + " to " + currentDate);
            
            confirmButtonText = "Confirm";
            cancelButtonDisable = false;
            cancelButtonText = "Cancel";
            confirmButtonDisable = eventOnDate != null;

        } else if (eventOnDate == null) {
            dateLabel.setText("Book a new event");
            infoText.setText("Create a new event on " + currentDate + "?");

            confirmButtonText = "Book";
            confirmButtonDisable = false;
            cancelButtonDisable = true;
            cancelButtonVisible = false;
            
        } else {
            dateLabel.setText("Modify existing event");
            infoText.setText("Cancel or reschedule event on " + currentDate + "?");

            confirmButtonText = "Reschedule";
            cancelButtonText = "Cancel Event";
            cancelButtonDisable = false;
            confirmButtonDisable = !gameController.getEventManager().canReschedule(eventOnDate);

        }

        cancelButton.setDisable(cancelButtonDisable);
        cancelButton.setVisible(cancelButtonVisible);
        cancelButton.setText(cancelButtonText);
        confirmButton.setText(confirmButtonText);
        confirmButton.setDisable(confirmButtonDisable);
    }

    private void bookShowOnDate() {
        if (ViewUtils.generateConfirmationDialogue("Booking a new show on " + currentDate, "Are you sure?")) {
            Event event = gameController.getEventFactory().createFutureEvent(playerPromotion(), currentDate);
            mainApp.show(ScreenCode.CALENDAR, event);
        }
    }

    private void startReschedule(Event event) {
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
