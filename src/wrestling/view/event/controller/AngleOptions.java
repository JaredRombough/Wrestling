package wrestling.view.event.controller;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import wrestling.model.AngleParams;
import wrestling.model.modelView.StableView;
import wrestling.model.segmentEnum.AngleType;
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void setAngleType(AngleType type) {
        switch (type) {
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
                setCombo1Visibility(false);
                setCombo2Visibility(false);
                break;
        }
    }

    public AngleParams getAngleParams() {
        AngleParams angleParams = new AngleParams();
        angleParams.setAngleType((AngleType) angleTypeComboBox.getValue());
        if (AngleType.OFFER.equals(angleParams.getAngleType())) {
            if (combo1.getValue() instanceof JoinTeamType) {
                angleParams.setJoinTeamType((JoinTeamType) combo1.getValue());
            } else if (combo1.getValue() instanceof StableView) {
                angleParams.setJoinTeamType(JoinTeamType.STABLE);
                angleParams.setJoinStable((StableView) combo1.getValue());
            }
        }
        return angleParams;
    }

    private void setCombo1(ObservableList items, String labelText) {
        setCombo1Visibility(true);
        getCombo1().setItems(items);
        getCombo1().getSelectionModel().selectFirst();
        label1.setText(labelText);
    }

    private void setCombo1Visibility(boolean visible) {
        getCombo1().setVisible(visible);
        label1.setVisible(visible);
    }

    private void setCombo2Visibility(boolean visible) {
        getCombo2().setVisible(visible);
        label2.setVisible(visible);
    }

    @Override
    public void initializeMore() {
        intitializeAngleTypeComboBox();
    }

    private void intitializeAngleTypeComboBox() {
        angleTypeComboBox.setItems(FXCollections.observableArrayList(AngleType.values()));
        angleTypeComboBox.valueProperty().addListener(new ChangeListener<AngleType>() {
            @Override
            public void changed(ObservableValue ov, AngleType t, AngleType t1) {
                if (t1 != null) {
                    setAngleType(t1);
                }
            }
        });
        angleTypeComboBox.getSelectionModel().selectFirst();
    }

    public void setOffers(List<Object> offers) {
        Object selected = null;
        if (offers.contains(combo1.getSelectionModel().selectedItemProperty().get())) {
            selected = combo1.getSelectionModel().selectedItemProperty().get();
        }
        combo1.setItems(FXCollections.observableArrayList(offers));
        if (selected != null) {
            combo1.getSelectionModel().select(selected);
        } else {
            combo1.getSelectionModel().selectFirst();
        }
    }

    /**
     * @return the angleTypeComboBox
     */
    public ComboBox getAngleTypeComboBox() {
        return angleTypeComboBox;
    }

    /**
     * @return the combo1
     */
    public ComboBox getCombo1() {
        return combo1;
    }

    /**
     * @return the combo2
     */
    public ComboBox getCombo2() {
        return combo2;
    }

}
