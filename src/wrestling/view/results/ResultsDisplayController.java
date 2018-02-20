package wrestling.view.results;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import wrestling.model.Worker;
import wrestling.model.modelView.SegmentView;
import wrestling.view.interfaces.ControllerBase;

public class ResultsDisplayController extends ControllerBase implements Initializable {
    
    @FXML
    private AnchorPane anchorPane;
    
    @FXML
    private FlowPane flowPane;
    
    @FXML
    private Text segmentTitle;
    
    private SegmentView segment;
    

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
    
    @Override
    public void setCurrent(Object obj) {
        if(obj instanceof SegmentView) {
            this.segment = (SegmentView)obj;
            updateLabels();
        }
    }
    
    @Override
    public void updateLabels() {
        
        
        if(segment != null) {
            segmentTitle.setText(segment.toString());
            populateView();
        }
        
        
        
    }
    
    private void populateView() {
        
    }

}
