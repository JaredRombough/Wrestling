package wrestling.view;

import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;

import java.time.LocalDate;

class CalendarNode extends AnchorPane {

    private LocalDate date;

    public CalendarNode(Node... children) {
        super(children);
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}
