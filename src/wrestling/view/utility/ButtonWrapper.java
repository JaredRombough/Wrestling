package wrestling.view.utility;

import java.util.ArrayList;
import java.util.List;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;

public class ButtonWrapper {

    private int selectedIndex;
    private ObservableList items;
    private List<Button> buttons;
    private GridPane gridPane;

    public ButtonWrapper(ObservableList items) {
        this.items = items;

        buttons = new ArrayList<>();

        gridPane = ViewUtils.gridPaneWithColumns(items.size());

        for (int i = 0; i < items.size(); i++) {
            Button button = new Button();
            button.setText(items.get(i).toString());
            ViewUtils.inititializeRegion(button);
            GridPane.setConstraints(button, i, 0);
            GridPane.setMargin(button, new Insets(5));
            gridPane.getChildren().addAll(button);
            buttons.add(button);
        }

        gridPane.setMaxWidth(Double.MAX_VALUE);

    }

    public Object getSelected() {
        return getItems().get(selectedIndex);
    }

    public Object updateSelected(Button button) {
        return updateSelected(buttons.indexOf(button));
    }

    public Object updateSelected(int index) {
        selectedIndex = index;
        ViewUtils.updateSelectedButton(getButtons().get(index), getButtons());
        return getItems().get(index);
    }

    /**
     * @param buttons the buttons to set
     */
    public void setButtons(List<Button> buttons) {
        this.buttons = buttons;
    }

    /**
     * @return the buttons
     */
    public List<Button> getButtons() {
        return buttons;
    }

    /**
     * @return the items
     */
    public ObservableList getItems() {
        return items;
    }

    /**
     * @return the gridPane
     */
    public GridPane getGridPane() {
        return gridPane;
    }

}
