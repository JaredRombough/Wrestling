/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wrestling.view.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import wrestling.MainApp;
import wrestling.model.GameController;

/**
 *
 * @author jared
 */
public class RootLayoutController implements Initializable {
    
    private MainApp mainApp;
    private GameController gameController;
    
    @FXML
    private Button eventButton;
    
    
    @FXML
    private Button nextDayButton;
    
    @FXML
    private Label currentDateLabel;
    
    @FXML
    private Label currentFundsLabel;
    
    
    @FXML
    private Button browserButton;
    
    
    
    @FXML
    private void handleButtonAction(ActionEvent event) throws IOException{
        
        if(event.getSource() == eventButton) {
            //System.out.println("eventButton pushed");
            mainApp.showEventScreen();
            
       } else if (event.getSource() == nextDayButton) {
           gameController.nextDay();
           updateCurrentDateLabel();
           mainApp.saveGame();
       } else if (event.getSource() == browserButton) {
           
           mainApp.showBrowser();
       }
           
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        setButtonsDisable(true);
    }    
    
    public void initializeMore() {
        updateCurrentDateLabel();
        //updateCurrentFundsLabel();
    }
    
    public void updateCurrentDateLabel() {
        currentDateLabel.setText("Day " + gameController.date());
    }
    
    public void updateCurrentFundsLabel() {
        System.out.println("update current funds label");
        System.out.println(gameController.playerPromotion());
        currentFundsLabel.setText("Funds: $" + gameController.playerPromotion().getFunds());
    }
    
    public void setButtonsDisable(boolean disable) {
        browserButton.setDisable(disable);
        nextDayButton.setDisable(disable);
        eventButton.setDisable(disable);
    }
    
    
    /**
     * Is called by the main application to give a reference back to itself.
     * 
     * @param mainApp
     */
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;

    }
    public void setGameController(GameController gameController) {
        this.gameController = gameController;
        
        initializeMore();
        
    }
}
