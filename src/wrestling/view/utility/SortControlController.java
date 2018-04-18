package wrestling.view.utility;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
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

    private List<ButtonWrapper> buttonWrappers;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        reverseButton.setText("▼");
        buttonWrappers = new ArrayList<>();
        addGenderFilter(FXCollections.observableArrayList(Gender.values()), Gender.ALL.ordinal());

    }

    private void addGenderFilter(ObservableList list, int startingIndex) {
        ButtonWrapper wrapper = new ButtonWrapper(list);
        buttonWrappers.add(wrapper);
        vBox.getChildren().add(wrapper.getGridPane());
        wrapper.getButtons().stream().forEach((button) -> {
            button.setOnAction(e -> {
                wrapper.updateSelected(button);
                updateLabels();
            });
        });
        wrapper.updateSelected(startingIndex);
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
            if (!getFilter(Gender.class).equals(Gender.ALL)
                    && !getFilter(Gender.class).equals(worker.getGender())) {
                return true;
            }
        }
        return false;
    }

    public Object getFilter(Class<?> cls) {
        for (ButtonWrapper wrapper : buttonWrappers) {
            if (cls.isInstance(wrapper.getSelected())) {
                return wrapper.getSelected();
            }
        }
        return null;
    }

}
