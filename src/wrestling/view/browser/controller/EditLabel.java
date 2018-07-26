package wrestling.view.browser.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import wrestling.view.utility.interfaces.ControllerBase;

public class EditLabel extends ControllerBase implements Initializable {
    
    @FXML
    Label label;
    
    @FXML
    private Button editButton;
    
    @FXML
    private Button createButton;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        label.setText("");
    }

    /**
     * @return the button
     */
    public Button getEditButton() {
        return editButton;
    }
    
    @Override
    public void setCurrent(Object object) {
        if (object != null) {
            label.setText(object.toString());
        } else {
            label.setText("");
        }
        
    }

    /**
     * @return the createButton
     */
    public Button getCreateButton() {
        return createButton;
    }
    
}
