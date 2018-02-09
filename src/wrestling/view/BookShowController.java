package wrestling.view;

import wrestling.view.interfaces.ControllerBase;
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
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import wrestling.model.Event;
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        logger = LogManager.getLogger(getClass());
        ViewUtils.inititializeRegion(anchorPane);
        rescheduling = false;
    }

    @Override
    public void initializeMore() {
        cancelButton.setText(resx.getString("Cancel"));
    }

    @FXML
    private void handleButtonAction(ActionEvent event) {
        if (event.getSource() == confirmButton) {
            if (rescheduling) {
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
            if (rescheduling) {
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

        if (rescheduling) {
            if (eventOnDate == null) {
                confirmButtonText = "Confirm";
                cancelButtonText = "Cancel";
                confirmButtonDisable = false;
                cancelButtonDisable = false;
            } else {
                confirmButtonText = "Confirm";
                cancelButtonText = "Cancel";
                confirmButtonDisable = true;
                cancelButtonDisable = false;
            }

        } else if (eventOnDate == null) {
            confirmButtonText = "Book";
            confirmButtonDisable = false;
            cancelButtonDisable = true;
            cancelButtonVisible = false;
        } else {
            confirmButtonText = "Reschedule";
            cancelButtonText = "Cancel Event";
            if (gameController.getEventManager().canReschedule(eventOnDate)) {
                confirmButtonDisable = false;
                cancelButtonDisable = false;
            } else {
                confirmButtonDisable = true;
                cancelButtonDisable = false;
            }

        }

        cancelButton.setDisable(cancelButtonDisable);
        cancelButton.setVisible(cancelButtonVisible);
        cancelButton.setText(cancelButtonText);
        confirmButton.setText(confirmButtonText);
        confirmButton.setDisable(confirmButtonDisable);
    }

    private void bookShowOnDate() {
        Event event = gameController.getEventFactory().createFutureEvent(playerPromotion(), currentDate);
        mainApp.show(ScreenCode.CALENDAR, event);
    }

    private void startReschedule(Event event) {
        rescheduling = true;
        eventToReschedule = event;
    }

    private void confirmReschedule() {
        eventToReschedule.setDate(currentDate);
        rescheduling = false;
        mainApp.show(ScreenCode.CALENDAR, eventToReschedule);
    }

    private void cancelReschedule() {
        rescheduling = false;
    }

    private void cancelShow() {
        logger.log(Level.INFO, "Canceling shows not yet implemented");
    }

    /**
     * @return the confirmButton
     */
    public Button getConfirmButton() {
        return confirmButton;
    }

}
