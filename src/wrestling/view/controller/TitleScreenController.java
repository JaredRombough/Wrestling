
package wrestling.view.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import wrestling.MainApp;


public class TitleScreenController implements Initializable {
    
    private MainApp mainApp;
    
    private Button startButton;
    
    @FXML
    private void handleButtonAction(ActionEvent event) throws IOException{
        
        
        mainApp.showStartGameScreen();
       }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    }    

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;

    }
}
