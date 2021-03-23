package openwrestling.view.browser.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import openwrestling.model.gameObjects.RosterSplit;
import openwrestling.model.gameObjects.Title;
import openwrestling.model.gameObjects.TitleReign;
import openwrestling.model.segment.constants.ActiveType;
import openwrestling.model.segment.constants.browse.mode.BrowseMode;
import openwrestling.view.utility.GameScreen;
import openwrestling.view.utility.ScreenCode;
import openwrestling.view.utility.ViewUtils;
import openwrestling.view.utility.interfaces.ControllerBase;

import java.net.URL;
import java.util.Arrays;
import java.util.Comparator;
import java.util.ResourceBundle;

import static openwrestling.view.utility.ViewUtils.updateRosterSplitComboBox;

public class TitleViewController extends ControllerBase implements Initializable {

    private Title title;

    @FXML
    private AnchorPane activeTypeAnchorPane;

    @FXML
    private AnchorPane nameAnchor;

    @FXML
    private Label prestigeLabel;

    @FXML
    private ListView listView;

    @FXML
    private GridPane gridPane;

    @FXML
    private ComboBox rosterSplitComboBox;

    private EditLabel editLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        listView.setCellFactory(param -> new ListCell<TitleReign>() {

            @Override
            public void updateItem(TitleReign titleReign, boolean empty) {
                super.updateItem(titleReign, empty);
                if (empty) {
                    setText(null);
                    setGraphic(null);
                } else {
                    GameScreen titleReignScreen = ViewUtils.loadScreenFromFXML(ScreenCode.TITLE_REIGN, mainApp, gameController);
                    titleReignScreen.controller.setCurrent(titleReign);
                    setGraphic(titleReignScreen.pane);
                }
            }
        });
    }

    @Override
    public void initializeMore() {
        GameScreen screen = ViewUtils.loadScreenFromResource(ScreenCode.EDIT_LABEL, mainApp, gameController, nameAnchor);
        editLabel = (EditLabel) screen.controller;
    }

    @Override
    public void setCurrent(Object obj) {
        if (obj instanceof Title) {
            this.title = (Title) obj;
        } else {
            this.title = null;
        }
        gridPane.setVisible(this.title != null);
        updateLabels();
    }

    @Override
    public void updateLabels() {
        editLabel.setCurrent(title);

        if (title != null) {
            ComboBox<ActiveType> comboBox = ViewUtils.updatePlayerComboBox(
                    activeTypeAnchorPane,
                    playerPromotion().equals(title.getPromotion()),
                    Arrays.asList(ActiveType.ACTIVE, ActiveType.INACTIVE),
                    title.getActiveType());
            comboBox.getSelectionModel().select(title.getActiveType());
            comboBox.getSelectionModel().selectedItemProperty()
                    .addListener((observable, oldValue, newValue) -> {
                        title.setActiveType(newValue);
                        gameController.getTitleManager().updateTitle(title);
                    });

            updateRosterSplitComboBox(rosterSplitComboBox,
                    gameController.getRosterSplitManager().getRosterSplits(),
                    title,
                    title.getPromotion(),
                    playerPromotion());
            rosterSplitComboBox.setOnAction(e -> {
                if (rosterSplitComboBox.getSelectionModel().getSelectedItem() instanceof RosterSplit) {
                    title.setRosterSplit((RosterSplit) rosterSplitComboBox.getSelectionModel().getSelectedItem());
                    gameController.getTitleManager().updateTitle(title);
                }
            });

            prestigeLabel.setText(String.valueOf(title.getPrestige()));

            Comparator<TitleReign> comparator = Comparator.comparingInt(TitleReign::getSequenceNumber).reversed();
            ObservableList<TitleReign> titleReigns = FXCollections.observableArrayList(title.getTitleReigns());
            FXCollections.sort(titleReigns, comparator);
            listView.setItems(titleReigns);
        } else {
            editLabel.setCurrent(BrowseMode.TITLES);
        }
    }

}
