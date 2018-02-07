package wrestling.view;

import javafx.fxml.Initializable;
import org.apache.logging.log4j.Logger;
import wrestling.MainApp;
import wrestling.model.Promotion;
import wrestling.model.controller.GameController;

public abstract class ControllerBase implements Initializable {

    public MainApp mainApp;
    public GameController gameController;
    public transient Logger logger;

    public void setDependencies(MainApp mainApp, GameController gameController) {
        this.gameController = gameController;
        this.mainApp = mainApp;
        initializeMore();
    }

    public void initializeMore() {};

    public void setCurrent(Object obj) {};

    public void updateLabels() {};
    
    public Promotion playerPromotion() {
        return gameController.getPromotionManager().playerPromotion();
    }

}
