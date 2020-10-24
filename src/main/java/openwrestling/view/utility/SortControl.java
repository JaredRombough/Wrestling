package openwrestling.view.utility;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import openwrestling.model.gameObjects.Promotion;
import openwrestling.model.gameObjects.RosterSplit;
import openwrestling.model.gameObjects.Stable;
import openwrestling.model.segment.constants.ActiveType;
import openwrestling.model.segment.constants.Gender;
import openwrestling.model.segment.constants.NewsFilter;
import openwrestling.model.segment.constants.StaffType;
import openwrestling.model.segment.constants.TopMatchFilter;
import openwrestling.model.segment.constants.browse.mode.BrowseMode;
import openwrestling.view.utility.interfaces.ControllerBase;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import static openwrestling.model.constants.StringConstants.ALL_ROSTER_SPLITS;
import static openwrestling.model.constants.StringConstants.ALL_STABLES;
import static openwrestling.model.constants.UIConstants.DOWN_ARROW;
import static openwrestling.model.constants.UIConstants.UP_ARROW;

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

    private FilterService filterService;

    private ComboBox stablesCombobox;
    private ComboBox rosterSplitCombobox;

    private List<Stable> stables;
    private List<RosterSplit> rosterSplits;

    private EventHandler<ActionEvent> updateAction;

    private final ChangeListener<?> filterChangeListener = new ChangeListener<>() {
        @Override
        public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
            setFilter(newValue);
            updateAction.handle(new ActionEvent());
        }
    };

    private BrowseMode browseMode;
    private Promotion promotion;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        reverseButton.setText(DOWN_ARROW);
        buttonWrappers = new ArrayList<>();
        filterComboBoxes = new ArrayList<>();
        stables = new ArrayList<>();
        rosterSplits = new ArrayList<>();
        stablesCombobox = new ComboBox();
        rosterSplitCombobox = new ComboBox();

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

    @Override
    public void initializeMore() {
        filterService = new FilterService(playerPromotion(), gameController.getDateManager());
    }

    public <T> SortedList<T> getSortedList(List<T> inputList) {
        return filterService.getSortedList(inputList, getCurrentComparator());
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
            filterService.setGenderFilter((Gender) obj);
        } else if (obj instanceof ActiveType) {
            filterService.setActiveTypeFilter((ActiveType) obj);
        } else if (obj instanceof StaffType) {
            filterService.setStaffTypeFilter((StaffType) obj);
        } else if (obj instanceof NewsFilter) {
            filterService.setNewsFilter((NewsFilter) obj);
        } else if (obj instanceof Stable) {
            Stable stable = (Stable) obj;
            if (stables != null && stables.contains(stable)) {
                filterService.setStableFilter((Stable) obj);
            }
        } else if (obj instanceof RosterSplit) {
            RosterSplit stable = (RosterSplit) obj;
            if (rosterSplits != null && rosterSplits.contains(stable)) {
                filterService.setRosterSplitFilter((RosterSplit) obj);
            }
        } else if (obj instanceof String) {
            String string = (String) obj;
            if (StringUtils.equals(string, ALL_STABLES)) {
                filterService.setStableFilter(null);
            } else if (StringUtils.equals(string, ALL_ROSTER_SPLITS)) {
                filterService.setRosterSplitFilter(null);
            }
        } else if (obj instanceof TopMatchFilter) {
            filterService.setTopMatchFilter((TopMatchFilter) obj);
        }

        filterComboBoxes.forEach(comboBox -> {
            if (comboBox.getItems().contains(obj)
                    && !comboBox.getSelectionModel().getSelectedItem().equals(obj)) {
                comboBox.getSelectionModel().select(obj);
            }
        });
    }

    public void setBrowseMode(BrowseMode browseMode) {
        this.browseMode = browseMode;
        if (browseMode.equals(BrowseMode.NEWS)) {
            addButtonWrapper(EnumSet.allOf(NewsFilter.class));
            gridPane.setVisible(false);
        } else {
            comparatorsComboBox.setItems(FXCollections.observableArrayList(browseMode.getComparators()));
            comparatorsComboBox.getSelectionModel().selectFirst();
            updateWorkerGroups();
            updateFilters();
        }
    }

    public void setCurrentPromotion(Promotion promotion) {
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

        rosterSplits = gameController.getRosterSplitManager().getRosterSplits().stream()
                .filter(s -> s.getOwner().equals(promotion))
                .collect(Collectors.toList());
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
        Enum selectedEnum = filterService.selectedEnum(set);

        if (selectedEnum != null) {
            wrapper.updateSelected(selectedEnum);
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

        Enum selectedEnum = filterService.selectedEnum(set);

        if (selectedEnum != null) {
            comboBox.getSelectionModel().select(selectedEnum);
        } else {
            comboBox.getSelectionModel().selectFirst();
        }
    }

    private void addWorkerGroupFilter(List list, ComboBox comboBox, String noFilterString) {
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

    private void updateFilters() {
        if (browseMode == null) {
            return;
        }
        vBox.getChildren().retainAll(Arrays.asList(gridPane));
        buttonWrappers.clear();
        filterComboBoxes.clear();
        for (Class<? extends Enum> filterEnum : browseMode.getSortFilters()) {
            EnumSet set = EnumSet.allOf(filterEnum);
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

    private Comparator getCurrentComparator() {
        return reverseButton.getText().equals(DOWN_ARROW)
                ? comparatorsComboBox.getSelectionModel().getSelectedItem()
                : comparatorsComboBox.getSelectionModel().getSelectedItem().reversed();
    }

}
