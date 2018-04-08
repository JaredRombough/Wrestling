package wrestling.view.utility;

import java.io.IOException;
import java.net.URL;
import java.util.Comparator;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.VBox;
import wrestling.model.Worker;
import wrestling.model.segmentEnum.Gender;
import wrestling.view.utility.interfaces.ControllerBase;

public class SortControlController extends ControllerBase implements Initializable {

    @FXML
    private Button reverseButton;

    @FXML
    private ComboBox comboBox;

    @FXML
    private VBox vBox;

    private Comparator currentComparator;

    private ScreenCode parentScreenCode;

    private Gender gender;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        reverseButton.setText("▼");
        addGenderFilter();
    }

    public void addGenderFilter() {
        ButtonWrapper genderWrapper = new ButtonWrapper(FXCollections.observableArrayList(Gender.values()));
        vBox.getChildren().add(genderWrapper.getGridPane());
        gender = Gender.ALL;
        genderWrapper.getButtons().stream().forEach((button) -> {
            button.setOnAction(e -> {
                gender = (Gender) genderWrapper.updateSelected(button);
                updateLabels();
            });
        });
        genderWrapper.updateSelected(genderWrapper.getItems().indexOf(gender));
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
        updateLabels();

    }

    @Override
    public void updateLabels() {
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

    public boolean isFiltered(Object p) {
        if (p instanceof Worker) {
            Worker worker = (Worker) p;
            if (!gender.equals(Gender.ALL)
                    && !gender.equals(worker.getGender())) {
                return true;
            }
        }
        return false;
    }

}
