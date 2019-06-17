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
import wrestling.model.interfaces.iNewsItem;
import wrestling.model.modelView.WorkerGroup;
import wrestling.model.modelView.StaffView;
import wrestling.model.modelView.TagTeamView;
import wrestling.model.modelView.WorkerView;
import wrestling.model.segmentEnum.ActiveType;
import wrestling.model.segmentEnum.BrowseMode;
import wrestling.model.segmentEnum.Gender;
import wrestling.model.segmentEnum.NewsFilter;
import wrestling.model.segmentEnum.StaffType;
import wrestling.view.utility.interfaces.ControllerBase;

public class SortControl extends ControllerBase implements Initializable {

    @FXML
    private Button reverseButton;

    @FXML
    private ComboBox comparatorsComboBox;

    @FXML
    private VBox vBox;

    @FXML
    private GridPane gridPane;

    private Comparator currentComparator;

    private ScreenCode parentScreenCode;

    private List<ButtonWrapper> buttonWrappers;
    private List<ComboBox> filterComboBoxes;

    private Gender genderFilter;
    private ActiveType activeTypeFilter;
    private StaffType staffTypeFilter;
    private NewsFilter newsFilter;

    private WorkerGroup stableFilter;
    private ComboBox stablesCombobox;

    private boolean bookingBrowseMode;

    private ComboBox<BrowseMode> bookingBrowseComboBox;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        reverseButton.setText("▼");
        buttonWrappers = new ArrayList<>();
        filterComboBoxes = new ArrayList<>();
        stablesCombobox = new ComboBox();
        genderFilter = Gender.ALL;
        activeTypeFilter = ActiveType.ALL;
        staffTypeFilter = StaffType.ALL;
        bookingBrowseMode = false;
        bookingBrowseComboBox = new ComboBox(FXCollections.observableArrayList(
                BrowseMode.WORKERS,
                BrowseMode.TAG_TEAMS,
                BrowseMode.STABLES,
                BrowseMode.TITLES,
                BrowseMode.REFS,
                BrowseMode.BROADCAST
        ));
        bookingBrowseComboBox.getSelectionModel().selectFirst();
        bookingBrowseComboBox.setMaxWidth(Double.MAX_VALUE);
        bookingBrowseComboBox.valueProperty().addListener(new ChangeListener<BrowseMode>() {
            @Override
            public void changed(ObservableValue<? extends BrowseMode> observable, BrowseMode oldValue, BrowseMode newValue) {
                setBrowseMode(newValue);
            }
        });
        VBox.setMargin(getBookingBrowseComboBox(), new Insets(0, 5, 10, 5));
    }

    private List<Enum> getActiveFilters() {
        return Arrays.asList(genderFilter, activeTypeFilter, staffTypeFilter);
    }

    public void clearFilters() {
        for (ButtonWrapper wrapper : buttonWrappers) {
            wrapper.getButtons().get(0).fire();
        }
        for (ComboBox comboBox : filterComboBoxes) {
            comboBox.getSelectionModel().selectFirst();
        }
    }

    @Override
    public void setCurrent(Object obj) {
        if (obj instanceof BrowseMode) {
            setBrowseMode((BrowseMode) obj);
        }
    }

    public void setFilter(Object obj) {
        if (obj instanceof Gender) {
            genderFilter = (Gender) obj;
        } else if (obj instanceof ActiveType) {
            activeTypeFilter = (ActiveType) obj;
        } else if (obj instanceof StaffType) {
            staffTypeFilter = (StaffType) obj;
        } else if (obj instanceof NewsFilter) {
            newsFilter = (NewsFilter) obj;
        } else if (obj instanceof WorkerGroup) {
            stableFilter = (WorkerGroup) obj;
        } else if (obj instanceof String) {
            stableFilter = null;
        }

        filterComboBoxes.stream().forEach(comboBox -> {
            if (comboBox.getItems().contains(obj)
                    && !comboBox.getSelectionModel().getSelectedItem().equals(obj)) {
                comboBox.getSelectionModel().select(obj);
            }
        });
    }

    public void setNewsMode() {
        addButtonWrapper(EnumSet.allOf(NewsFilter.class));
        gridPane.setVisible(false);
    }

    private void addButtonWrapper(EnumSet set) {
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

    private void addComboboxFilter(EnumSet set) {

        ComboBox comboBox = new ComboBox(FXCollections.observableArrayList(set));
        comboBox.setMaxWidth(Double.MAX_VALUE);

        filterComboBoxes.add(comboBox);
        vBox.getChildren().add(comboBox);

        comboBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Object>() {
            @Override
            public void changed(ObservableValue<? extends Object> observable, Object oldValue, Object newValue) {
                setFilter(newValue);
                updateLabels();
            }
        });

        if (selectedEnum(set) != null) {
            comboBox.getSelectionModel().select(selectedEnum(set));
        } else {
            comboBox.getSelectionModel().selectFirst();
        }

    }

    private void addStableFilter() {
        stablesCombobox.setMaxWidth(Double.MAX_VALUE);

        filterComboBoxes.add(stablesCombobox);
        vBox.getChildren().add(stablesCombobox);

        stablesCombobox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Object>() {
            @Override
            public void changed(ObservableValue<? extends Object> observable, Object oldValue, Object newValue) {
                setFilter(newValue);
                updateLabels();
            }
        });

        stablesCombobox.getSelectionModel().selectFirst();
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

    private void setBrowseMode(iBrowseMode browseMode) {
        List<Region> toRetain = new ArrayList<>(Arrays.asList(gridPane));
        if (bookingBrowseMode) {
            toRetain.add(bookingBrowseComboBox);
        }
        vBox.getChildren().retainAll(toRetain);
        buttonWrappers.clear();
        filterComboBoxes.clear();
        setComparators(browseMode.comparators());
        for (EnumSet set : browseMode.getSortFilters()) {
            if (set.size() > 5) {
                addComboboxFilter(set);
            } else {
                addButtonWrapper(set);
            }
        }
        if (browseMode.equals(BrowseMode.WORKERS) || browseMode.equals(BrowseMode.TAG_TEAMS)) {
            addStableFilter();
        }

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
        comparatorsComboBox.setItems(comparators);

        comparatorsComboBox.valueProperty().addListener((obs, oldItem, newItem) -> {
            if (newItem != null) {
                setCurrentComparator((Comparator) newItem);
            }
        });

        comparatorsComboBox.getSelectionModel().selectFirst();
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
            return isActiveFiltered(segmentItem) || isGenderFiltered(segmentItem) || isStaffTypeFiltered(segmentItem) || isStableFiltered(segmentItem);
        }
        return true;
    }

    public boolean isNewsItemFiltered(iNewsItem newsItem) {
        if (newsFilter.equals(NewsFilter.ALL)) {
            return false;
        }
        return !playerPromotion().equals(newsItem.getPromotion());
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

    private boolean isStableFiltered(SegmentItem segmentItem) {
        if (stableFilter != null && (segmentItem instanceof WorkerView || segmentItem instanceof TagTeamView)) {
            for (SegmentItem subItem : segmentItem.getSegmentItems()) {
                if (!stableFilter.getWorkers().contains((WorkerView) subItem)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isStaffTypeFiltered(SegmentItem segmentItem) {
        if (!staffTypeFilter.equals(StaffType.ALL)) {
            for (SegmentItem subItem : segmentItem.getSegmentItems()) {
                if (subItem instanceof StaffView
                        && !((StaffView) subItem).getStaffType().equals(staffTypeFilter)) {
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

    /**
     * @param stables the stables to set
     */
    public void setStables(List<WorkerGroup> stables) {
        if (stables.isEmpty()) {
            stablesCombobox.setVisible(false);
        } else {
            stablesCombobox.setVisible(true);
            List<Object> listForComobBox = new ArrayList<>(stables);
            listForComobBox.add(0, "All");
            stablesCombobox.setItems(FXCollections.observableArrayList(listForComobBox));
            stablesCombobox.getSelectionModel().selectFirst();
        }

    }

}
