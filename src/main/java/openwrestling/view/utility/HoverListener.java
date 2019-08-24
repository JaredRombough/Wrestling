package openwrestling.view.utility;

import java.util.Objects;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import openwrestling.model.gameObjects.Promotion;

public class HoverListener implements EventHandler<MouseEvent> {

    private Promotion promotion;
    private Promotion playerPromotion;
    private Button button;

    @Override
    public void handle(MouseEvent event) {
        if (event.getEventType() == MouseEvent.MOUSE_EXITED) {
            button.setVisible(false);
        } else if (event.getEventType() == MouseEvent.MOUSE_ENTERED) {
            if (Objects.equals(promotion, playerPromotion)) {
                button.setVisible(Objects.equals(promotion, playerPromotion));
            }
        }
    }
}
