package wrestling.view.calendar.controller;

import java.time.LocalDate;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;

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
