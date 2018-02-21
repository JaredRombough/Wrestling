package wrestling.view.results;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.text.Text;
import wrestling.model.modelView.SegmentView;
import wrestling.view.utility.interfaces.ControllerBase;

public class ResultsDisplayController extends ControllerBase implements Initializable {
    
    @FXML
    private AnchorPane anchorPane;
    
    @FXML
    private FlowPane flowPane;
    
    @FXML
    private Text segmentTitle;
    
    private SegmentView segmentView;
    

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
    
    @Override
    public void setCurrent(Object obj) {
        if(obj instanceof SegmentView) {
            this.segmentView = (SegmentView)obj;
            updateLabels();
        }
    }
    
    @Override
    public void updateLabels() {
        
        
        if(segmentView != null) {
            segmentTitle.setText(gameController.getMatchManager().getMatchString(segmentView));
            populateView();
        }
        
        
        
    }
    
    private void populateView() {
        
    }

}
