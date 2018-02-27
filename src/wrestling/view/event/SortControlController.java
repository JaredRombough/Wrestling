package wrestling.view.event;

import java.io.IOException;
import java.net.URL;
import java.util.Comparator;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import wrestling.view.utility.ScreenCode;
import wrestling.view.utility.interfaces.ControllerBase;

public class SortControlController extends ControllerBase implements Initializable {

    @FXML
    private Button reverseButton;

    @FXML
    private ComboBox comboBox;

    private Comparator currentComparator;

    private ScreenCode parentScreenCode;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        reverseButton.setText("▼");
    }

    @FXML
    private void handleButtonAction(ActionEvent event) throws IOException {

        if (event.getSource() == reverseButton) {
            reverseButton.setText(
                    reverseButton.getText().equals("▲")
                    ? "▼" : "▲");

            setCurrentComparator(currentComparator.reversed());
        }
    }

    @Override
    public void setCurrent(Object obj) {
        if (obj instanceof ObservableList) {
            setComparators((ObservableList<Comparator>) obj);
        }
    }

    private void setCurrentComparator(Comparator comparator) {
        currentComparator = comparator;
        if (parentScreenCode != null) {
            mainApp.updateLabels(parentScreenCode);
        }

    }

    private void setComparators(ObservableList<Comparator> comparators) {
        comboBox.setItems(comparators);

        comboBox.valueProperty().addListener((obs, oldItem, newItem) -> {
            if (newItem != null) {
                setCurrentComparator((Comparator) newItem);
            }
        });

        comboBox.getSelectionModel().selectFirst();
    }

    /**
     * @return the currentComparator
     */
    public Comparator getCurrentComparator() {
        return currentComparator;
    }

    /**
     * @param parentScreenCode the parentScreenCode to set
     */
    public void setParentScreenCode(ScreenCode parentScreenCode) {
        this.parentScreenCode = parentScreenCode;
    }

}
