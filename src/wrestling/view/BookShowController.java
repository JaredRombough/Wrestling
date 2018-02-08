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
import javafx.scene.text.Text;
import wrestling.model.Event;
import wrestling.view.utility.ScreenCode;
import wrestling.view.utility.ViewUtils;

public class BookShowController extends ControllerBase implements Initializable {

    @FXML
    public Label dateLabel;

    @FXML
    public ScrollPane scrollPane;

    @FXML
    public Button bookShowButton;

    @FXML
    private Button rescheduleButton;

    @FXML
    public AnchorPane anchorPane;

    private LocalDate currentDate;
    private boolean rescheduling;
    private LocalDate rescheduleDate;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ViewUtils.inititializeRegion(anchorPane);
        rescheduling = false;
    }

    @FXML
    private void handleButtonAction(ActionEvent event) {
        if (event.getSource() == bookShowButton) {
            bookShowClicked();
        }
    }

    public void setDate(LocalDate date) {
        currentDate = date;
        updateLabels();
    }

    public void clickReschedule() {
        rescheduling = !rescheduling;
        rescheduleDate = currentDate;
        updateLabels();
    }

    @Override
    public void updateLabels() {

        Text displayContent = new Text();
        BookShowState state = rescheduling ? BookShowState.CAN_RESCHEDULE : BookShowState.CAN_BOOK;
        Event eventOnDay = gameController.getEventManager().getEventOnDate(playerPromotion(), currentDate);
        String displayText = "";

        dateLabel.setText(rescheduling
                ? ("Reschedule show from " + rescheduleDate + " to " + currentDate)
                : ("Book a show for " + currentDate)
        );

        if (currentDate.isBefore(gameController.getDateManager().today())) {
            state = rescheduling ? BookShowState.CANT_RESCHEDULE_PAST : BookShowState.CANT_BOOK_PAST;
        } else if (eventOnDay != null) {
            state = rescheduling ? BookShowState.CANT_RESCHEDULE_CONFLICT : BookShowState.CANT_BOOK_CONFILCT;
            displayText = eventOnDay.toString() + "\n" + gameController.getEventManager().generateSummaryString(eventOnDay);
        }

        String leftButtonText = "";
        String rightButtonText = "";
        boolean leftButtonDisabled = false;
        boolean rightButtonDisabled = false;

        switch (state) {
            case CAN_BOOK:
                leftButtonText = "Reschedule Event";
                leftButtonDisabled = true;
                rightButtonText = "Book Show";
                rightButtonDisabled = false;
                displayText = "Booking a new show on this date.";
                break;
            case CAN_RESCHEDULE:
                leftButtonText = "Reschedule Event";
                leftButtonDisabled = true;
                rightButtonText = "Reschedule Show";
                rightButtonDisabled = false;
                displayText = "Booking a new show on this date.";
                break;
            case CANT_BOOK_PAST:
                leftButtonText = "Reschedule Event";
                leftButtonDisabled = true;
                rightButtonText = "Book Show";
                rightButtonDisabled = true;
                displayText = "This date is in the past, can't book a show.";
                break;
            case CANT_RESCHEDULE_PAST:
                leftButtonText = "Cancel";
                leftButtonDisabled = false;
                rightButtonText = "Reschedule Show";
                rightButtonDisabled = true;
                displayText = "This date is in the past, can't reschedule a show.";
                break;
            case CANT_BOOK_CONFILCT:
                leftButtonText = "Reschedule Event";
                leftButtonDisabled = !gameController.getEventManager().canReschedule(eventOnDay);
                rightButtonText = "Book Show";
                rightButtonDisabled = true;
                displayText = "There is already an event on this date, can't book a show.";
                break;
            case CANT_RESCHEDULE_CONFLICT:
                leftButtonText = "Cancel";
                leftButtonDisabled = false;
                rightButtonText = "Reschedule Show";
                rightButtonDisabled = true;
                displayText = "There is already an event on this date, can't reschedule a show.";
                break;
        }

        bookShowButton.setDisable(rightButtonDisabled);
        bookShowButton.setText(rightButtonText);
        rescheduleButton.setText(leftButtonText);
        rescheduleButton.setDisable(leftButtonDisabled);
        displayContent.setText(displayText);
        scrollPane.setContent(displayContent);
    }

    private void bookShowClicked() {
        Event event = gameController.getEventFactory().createFutureEvent(playerPromotion(), currentDate);
        mainApp.show(ScreenCode.CALENDAR, event);
    }

    /**
     * @return the rescheduleButton
     */
    public Button getRescheduleButton() {
        return rescheduleButton;
    }

    private enum BookShowState {
        CAN_BOOK,
        CAN_RESCHEDULE,
        CANT_BOOK_PAST,
        CANT_RESCHEDULE_PAST,
        CANT_BOOK_CONFILCT,
        CANT_RESCHEDULE_CONFLICT
    }

}
