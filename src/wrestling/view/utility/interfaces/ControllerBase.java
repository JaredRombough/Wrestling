package wrestling.view.utility.interfaces;

import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import org.apache.logging.log4j.Logger;
import wrestling.MainApp;
import wrestling.model.controller.GameController;
import wrestling.model.modelView.PromotionView;

public abstract class ControllerBase implements Initializable {

    public MainApp mainApp;
    public GameController gameController;
    public transient Logger logger;
    public ResourceBundle resx;

    public void setDependencies(MainApp mainApp, GameController gameController) {
        this.gameController = gameController;
        this.mainApp = mainApp;
        resx = mainApp.getResx();
        initializeMore();
    }

    public void initializeMore() {};

    public void setCurrent(Object obj) {};

    public void updateLabels() {};
    
    public void focusLost() {};
    
    public PromotionView playerPromotion() {
        return gameController.getPromotionManager().playerPromotion();
    }

}
