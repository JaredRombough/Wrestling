package wrestling.view;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import org.apache.logging.log4j.LogManager;
import wrestling.model.modelView.SegmentView;
import wrestling.view.utility.interfaces.ControllerBase;

public class NextDayScreenController extends ControllerBase implements Initializable {

    @FXML
    public AnchorPane displayPane;

    @FXML
    public ListView rankingsListView;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        logger = LogManager.getLogger(getClass());
    }

    @Override
    public void initializeMore() {

    }

    public void setLoadingMessage(String string) {
        Text text = new Text(string);
        displayPane.getChildren().clear();
        displayPane.getChildren().add(text);
    }

    public void nextDay() {
        updateRankings();
    }

    public void updateRankings() {
        List<SegmentView> topMatches
                = gameController.getSegmentManager().getTopMatches(gameController.getDateManager().today(), 10);

        for (SegmentView segmentView : topMatches) {
            System.out.println(gameController.getSegmentManager().getSegmentString(segmentView));
            System.out.println(segmentView.getSegment().getWorkRating());
        }
    }

}
