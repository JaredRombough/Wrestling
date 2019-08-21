package openwrestling.view.utility;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
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
import openwrestling.model.NewsItem;
import openwrestling.model.SegmentItem;
import static openwrestling.model.constants.StringConstants.ALL_ROSTER_SPLITS;
import static openwrestling.model.constants.StringConstants.ALL_STABLES;
import static openwrestling.model.constants.UIConstants.DOWN_ARROW;
import static openwrestling.model.constants.UIConstants.UP_ARROW;
import openwrestling.model.interfaces.iBrowseMode;
import openwrestling.model.interfaces.iNewsItem;
import openwrestling.model.modelView.PromotionView;
import openwrestling.model.modelView.StaffView;
import openwrestling.model.modelView.TagTeamView;
import openwrestling.model.modelView.TitleView;
import openwrestling.model.modelView.WorkerGroup;
import openwrestling.model.modelView.WorkerView;
import openwrestling.model.segmentEnum.ActiveType;
import openwrestling.model.segmentEnum.BrowseMode;
import openwrestling.model.segmentEnum.Gender;
import openwrestling.model.segmentEnum.NewsFilter;
import openwrestling.model.segmentEnum.StaffType;
import openwrestling.view.utility.interfaces.ControllerBase;

public class SortControl extends ControllerBase implements Initializable {

    @FXML
    private Button reverseButton;

    @FXML
    private ComboBox<Comparator> comparatorsComboBox;

    @FXML
    private VBox vBox;

    @FXML
    private GridPane gridPane;

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

    private EventHandler<ActionEvent> updateAction;

    private final ChangeListener filterChangeListener = new ChangeListener<Object>() {
        @Override
        public void changed(ObservableValue<? extends Object> observable, Object oldValue, Object newValue) {
            setFilter(newValue);
            updateAction.handle(new ActionEvent());
        }
    };

    private iBrowseMode browseMode;
    private PromotionView promotion;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        reverseButton.setText(DOWN_ARROW);
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
                updateAction.handle(new ActionEvent());
            }
        });

        reverseButton.setOnAction(e -> {
            reverseButton.setText(
                    reverseButton.getText().equals(UP_ARROW)
                    ? DOWN_ARROW : UP_ARROW);
            updateAction.handle(new ActionEvent());
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

    private void updateFilters() {
        if (browseMode == null) {
            return;
        }
        vBox.getChildren().retainAll(Arrays.asList(gridPane));
        buttonWrappers.clear();
        filterComboBoxes.clear();
        for (EnumSet set : browseMode.getSortFilters()) {
            if (set.size() > 5) {
                addComboboxFilter(set);
            } else {
                addButtonWrapper(set);
            }
        }
        if (browseMode.equals(BrowseMode.WORKERS) || browseMode.equals(BrowseMode.TAG_TEAMS) || browseMode.equals(BrowseMode.TITLES)) {
            if (CollectionUtils.isNotEmpty(stables) && !browseMode.equals(BrowseMode.TITLES)) {
                addWorkerGroupFilter(stables, stablesCombobox, ALL_STABLES);
            }
            if (CollectionUtils.isNotEmpty(rosterSplits)) {
                addWorkerGroupFilter(rosterSplits, rosterSplitCombobox, ALL_ROSTER_SPLITS);
            }
        }
    }

    /**
     * @return the currentComparator
     */
    public Comparator getCurrentComparator() {
        return reverseButton.getText().equals(DOWN_ARROW)
                ? comparatorsComboBox.getSelectionModel().getSelectedItem()
                : comparatorsComboBox.getSelectionModel().getSelectedItem().reversed();
    }

    public boolean isFiltered(Object object) {
        if (object instanceof SegmentItem) {
            SegmentItem segmentItem = (SegmentItem) object;
            return isActiveFiltered(segmentItem) || isGenderFiltered(segmentItem) || isStaffTypeFiltered(segmentItem) || isWorkerGroupFiltered(segmentItem);
        }
        return true;
    }

    public boolean isNewsItemFiltered(NewsItem newsItem) {
        if (newsFilter.equals(NewsFilter.ALL)) {
            return false;
        }
        return !newsItem.getPromotions().contains(playerPromotion());
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
        return isStableFiltered(segmentItem) || isRosterSplitFiltered(segmentItem);
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

    private boolean isRosterSplitFiltered(SegmentItem segmentItem) {
        if (rosterSplitFilter != null && (segmentItem instanceof WorkerView || segmentItem instanceof TagTeamView || segmentItem instanceof TitleView)) {
            for (SegmentItem subItem : segmentItem.getSegmentItems()) {
                if (segmentItem instanceof TitleView) {
                    return !Objects.equals(((TitleView) segmentItem).getRosterSplit(), rosterSplitFilter);
                } else if (!rosterSplitFilter.getWorkers().contains((WorkerView) subItem)) {
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

    public void setBrowseMode(iBrowseMode browseMode) {
        this.browseMode = browseMode;
        comparatorsComboBox.setItems(browseMode.comparators());
        comparatorsComboBox.getSelectionModel().selectFirst();
        updateWorkerGroups();
        updateFilters();
    }

    public void setCurrentPromotion(PromotionView promotion) {
        this.promotion = promotion;
        updateWorkerGroups();
        updateFilters();
    }

    public void setUpdateAction(EventHandler<ActionEvent> action) {
        this.updateAction = action;
    }

    private void updateWorkerGroups() {
        stables = gameController.getStableManager().getStables().stream()
                .filter(s -> s.getOwner().equals(promotion))
                .collect(Collectors.toList());

        rosterSplits = gameController.getStableManager().getRosterSplits().stream()
                .filter(s -> s.getOwner().equals(promotion))
                .collect(Collectors.toList());
    }

}
