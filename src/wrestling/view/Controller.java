package wrestling.view;

import javafx.fxml.Initializable;
import wrestling.MainApp;
import wrestling.model.GameController;

public abstract class Controller implements Initializable {

    private MainApp mainApp;
    private GameController gameController;

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    public void setGameController(GameController gameController) {
        this.gameController = gameController;
        initializeMore();
    }

    abstract void initializeMore();

    abstract void setCurrent(Object obj);

    abstract void updateLabels();

}
