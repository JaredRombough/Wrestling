package openwrestling.view.browser.controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import openwrestling.model.gameObjects.TagTeam;
import openwrestling.model.segment.constants.ActiveType;
import openwrestling.model.segment.constants.browse.mode.BrowseMode;
import openwrestling.view.utility.GameScreen;
import openwrestling.view.utility.ScreenCode;
import openwrestling.view.utility.ViewUtils;
import openwrestling.view.utility.interfaces.ControllerBase;

import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;

public class TagTeamController extends ControllerBase implements Initializable {

    private TagTeam tagTeam;

    @FXML
    private AnchorPane imageAnchor1;

    @FXML
    private AnchorPane imageAnchor2;

    @FXML
    private AnchorPane activeTypeAnchorPane;

    @FXML
    private AnchorPane nameAnchor;

    @FXML
    private GridPane gridPane;

    @FXML
    private Label experienceLabel;

    private EditLabel editLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    @Override
    public void initializeMore() {
        GameScreen screen = ViewUtils.loadScreenFromResource(ScreenCode.EDIT_LABEL, mainApp, gameController, nameAnchor);
        editLabel = (EditLabel) screen.controller;
    }

    @Override
    public void setCurrent(Object obj) {
        if (obj instanceof TagTeam) {
            this.tagTeam = (TagTeam) obj;
        } else {
            this.tagTeam = null;
        }
        gridPane.setVisible(this.tagTeam != null);
        imageAnchor1.setVisible(this.tagTeam != null);
        imageAnchor2.setVisible(this.tagTeam != null);
        updateLabels();
    }

    @Override
    public void updateLabels() {
        editLabel.setCurrent(tagTeam);

        if (tagTeam != null) {
            ComboBox comboBox = ViewUtils.updatePlayerComboBox(
                    activeTypeAnchorPane,
                    gameController.getWorkerManager().getRoster(playerPromotion()).containsAll(tagTeam.getWorkers()),
                    Arrays.asList(ActiveType.ACTIVE, ActiveType.INACTIVE),
                    tagTeam.getActiveType());
            comboBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<ActiveType>() {
                @Override
                public void changed(ObservableValue<? extends ActiveType> observable, ActiveType oldValue, ActiveType newValue) {
                    tagTeam.setActiveType(newValue);
                }
            });

            imageAnchor1.getChildren().clear();
            imageAnchor2.getChildren().clear();
            GameScreen card1 = ViewUtils.loadScreenFromResource(ScreenCode.RESULTS_CARD, mainApp, gameController, imageAnchor1);
            GameScreen card2 = ViewUtils.loadScreenFromResource(ScreenCode.RESULTS_CARD, mainApp, gameController, imageAnchor2);
            card1.controller.setCurrent(tagTeam.getWorkers().get(0));
            card2.controller.setCurrent(tagTeam.getWorkers().get(1));

            experienceLabel.setText(Integer.toString(tagTeam.getExperience()));

        } else {
            editLabel.setCurrent(BrowseMode.TAG_TEAMS);
        }

    }

}
