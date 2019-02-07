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
import wrestling.model.modelView.StableView;
import wrestling.model.modelView.TagTeamView;
import wrestling.model.modelView.TitleView;
import wrestling.model.segmentEnum.BrowseMode;
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

    @Override
    public void setCurrent(Object object) {
        SegmentItem segmentItem = object instanceof SegmentItem ? (SegmentItem) object : null;
        BrowseMode browseMode = object instanceof BrowseMode ? (BrowseMode) object : null;

        if (segmentItem != null) {
            label.setText(segmentItem.getLongName());
            editButton.setOnAction(e -> {
                if (segmentItem instanceof TitleView) {
                    TitleView titleView = (TitleView) object;
                    titleView.getTitle().setName(ViewUtils.editTextDialog(titleView.getTitle().getName()));
                } else if (segmentItem instanceof EventTemplate) {
                    EventTemplate eventTemplate = (EventTemplate) object;
                    eventTemplate.setName(ViewUtils.editTextDialog(eventTemplate.getName()));
                    gameController.getEventManager().updateEventName(eventTemplate);
                } else if (segmentItem instanceof TagTeamView) {
                    TagTeamView tagTeamView = (TagTeamView) object;
                    tagTeamView.getTagTeam().setName(ViewUtils.editTextDialog(tagTeamView.getTagTeam().getName()));
                } else if (segmentItem instanceof StableView) {
                    StableView stable = (StableView) object;
                    stable.setName(ViewUtils.editTextDialog(stable.getName()));
                }
                updateLabels();
                mainApp.updateLabels(ScreenCode.BROWSER);
            });

        } else {
            label.setText("");
        }
        editButton.setVisible(segmentItem != null);

        createButton.setOnAction(e -> {
            if (segmentItem instanceof EventTemplate || BrowseMode.EVENTS.equals(browseMode)) {
                mainApp.show(ScreenCode.CALENDAR);
            } else {
                Optional<? extends SegmentItem> optionalResult = Optional.empty();
                if (segmentItem instanceof TitleView || BrowseMode.TITLES.equals(browseMode)) {
                    optionalResult = ViewUtils.createTitleViewDialog(gameController).showAndWait();
                } else if (segmentItem instanceof TagTeamView || BrowseMode.TAG_TEAMS.equals(browseMode)) {
                    CreateTagTeamDialog createTagTeamDialog = new CreateTagTeamDialog();
                    optionalResult = createTagTeamDialog.getDialog(gameController).showAndWait();
                }

                optionalResult.ifPresent((SegmentItem newSegmentItem) -> {
                    mainApp.show(ScreenCode.BROWSER, newSegmentItem);
                });
            }
        });

    }

}
