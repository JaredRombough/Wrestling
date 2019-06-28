package wrestling.view.utility;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import wrestling.model.SegmentItem;
import wrestling.model.interfaces.iBrowseMode;
import wrestling.model.interfaces.iNewsItem;
import wrestling.model.modelView.PromotionView;
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

    private List<ButtonWrapper> buttonWrappers;
    private List<ComboBox> filterComboBoxes;

    private Gender genderFilter;
    private ActiveType activeTypeFilter;
    private StaffType staffTypeFilter;
    private NewsFilter newsFilter;

    private WorkerGroup stableFilter;
    private WorkerGroup rosterSplitFilter;
    private ComboBox stablesCombobox;
    private ComboBox rosterSplitCombobox;

    private List<WorkerGroup> stables;
    private List<WorkerGroup> rosterSplits;

    private final String ALL_ROSTER_SPLITS = "All Roster Splits";
    private final String ALL_STABLES = "All Stables";

    private EventHandler<ActionEvent> updateAction;

    private final ChangeListener filterChangeListener = new ChangeListener<Object>() {
        @Override
        public void changed(ObservableValue<? extends Object> observable, Object oldValue, Object newValue) {
            setFilter(newValue);
            updateAction.handle(new ActionEvent());
        }
    };

    private iBrowseMode browseMode;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        reverseButton.setText("▼");
        buttonWrappers = new ArrayList<>();
        filterComboBoxes = new ArrayList<>();
        stables = new ArrayList<>();
        rosterSplits = new ArrayList<>();
        stablesCombobox = new ComboBox();
        rosterSplitCombobox = new ComboBox();
        genderFilter = Gender.ALL;
        activeTypeFilter = ActiveType.ALL;
        staffTypeFilter = StaffType.ALL;

        comparatorsComboBox.valueProperty().addListener((obs, oldItem, newItem) -> {
            if (newItem != null && updateAction != null) {
                setCurrentComparator((Comparator) newItem);
                updateAction.handle(new ActionEvent());
            }
        });

        reverseButton.setOnAction(e -> {
            reverseButton.setText(
                    reverseButton.getText().equals("▲")
                    ? "▼" : "▲");

            setCurrentComparator(currentComparator.reversed());
        });
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
            WorkerGroup workerGroup = (WorkerGroup) obj;
            if (stables != null && stables.contains(workerGroup)) {
                stableFilter = workerGroup;
            } else {
                rosterSplitFilter = workerGroup;
            }
        } else if (obj instanceof String) {
            String string = (String) obj;
            if (StringUtils.equals(string, ALL_STABLES)) {
                stableFilter = null;
            } else if (StringUtils.equals(string, ALL_ROSTER_SPLITS)) {
                rosterSplitFilter = null;
            }
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
                updateAction.handle(e);
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

        comboBox.getSelectionModel().selectedItemProperty().addListener(filterChangeListener);

        if (selectedEnum(set) != null) {
            comboBox.getSelectionModel().select(selectedEnum(set));
        } else {
            comboBox.getSelectionModel().selectFirst();
        }

    }

    private void addWorkerGroupFilter(List<WorkerGroup> list, ComboBox comboBox, String noFilterString) {
        List<Object> listForComobBox = new ArrayList<>(list);
        listForComobBox.add(0, noFilterString);
        Object selected = comboBox.getSelectionModel().getSelectedItem();
        comboBox.setItems(FXCollections.observableArrayList(listForComobBox));
        comboBox.getSelectionModel().selectFirst();
        comboBox.setOnAction(updateAction);

        comboBox.setMaxWidth(Double.MAX_VALUE);

        filterComboBoxes.add(comboBox);
        vBox.getChildren().add(comboBox);

        comboBox.getSelectionModel().selectedItemProperty().addListener(filterChangeListener);

        if (comboBox.getItems().contains(selected)) {
            comboBox.getSelectionModel().select(selected);
        } else {
            comboBox.getSelectionModel().selectFirst();
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

    public void setBrowseMode(iBrowseMode browseMode) {
        this.browseMode = browseMode;
        updateFilters();
    }

    private void updateFilters() {
        if (browseMode == null) {
            return;
        }
        vBox.getChildren().retainAll(Arrays.asList(gridPane));
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
            if (CollectionUtils.isNotEmpty(stables)) {
                addWorkerGroupFilter(stables, stablesCombobox, ALL_STABLES);
            }
            if (CollectionUtils.isNotEmpty(rosterSplits)) {
                addWorkerGroupFilter(rosterSplits, rosterSplitCombobox, ALL_ROSTER_SPLITS);
            }
        }
    }

    private void setCurrentComparator(Comparator comparator) {
        currentComparator = comparator;
        updateLabels();

    }

    private void setComparators(ObservableList<Comparator> comparators) {
        comparatorsComboBox.setItems(comparators);

        comparatorsComboBox.getSelectionModel().selectFirst();
    }

    /**
     * @return the currentComparator
     */
    public Comparator getCurrentComparator() {
        return currentComparator;
    }

    public boolean isFiltered(Object object) {
        if (object instanceof SegmentItem) {
            SegmentItem segmentItem = (SegmentItem) object;
            return isActiveFiltered(segmentItem) || isGenderFiltered(segmentItem) || isStaffTypeFiltered(segmentItem) || isWorkerGroupFiltered(segmentItem);
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

    private boolean isWorkerGroupFiltered(SegmentItem segmentItem) {
        if (stableFilter != null && (segmentItem instanceof WorkerView || segmentItem instanceof TagTeamView)) {
            for (SegmentItem subItem : segmentItem.getSegmentItems()) {
                if (!stableFilter.getWorkers().contains((WorkerView) subItem)) {
                    return true;
                }
            }
        }
        if (rosterSplitFilter != null && (segmentItem instanceof WorkerView || segmentItem instanceof TagTeamView)) {
            for (SegmentItem subItem : segmentItem.getSegmentItems()) {
                if (!rosterSplitFilter.getWorkers().contains((WorkerView) subItem)) {
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

    public void setCurrentPromotion(PromotionView promotion) {
        stables = gameController.getStableManager().getStables().stream()
                .filter(s -> s.getOwner().equals(promotion))
                .collect(Collectors.toList());

        rosterSplits = gameController.getStableManager().getRosterSplits().stream()
                .filter(s -> s.getOwner().equals(promotion))
                .collect(Collectors.toList());
        updateFilters();
    }

    public void setUpdateAction(EventHandler<ActionEvent> action) {
        this.updateAction = action;
    }

}
