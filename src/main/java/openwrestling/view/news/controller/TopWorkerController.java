package openwrestling.view.news.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import openwrestling.model.gameObjects.Worker;
import openwrestling.model.segment.constants.browse.mode.BrowseMode;
import openwrestling.model.utility.ModelUtils;
import openwrestling.view.results.controller.ResultsCardController;
import openwrestling.view.utility.GameScreen;
import openwrestling.view.utility.ScreenCode;
import openwrestling.view.utility.ViewUtils;
import openwrestling.view.utility.interfaces.ControllerBase;
import org.apache.commons.lang3.StringUtils;

import java.net.URL;
import java.util.ResourceBundle;

public class TopWorkerController extends ControllerBase implements Initializable {

    @FXML
    private AnchorPane imageAnchor;

    @FXML
    private Label rankLabel;

    @FXML
    private Label nameLabel;

    @FXML
    private Label statLabel;

    @FXML
    private Label promotionLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    public void setWorker(BrowseMode browseMode, Worker worker, int rank) {
        imageAnchor.getChildren().clear();
        GameScreen card = ViewUtils.loadScreenFromResource(ScreenCode.RESULTS_CARD, mainApp, gameController, imageAnchor);
        ((ResultsCardController) card.controller).setWorkerInfoMode(60);
        card.controller.setCurrent(worker);

        statLabel.setText(getStatLabelText(browseMode, worker));

        rankLabel.setText(String.valueOf(rank));
        String promotionText = gameController.getContractManager().contractPromotionsString(worker);
        if (StringUtils.isEmpty(promotionText)) {
            promotionText = "Free Agent";
        }
        promotionLabel.setText(promotionText);
        nameLabel.setText(worker.getName());
    }

    private String getStatLabelText(BrowseMode browseMode, Worker worker) {
        int statValue;
        switch (browseMode) {
            case TOP_POPULARITY:
            case TOP_POPULARITY_MEN:
            case TOP_POPULARITY_WOMEN:
                statValue = worker.getPopularity();
                break;
            case TOP_STRIKING:
            case TOP_STRIKING_MEN:
            case TOP_STRIKING_WOMEN:
                statValue = worker.getStriking();
                break;
            case TOP_WRESTLING:
            case TOP_WRESTLING_MEN:
            case TOP_WRESTLING_WOMEN:
                statValue = worker.getWrestling();
                break;
            case TOP_FLYING:
            case TOP_FLYING_MEN:
            case TOP_FLYING_WOMEN:
                statValue = worker.getFlying();
                break;
            case TOP_CHARISMA:
            case TOP_CHARISMA_MEN:
            case TOP_CHARISMA_WOMEN:
                statValue = worker.getCharisma();
                break;
            case TOP_WORKRATE:
            case TOP_WORKRATE_MEN:
            case TOP_WORKRATE_WOMEN:
                return ViewUtils.intToStars(ModelUtils.getMatchWorkRating(worker));
            default:
                statValue = 0;
                break;
        }
        return String.valueOf(statValue);
    }

}
