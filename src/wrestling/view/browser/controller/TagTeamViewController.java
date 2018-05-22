package wrestling.view.browser.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import wrestling.model.EventTemplate;
import wrestling.model.modelView.TagTeamView;
import wrestling.model.segmentEnum.EventVenueSize;
import wrestling.model.utility.ModelUtils;
import wrestling.view.results.controller.ResultsCardController;
import wrestling.view.utility.Screen;
import wrestling.view.utility.ScreenCode;
import wrestling.view.utility.ViewUtils;
import wrestling.view.utility.interfaces.ControllerBase;

public class TagTeamViewController extends ControllerBase implements Initializable {

    private TagTeamView tagTeamView;

    @FXML
    private Label nameLabel;

    @FXML
    private AnchorPane imageAnchor1;

    @FXML
    private AnchorPane imageAnchor2;

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
            nameLabel.setText(tagTeamView.toString());

            imageAnchor1.getChildren().clear();
            imageAnchor2.getChildren().clear();
            Screen card1 = ViewUtils.loadScreenFromResource(ScreenCode.RESULTS_CARD, mainApp, gameController, imageAnchor1);
            Screen card2 = ViewUtils.loadScreenFromResource(ScreenCode.RESULTS_CARD, mainApp, gameController, imageAnchor2);
            card1.controller.setCurrent(tagTeamView.getWorkers().get(0));
            card2.controller.setCurrent(tagTeamView.getWorkers().get(1));
        }

    }

}
