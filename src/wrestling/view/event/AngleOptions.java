package wrestling.view.event;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import wrestling.model.segmentEnum.AngleType;
import wrestling.model.segmentEnum.Description;
import wrestling.model.segmentEnum.JoinTeamType;
import wrestling.model.segmentEnum.ShowType;
import wrestling.view.utility.interfaces.ControllerBase;

public class AngleOptions extends ControllerBase implements Initializable {

    @FXML
    private ComboBox angleTypeComboBox;

    @FXML
    private ComboBox combo1;

    @FXML
    private ComboBox combo2;

    @FXML
    private Label label1;

    @FXML
    private Label label2;

    private AngleType type;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void setAngleType(AngleType type) {
        this.type = type;
        switch (type) {
            case PROMO:
                setCombo1Visibility(false);
                setCombo2Visibility(false);
                break;
            case OFFER:
                setCombo1(FXCollections.observableArrayList(JoinTeamType.values()),
                        JoinTeamType.label());
                setCombo2Visibility(false);
                break;
            case CHALLENGE:
                setCombo1(FXCollections.observableArrayList(ShowType.values()),
                        ShowType.label());
                setCombo2Visibility(false);
                break;
            default:
                break;
        }
    }

    private void setCombo1(ObservableList items, String labelText) {
        setCombo1Visibility(true);
        combo1.setItems(items);
        combo1.getSelectionModel().selectFirst();
        label1.setText(labelText);
    }

    private void setCombo2(ObservableList items, String labelText) {
        setCombo2Visibility(true);
        combo2.setItems(items);
        combo2.getSelectionModel().selectFirst();
        label2.setText(labelText);
    }

    private void setCombo1Visibility(boolean visible) {
        combo1.setVisible(visible);
        label1.setVisible(visible);
    }

    private void setCombo2Visibility(boolean visible) {
        combo2.setVisible(visible);
        label2.setVisible(visible);
    }

    @Override
    public void initializeMore() {
        intitializeAngleTypeComboBox();
    }

    private void intitializeAngleTypeComboBox() {
        angleTypeComboBox.setItems(FXCollections.observableArrayList(AngleType.values()));
        angleTypeComboBox.getSelectionModel().selectFirst();
    }

    /**
     * @return the angleTypeComboBox
     */
    public ComboBox getAngleTypeComboBox() {
        return angleTypeComboBox;
    }

}
