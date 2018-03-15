package wrestling.view.event;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import wrestling.model.segmentEnum.AngleType;
import wrestling.view.utility.interfaces.ControllerBase;

public class AngleOptions extends ControllerBase implements Initializable {

    @FXML
    private ComboBox angleTypeComboBox;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @Override
    public void initializeMore() {
        intitializeAngleTypeComboBox();
    }

    private void intitializeAngleTypeComboBox() {
        angleTypeComboBox.setItems(FXCollections.observableArrayList(AngleType.values()));
        

    }

    /**
     * @return the angleTypeComboBox
     */
    public ComboBox getAngleTypeComboBox() {
        return angleTypeComboBox;
    }

}
