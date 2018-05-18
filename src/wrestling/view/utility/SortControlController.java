package wrestling.view.utility;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import wrestling.model.Worker;
import wrestling.model.interfaces.iBrowseMode;
import wrestling.model.segmentEnum.BookingBrowseMode;
import wrestling.model.segmentEnum.BrowseMode;
import wrestling.model.segmentEnum.Gender;
import wrestling.view.utility.interfaces.ControllerBase;

public class SortControlController extends ControllerBase implements Initializable {

    @FXML
    private Button reverseButton;

    @FXML
    private ComboBox comboBox;

    @FXML
    private VBox vBox;

    @FXML
    private GridPane gridPane;

    private Comparator currentComparator;

    private ScreenCode parentScreenCode;

    private List<ButtonWrapper> buttonWrappers;

    private Gender genderFilter;

    private boolean bookingBrowseMode;

    private ComboBox<iBrowseMode> bookingBrowseComboBox;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        reverseButton.setText("▼");
        buttonWrappers = new ArrayList<>();
        genderFilter = Gender.ALL;
        bookingBrowseMode = false;
        bookingBrowseComboBox = new ComboBox(FXCollections.observableArrayList(BookingBrowseMode.values()));
        bookingBrowseComboBox.getSelectionModel().selectFirst();
        bookingBrowseComboBox.setMaxWidth(Double.MAX_VALUE);
        VBox.setMargin(bookingBrowseComboBox, new Insets(0, 5, 10, 5));
    }

    private List<Enum> getActiveFilters() {
        return Arrays.asList(genderFilter);
    }

    private void addButtonWrapper(iBrowseMode browseMode) {
        for (EnumSet set : browseMode.getSortFilters()) {

            ButtonWrapper wrapper = new ButtonWrapper(FXCollections.observableArrayList(
                    set));
            buttonWrappers.add(wrapper);
            vBox.getChildren().add(wrapper.getGridPane());
            wrapper.getButtons().stream().forEach((button) -> {
                button.setOnAction(e -> {
                    setFilter(wrapper.updateSelected(button));
                    updateLabels();
                });
            });
            if (selectedEnum(set) != null) {
                wrapper.updateSelected(selectedEnum(set));
            } else {
                wrapper.updateSelected(0);
            }

        }
    }

    private Enum selectedEnum(EnumSet set) {
        for (Enum e : getActiveFilters()) {
            if (set.contains(e)) {
                return e;
            }
        }
        return null;
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
        if (obj instanceof BrowseMode) {
            setCurrentBrowseMode((BrowseMode) obj);
        }
    }

    private void setFilter(Object obj) {
        if (obj instanceof Gender) {
            genderFilter = (Gender) obj;
        }
    }

    private void setCurrentBrowseMode(iBrowseMode browseMode) {
        vBox.getChildren().retainAll(gridPane);
        buttonWrappers.clear();
        setComparators(browseMode.comparators());
        addButtonWrapper(browseMode);
        if (bookingBrowseMode) {
            addBookingBrowseComboBox();
        }

    }

    private void addBookingBrowseComboBox() {
        vBox.getChildren().add(0, bookingBrowseComboBox);
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
            if (!genderFilter.equals(Gender.ALL)
                    && !genderFilter.equals(worker.getGender())) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param bookingBrowseMode the bookingBrowseMode to set
     */
    public void setBookingBrowseMode(boolean bookingBrowseMode) {
        this.bookingBrowseMode = bookingBrowseMode;
        if (bookingBrowseMode) {
            addBookingBrowseComboBox();
        }
    }

}
