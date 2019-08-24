package openwrestling.view.utility.interfaces;

import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import org.apache.logging.log4j.Logger;
import openwrestling.MainApp;
import openwrestling.model.controller.GameController;
import openwrestling.model.gameObjects.Promotion;

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
    
    public Promotion playerPromotion() {
        return gameController.getPromotionManager().playerPromotion();
    }
    
    public LocalDate today() {
        return gameController.getDateManager().today();
    }

}
