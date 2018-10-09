package wrestling.view.browser.controller;

import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import wrestling.model.modelView.TagTeamView;
import wrestling.model.segmentEnum.ActiveType;
import wrestling.model.segmentEnum.BrowseMode;
import wrestling.view.utility.GameScreen;
import wrestling.view.utility.ScreenCode;
import wrestling.view.utility.ViewUtils;
import wrestling.view.utility.interfaces.ControllerBase;

public class TagTeamViewController extends ControllerBase implements Initializable {

    private TagTeamView tagTeamView;

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
        if (obj instanceof TagTeamView) {
            this.tagTeamView = (TagTeamView) obj;
        } else {
            this.tagTeamView = null;
        }
        gridPane.setVisible(this.tagTeamView != null);
        imageAnchor1.setVisible(this.tagTeamView != null);
        imageAnchor2.setVisible(this.tagTeamView != null);
        updateLabels();
    }

    @Override
    public void updateLabels() {
        editLabel.setCurrent(tagTeamView);

        if (tagTeamView != null) {
            ComboBox comboBox = ViewUtils.updatePlayerComboBox(
                    activeTypeAnchorPane,
                    playerPromotion().getFullRoster().containsAll(tagTeamView.getWorkers()),
                    Arrays.asList(ActiveType.ACTIVE, ActiveType.INACTIVE),
                    tagTeamView.getTagTeam().getActiveType());
            comboBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<ActiveType>() {
                @Override
                public void changed(ObservableValue<? extends ActiveType> observable, ActiveType oldValue, ActiveType newValue) {
                    tagTeamView.getTagTeam().setActiveType(newValue);
                }
            });

            imageAnchor1.getChildren().clear();
            imageAnchor2.getChildren().clear();
            GameScreen card1 = ViewUtils.loadScreenFromResource(ScreenCode.RESULTS_CARD, mainApp, gameController, imageAnchor1);
            GameScreen card2 = ViewUtils.loadScreenFromResource(ScreenCode.RESULTS_CARD, mainApp, gameController, imageAnchor2);
            card1.controller.setCurrent(tagTeamView.getWorkers().get(0));
            card2.controller.setCurrent(tagTeamView.getWorkers().get(1));

            experienceLabel.setText(Integer.toString(tagTeamView.getTagTeam().getExperience()));

        } else {
            editLabel.setCurrent(BrowseMode.TAG_TEAMS);
        }

    }

}
