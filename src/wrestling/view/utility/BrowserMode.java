package wrestling.view.utility;

import java.io.IOException;
import java.util.List;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import wrestling.MainApp;
import wrestling.model.controller.GameController;
import wrestling.view.ControllerBase;

public class BrowserMode<T> {

    private SortedList sortedList;

    private ListView listView = new ListView<>();

    private AnchorPane displayPane;

    private ControllerBase controller;

    private ObservableList comparators;

    public BrowserMode(List initialItems, String fxmlPath, ObservableList comparators, MainApp mainApp, GameController gameController) throws IOException {

        //load the display pane and its controller
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(MainApp.class.getResource(fxmlPath));
        displayPane = (AnchorPane) loader.load();

        ViewUtils.inititializeRegion(listView);
        ViewUtils.inititializeRegion(displayPane);

        controller = loader.getController();
        
        this.comparators = comparators;

        controller.setDependencies(mainApp, gameController);

        //get the listview ready
        listView.setItems(FXCollections.observableArrayList(initialItems));

        //listen for changes in selection on the listview
        listView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<T>() {
            @Override
            public void changed(ObservableValue<? extends T> observable, T oldValue, T newValue) {

                //tell the controller what object we're looking at
                getController().setCurrent(newValue);

            }
        });

    }

    /**
     * @return the sortedList
     */
    public SortedList getSortedList() {
        return sortedList;
    }

    /**
     * @return the listView
     */
    public ListView getListView() {
        return listView;
    }

    /**
     * @return the displayPane
     */
    public AnchorPane getDisplayPane() {
        return displayPane;
    }

    /**
     * @return the controller
     */
    public ControllerBase getController() {
        return controller;
    }

    /**
     * @return the comparators
     */
    public ObservableList getComparators() {
        return comparators;
    }

    /**
     * @param sortedList the sortedList to set
     */
    public void setSortedList(SortedList sortedList) {
        this.sortedList = sortedList;
        listView.setItems(sortedList);
    }

    /**
     * @param listView the listView to set
     */
    public void setListView(ListView listView) {
        this.listView = listView;
    }

    /**
     * @param displayPane the displayPane to set
     */
    public void setDisplayPane(AnchorPane displayPane) {
        this.displayPane = displayPane;
    }

    /**
     * @param controller the controller to set
     */
    public void setController(ControllerBase controller) {
        this.controller = controller;
    }

    /**
     * @param comparators the comparators to set
     */
    public void setComparators(ObservableList comparators) {
        this.comparators = comparators;
    }

}
