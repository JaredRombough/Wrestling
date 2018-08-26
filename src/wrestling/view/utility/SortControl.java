package wrestling.view.utility;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import wrestling.model.SegmentItem;
import wrestling.model.interfaces.iBrowseMode;
import wrestling.model.modelView.WorkerView;
import wrestling.model.segmentEnum.ActiveType;
import wrestling.model.segmentEnum.BrowseMode;
import wrestling.model.segmentEnum.Gender;
import wrestling.view.utility.interfaces.ControllerBase;

public class SortControl extends ControllerBase implements Initializable {

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
    private ActiveType activeTypeFilter;

    private boolean bookingBrowseMode;

    private ComboBox<BrowseMode> bookingBrowseComboBox;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        reverseButton.setText("▼");
        buttonWrappers = new ArrayList<>();
        genderFilter = Gender.ALL;
        activeTypeFilter = ActiveType.ALL;
        bookingBrowseMode = false;
        bookingBrowseComboBox = new ComboBox(FXCollections.observableArrayList(BrowseMode.WORKERS, BrowseMode.TAG_TEAMS, BrowseMode.TITLES));
        bookingBrowseComboBox.getSelectionModel().selectFirst();
        bookingBrowseComboBox.setMaxWidth(Double.MAX_VALUE);
        bookingBrowseComboBox.valueProperty().addListener(new ChangeListener<BrowseMode>() {
            @Override
            public void changed(ObservableValue<? extends BrowseMode> observable, BrowseMode oldValue, BrowseMode newValue) {
                setBrowseMode(newValue);
                //setComparators(newValue.comparators());
            }
        });
        VBox.setMargin(getBookingBrowseComboBox(), new Insets(0, 5, 10, 5));
    }

    private List<Enum> getActiveFilters() {
        return Arrays.asList(genderFilter, activeTypeFilter);
    }

    public void clearFilters() {
        for (ButtonWrapper wrapper : buttonWrappers) {
            wrapper.getButtons().get(0).fire();
        }
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
            setBrowseMode((BrowseMode) obj);
        }
    }

    private void setFilter(Object obj) {
        if (obj instanceof Gender) {
            genderFilter = (Gender) obj;
        } else if (obj instanceof ActiveType) {
            activeTypeFilter = (ActiveType) obj;
        }
    }

    private void setBrowseMode(iBrowseMode browseMode) {
        List<Region> toRetain = new ArrayList<>(Arrays.asList(gridPane));
        if (bookingBrowseMode) {
            toRetain.add(bookingBrowseComboBox);
        }
        vBox.getChildren().retainAll(toRetain);
        buttonWrappers.clear();
        setComparators(browseMode.comparators());
        addButtonWrapper(browseMode);
    }

    private void addBookingBrowseComboBox() {
        vBox.getChildren().add(0, getBookingBrowseComboBox());
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

    public boolean isFiltered(Object object) {
        if (object instanceof SegmentItem) {
            SegmentItem segmentItem = (SegmentItem) object;
            return isActiveFiltered(segmentItem) || isGenderFiltered(segmentItem);
        }
        return true;
    }

    private boolean isActiveFiltered(SegmentItem segmentItem) {
        if (activeTypeFilter.equals(ActiveType.ALL)
                || segmentItem.getActiveType().equals(ActiveType.ALL)) {
            return false;
        }
        return !activeTypeFilter.equals(segmentItem.getActiveType());
    }

    private boolean isGenderFiltered(SegmentItem segmentItem) {
        if (!genderFilter.equals(Gender.ALL)) {
            for (SegmentItem subItem : segmentItem.getSegmentItems()) {
                if (subItem instanceof WorkerView
                        && !((WorkerView) subItem).getGender().equals(genderFilter)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * @param bookingBrowseMode the bookingBrowseMode to set
     */
    public void setBookingBrowseModeEnabled(boolean bookingBrowseMode) {
        this.bookingBrowseMode = bookingBrowseMode;
        if (bookingBrowseMode) {
            addBookingBrowseComboBox();
        }
    }

    /**
     * @return the bookingBrowseComboBox
     */
    public ComboBox<BrowseMode> getBookingBrowseComboBox() {
        return bookingBrowseComboBox;
    }

}
