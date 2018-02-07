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
    public AnchorPane anchorPane;
    
    private LocalDate currentDate;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ViewUtils.inititializeRegion(anchorPane);
    }
    
    @FXML
    private void handleButtonAction(ActionEvent event) {
        if (event.getSource() == bookShowButton) {
            bookShowClicked();
        }
    }
    
    public void setDate(LocalDate date) {
        currentDate = date;
        dateLabel.setText("Book a show for " + currentDate.toString());
        updateLabels();
    }
    
    @Override
    public void updateLabels() {
        boolean disable = false;
        String buttonText = "Book show";
        Text text = new Text();
        Event eventOnDay = gameController.getEventManager().getEventOnDate(playerPromotion(), currentDate);
        if (currentDate.isBefore(gameController.getDateManager().today())) {
            disable = true;
            buttonText = "Cannot book show past date";
            
        } else if (eventOnDay != null) {
            disable = true;
            buttonText = "There is already a show on this date";
            text.setText(eventOnDay.toString() + "\n" + gameController.getEventManager().generateSummaryString(eventOnDay));
        }
        scrollPane.setContent(text);
        bookShowButton.setDisable(disable);
        bookShowButton.setText(buttonText);
    }
    
    private void bookShowClicked() {
        Event event = gameController.getEventFactory().createFutureEvent(playerPromotion(), currentDate);
        mainApp.show(ScreenCode.CALENDAR, event);
    }
    
}
