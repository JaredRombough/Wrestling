package openwrestling.view.browser.controller;

import java.net.URL;
import java.util.Arrays;
import java.util.Comparator;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
import openwrestling.model.gameObjects.TitleReign;
import openwrestling.model.gameObjects.Title;
import openwrestling.model.segmentEnum.ActiveType;
import openwrestling.model.segmentEnum.BrowseMode;
import openwrestling.view.utility.GameScreen;
import openwrestling.view.utility.ScreenCode;
import openwrestling.view.utility.ViewUtils;
import static openwrestling.view.utility.ViewUtils.updateRosterSplitComboBox;
import openwrestling.view.utility.interfaces.ControllerBase;

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
            ComboBox comboBox = ViewUtils.updatePlayerComboBox(
                    activeTypeAnchorPane,
                    playerPromotion().equals(title.getPromotion()),
                    Arrays.asList(ActiveType.ACTIVE, ActiveType.INACTIVE),
                    title.getActiveType());
            comboBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<ActiveType>() {
                @Override
                public void changed(ObservableValue<? extends ActiveType> observable, ActiveType oldValue, ActiveType newValue) {
                    title.setActiveType(newValue);
                }
            });

            updateRosterSplitComboBox(rosterSplitComboBox,
                    gameController.getRosterSplitManager().getRosterSplits(),
                    title,
                    title.getPromotion(),
                    playerPromotion());

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
