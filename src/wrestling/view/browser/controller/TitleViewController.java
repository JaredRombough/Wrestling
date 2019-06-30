package wrestling.view.browser.controller;

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
import wrestling.model.modelView.TitleReign;
import wrestling.model.modelView.TitleView;
import wrestling.model.segmentEnum.ActiveType;
import wrestling.model.segmentEnum.BrowseMode;
import wrestling.view.utility.GameScreen;
import wrestling.view.utility.ScreenCode;
import wrestling.view.utility.ViewUtils;
import static wrestling.view.utility.ViewUtils.updateRosterSplitComboBox;
import wrestling.view.utility.interfaces.ControllerBase;

public class TitleViewController extends ControllerBase implements Initializable {

    private TitleView titleView;

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
        if (obj instanceof TitleView) {
            this.titleView = (TitleView) obj;
        } else {
            this.titleView = null;
        }
        gridPane.setVisible(this.titleView != null);
        updateLabels();
    }

    @Override
    public void updateLabels() {
        editLabel.setCurrent(titleView);

        if (titleView != null) {
            ComboBox comboBox = ViewUtils.updatePlayerComboBox(
                    activeTypeAnchorPane,
                    playerPromotion().equals(titleView.getTitle().getPromotion()),
                    Arrays.asList(ActiveType.ACTIVE, ActiveType.INACTIVE),
                    titleView.getActiveType());
            comboBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<ActiveType>() {
                @Override
                public void changed(ObservableValue<? extends ActiveType> observable, ActiveType oldValue, ActiveType newValue) {
                    titleView.getTitle().setActiveType(newValue);
                }
            });

            updateRosterSplitComboBox(rosterSplitComboBox,
                    gameController.getStableManager().getRosterSplits(),
                    titleView,
                    titleView.getTitle().getPromotion(),
                    playerPromotion());

            prestigeLabel.setText(String.valueOf(titleView.getTitle().getPrestige()));

            Comparator<TitleReign> comparator = Comparator.comparingInt(TitleReign::getSequenceNumber).reversed();
            ObservableList<TitleReign> titleReigns = FXCollections.observableArrayList(titleView.getTitleReigns());
            FXCollections.sort(titleReigns, comparator);
            listView.setItems(titleReigns);
        } else {
            editLabel.setCurrent(BrowseMode.TITLES);
        }
    }

}
