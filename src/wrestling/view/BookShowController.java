package wrestling.view;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.text.Text;
import wrestling.model.Worker;

public class BookShowController extends ControllerBase implements Initializable {

    @FXML
    public Label dateLabel;

    @FXML
    public ScrollPane scrollPane;

    private LocalDate currentDate;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void setDate(LocalDate date) {
        currentDate = date;
        dateLabel.setText(currentDate.toString());
        updateText();
    }

    private void updateText() {
        Text text = new Text();
        StringBuilder sb = new StringBuilder();
        List<Worker> unavailable = gameController.getEventManager().getUnavailableRoster(
                gameController.getPromotionManager().playerPromotion(), currentDate);
        for (Worker worker : unavailable) {
            sb.append(worker.getName());
            sb.append("\n");
        }
        scrollPane.setContent(text);

    }

}
