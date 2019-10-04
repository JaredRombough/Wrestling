package openwrestling.view.event.controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import openwrestling.model.AngleParams;
import openwrestling.model.gameObjects.EventTemplate;
import openwrestling.model.SegmentTemplate;
import openwrestling.model.gameObjects.Stable;
import openwrestling.model.segmentEnum.AngleType;
import openwrestling.model.segmentEnum.JoinTeamType;
import openwrestling.model.segmentEnum.ShowType;
import openwrestling.view.utility.interfaces.ControllerBase;

public class AngleOptionsController extends ControllerBase implements Initializable {

    @FXML
    private ComboBox angleTypeComboBox;

    @FXML
    private ComboBox combo1;

    @FXML
    private Label label1;

    @FXML
    private Button challengeButton;

    private List<Object> challengeOptions;
    private boolean challengeIsPresent;
    private boolean challengeIsComplete;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        challengeOptions = new ArrayList<>();
    }

    private void setAngleType(AngleType type) {
        switch (type) {
            case OFFER:
                setCombo1(FXCollections.observableArrayList(JoinTeamType.values()),
                        JoinTeamType.label());
                break;
            case CHALLENGE:

                setCombo1(FXCollections.observableArrayList(challengeOptions),
                        ShowType.label());
                break;
            default:
                setCombo1Visibility(false);
                break;
        }
    }

    public AngleParams getAngleParams() {
        AngleParams angleParams = new AngleParams();
        angleParams.setAngleType((AngleType) angleTypeComboBox.getValue());
        if (AngleType.OFFER.equals(angleParams.getAngleType())) {
            if (combo1.getValue() instanceof JoinTeamType) {
                angleParams.setJoinTeamType((JoinTeamType) combo1.getValue());
            } else if (combo1.getValue() instanceof Stable) {
                angleParams.setJoinTeamType(JoinTeamType.STABLE);
                angleParams.setJoinStable((Stable) combo1.getValue());
            }
        } else if (AngleType.CHALLENGE.equals((angleParams.getAngleType()))) {
            SegmentTemplate challengeSegment = new SegmentTemplate();
            if (combo1.getValue() instanceof ShowType) {
                angleParams.setShowType((ShowType) combo1.getValue());
            } else if (combo1.getValue() instanceof EventTemplate) {
                angleParams.setShowType(ShowType.NEXT_SHOW);
                challengeSegment.setEventTemplate((EventTemplate) combo1.getValue());
            }
            angleParams.setChallengeSegment(challengeSegment);
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

    public void setChallengeOptions(List<Object> challengeOptions) {
        this.challengeOptions = challengeOptions;
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

    public void setAngleTypeListener(ChangeListener<AngleType> listener) {
        angleTypeComboBox.valueProperty().addListener(listener);
        angleTypeComboBox.getSelectionModel().selectFirst();
    }

    /**
     * @return the combo1
     */
    public ComboBox getCombo1() {
        return combo1;
    }

    public void setChallengeButtonAction(EventHandler<ActionEvent> action) {
        challengeButton.setOnAction(action);
    }

    @Override
    public void updateLabels() {
        AngleParams angleParams = getAngleParams();
        challengeButton.setDisable(challengeIsPresent || !challengeIsComplete || !angleParams.getShowType().equals(ShowType.TONIGHT));
        challengeButton.setVisible(AngleType.CHALLENGE.equals(angleParams.getAngleType()));
    }

    /**
     * @param challengeIsPresent the challengeIsPresent to set
     */
    public void setChallengeIsPresent(boolean challengeIsPresent) {
        this.challengeIsPresent = challengeIsPresent;
    }

    /**
     * @param challengeIsComplete the challengeIsComplete to set
     */
    public void setChallengeIsComplete(boolean challengeIsComplete) {
        this.challengeIsComplete = challengeIsComplete;
    }

}
