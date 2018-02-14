package wrestling.view.utility;

import com.sun.javafx.scene.control.skin.ListViewSkin;
import javafx.scene.control.ListView;

public class RefreshSkin extends ListViewSkin {

    public RefreshSkin(ListView listView) {
        super(listView);
    }

    public void refresh() {
        super.flow.recreateCells();
    }
}
