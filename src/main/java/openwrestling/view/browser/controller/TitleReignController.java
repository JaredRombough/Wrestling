package openwrestling.view.browser.controller;

import java.net.URL;
import static java.time.temporal.ChronoUnit.DAYS;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import openwrestling.model.modelView.TitleReign;
import openwrestling.model.utility.ModelUtils;
import openwrestling.view.utility.interfaces.ControllerBase;

public class TitleReignController extends ControllerBase implements Initializable {

    @FXML
    Label nameLabel;

    @FXML
    Label dateWonLabel;

    @FXML
    Label dateLostLabel;

    @FXML
    Label numberOfDaysLabel;

    private TitleReign titleReign;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    @Override
    public void setCurrent(Object object) {
        if (object instanceof TitleReign) {
            titleReign = (TitleReign) object;
            updateLabels();
        }

    }

    @Override
    public void updateLabels() {
        nameLabel.setText(titleReign.getWorkers().isEmpty()
                ? "Vacant"
                : ModelUtils.slashNames(titleReign.getWorkers()));
        dateWonLabel.setText(titleReign.getDayWon().toString());
        dateLostLabel.setText(titleReign.getDayLostString());
        numberOfDaysLabel.setText(Long.toString(getNumberOfDays()) + " Days");
    }

    private long getNumberOfDays() {
        if (titleReign.getDateLost() == null) {
            return DAYS.between(titleReign.getDayWon(), gameController.getDateManager().today());
        } else {
            return DAYS.between(titleReign.getDayWon(), titleReign.getDateLost());
        }
    }
}
