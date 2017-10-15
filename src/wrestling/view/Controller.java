package wrestling.view;

import javafx.fxml.Initializable;
import wrestling.MainApp;
import wrestling.model.controller.GameController;

public abstract class Controller implements Initializable {

    public MainApp mainApp;
    public GameController gameController;

    public void setDependencies(MainApp mainApp, GameController gameController) {
        this.gameController = gameController;
        this.mainApp = mainApp;
        initializeMore();
    }

    public void initializeMore() {};

    public void setCurrent(Object obj) {};

    public void updateLabels() {};

}
