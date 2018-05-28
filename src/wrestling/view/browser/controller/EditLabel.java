
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
    private Button button;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        label.underlineProperty().setValue(true);
    }

    /**
     * @return the button
     */
    public Button getButton() {
        return button;
    }

    @Override
    public void setCurrent(Object object) {
        if (object != null) {
            label.setText(object.toString());
        }

    }

}
