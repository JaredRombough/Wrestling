package wrestling.view.utility;

import java.util.ArrayList;
import java.util.List;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;

public class ButtonWrapper {

    private int selectedIndex;
    private int insets = 5;
    private int rows;
    private ObservableList items;
    private List<Button> buttons;
    private GridPane gridPane;

    public ButtonWrapper(ObservableList items) {
        this(items, 5, 1);
    }

    public ButtonWrapper(ObservableList items, int insets) {
        this(items, insets, 1);

    }

    public ButtonWrapper(ObservableList items, int insets, int rows) {
        this.insets = insets;
        this.rows = rows;
        setItems(items);

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

    public Object updateSelected(Object obj) {
        selectedIndex = items.indexOf(obj);
        ViewUtils.updateSelectedButton(getButtons().get(selectedIndex), getButtons());
        return getItems().get(selectedIndex);
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

    /**
     * @param items the items to set
     */
    public void setItems(ObservableList items) {
        if (buttons == null || items.size() != this.items.size()) {
            buttons = new ArrayList<>();
            for (int i = 0; i < items.size(); i++) {
                buttons.add(new Button());
            }
        }

        this.items = items;

        if (gridPane == null) {
            if (rows == 1) {
                gridPane = ViewUtils.gridPaneWithColumns(items.size());
            } else {
                gridPane = ViewUtils.gridPaneWithDimensions(items.size() / rows, rows);
            }

        } else {
            gridPane.getChildren().clear();
        }

        int row = 0;
        int col = 0;

        for (int i = 0; i < items.size(); i++) {
            Button button = buttons.get(i);
            button.setText(items.get(i).toString());
            ViewUtils.inititializeRegion(button);
            GridPane.setConstraints(button, col, row);

            if (i == 0) {
                GridPane.setMargin(button, new Insets(0, insets, 0, 0));
            } else if (i == items.size() - 1) {
                GridPane.setMargin(button, new Insets(0, 0, 0, insets));
            } else {
                GridPane.setMargin(button, new Insets(0, insets, 0, insets));
            }

            gridPane.getChildren().addAll(button);
            buttons.add(button);
            col++;
            if (col >= items.size() / rows) {
                col = 0;
                row++;
            }
        }
    }

}
