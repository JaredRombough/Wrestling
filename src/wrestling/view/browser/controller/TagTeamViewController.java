package wrestling.view.browser.controller;

import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import wrestling.model.modelView.TagTeamView;
import wrestling.model.segmentEnum.ActiveType;
import wrestling.model.segmentEnum.EventVenueSize;
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
    private Label experienceLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    @Override
    public void setCurrent(Object obj) {
        if (obj instanceof TagTeamView) {
            this.tagTeamView = (TagTeamView) obj;
            updateLabels();
        }
    }

    @Override
    public void updateLabels() {
        if (tagTeamView != null && tagTeamView.getWorkers().size() == 2) {

            ViewUtils.updatePlayerComboBox(
                    activeTypeAnchorPane,
                    gameController.getContractManager().getFullRoster(playerPromotion()).containsAll(tagTeamView.getWorkers()),
                    Arrays.asList(ActiveType.ACTIVE, ActiveType.INACTIVE),
                    tagTeamView.getTagTeam().getActiveType());
            nameAnchor.getChildren().clear();
            GameScreen screen = ViewUtils.loadScreenFromResource(ScreenCode.EDIT_LABEL, mainApp, gameController, nameAnchor);

            EditLabel editLabel = (EditLabel) screen.controller;
            editLabel.setCurrent(tagTeamView.getTagTeam().getName());
            editLabel.getButton().setOnAction(e -> {
                tagTeamView.getTagTeam().setName(ViewUtils.editTextDialog(tagTeamView.getTagTeam().getName()));
                updateLabels();
                mainApp.updateLabels(ScreenCode.BROWSER);
            });

            imageAnchor1.getChildren().clear();
            imageAnchor2.getChildren().clear();
            GameScreen card1 = ViewUtils.loadScreenFromResource(ScreenCode.RESULTS_CARD, mainApp, gameController, imageAnchor1);
            GameScreen card2 = ViewUtils.loadScreenFromResource(ScreenCode.RESULTS_CARD, mainApp, gameController, imageAnchor2);
            card1.controller.setCurrent(tagTeamView.getWorkers().get(0));
            card2.controller.setCurrent(tagTeamView.getWorkers().get(1));

            experienceLabel.setText(Integer.toString(tagTeamView.getTagTeam().getExperience()));

        }

    }

}
