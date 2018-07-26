package wrestling.view.browser.controller;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import wrestling.model.EventTemplate;
import wrestling.model.SegmentItem;
import wrestling.model.modelView.TagTeamView;
import wrestling.model.modelView.TitleView;
import wrestling.view.utility.ScreenCode;
import wrestling.view.utility.ViewUtils;
import wrestling.view.utility.interfaces.ControllerBase;

public class EditLabel extends ControllerBase implements Initializable {

    @FXML
    Label label;

    @FXML
    private Button editButton;

    @FXML
    private Button createButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        label.setText("");
    }

    /**
     * @return the button
     */
    public Button getEditButton() {
        return editButton;
    }

    @Override
    public void setCurrent(Object object) {
        if (object instanceof SegmentItem) {
            SegmentItem segmentItem = (SegmentItem) object;
            label.setText(segmentItem.getLongName());
            editButton.setOnAction(e -> {
                if (object instanceof TitleView) {
                    TitleView titleView = (TitleView) object;
                    titleView.getTitle().setName(ViewUtils.editTextDialog(titleView.getTitle().getName()));
                } else if (object instanceof EventTemplate) {
                    EventTemplate eventTemplate = (EventTemplate) object;
                    eventTemplate.setName(ViewUtils.editTextDialog(eventTemplate.getName()));
                    gameController.getEventManager().updateEventName(eventTemplate);
                } else if (object instanceof TagTeamView) {
                    TagTeamView tagTeamView = (TagTeamView) object;
                    tagTeamView.getTagTeam().setName(ViewUtils.editTextDialog(tagTeamView.getTagTeam().getName()));
                }
                updateLabels();
                mainApp.updateLabels(ScreenCode.BROWSER);
            });
            createButton.setOnAction(e -> {
                if (segmentItem instanceof EventTemplate) {
                    mainApp.show(ScreenCode.CALENDAR);
                } else {
                    Optional<? extends SegmentItem> optionalResult = Optional.empty();
                    if (segmentItem instanceof TitleView) {
                        optionalResult = ViewUtils.createTitleViewDialog(gameController).showAndWait();
                    } else if (segmentItem instanceof TagTeamView) {
                        CreateTagTeamDialog createTagTeamDialog = new CreateTagTeamDialog();
                        optionalResult = createTagTeamDialog.getDialog(gameController).showAndWait();
                    }

                    optionalResult.ifPresent((SegmentItem newSegmentItem) -> {
                        mainApp.show(ScreenCode.BROWSER, newSegmentItem);
                    });
                }
            });

        } else {
            label.setText("");
        }
        editButton.setVisible(object != null);

    }

    /**
     * @return the createButton
     */
    public Button getCreateButton() {
        return createButton;
    }

}
