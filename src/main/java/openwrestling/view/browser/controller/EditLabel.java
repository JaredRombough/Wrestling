package openwrestling.view.browser.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import openwrestling.model.SegmentItem;
import openwrestling.model.gameObjects.EventTemplate;
import openwrestling.model.gameObjects.RosterSplit;
import openwrestling.model.gameObjects.Stable;
import openwrestling.model.gameObjects.TagTeam;
import openwrestling.model.gameObjects.Title;
import openwrestling.model.segmentEnum.BrowseMode;
import openwrestling.view.utility.ScreenCode;
import openwrestling.view.utility.ViewUtils;
import openwrestling.view.utility.interfaces.ControllerBase;
import org.apache.commons.lang3.StringUtils;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import static openwrestling.model.constants.UIConstants.EDIT_ICON;

public class EditLabel extends ControllerBase implements Initializable {

    @FXML
    Label label;

    @FXML
    private Button editButton;

    @FXML
    private Button createButton;

    private BrowseMode browseMode;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        label.setText("");
        editButton.setText(EDIT_ICON);
        createButton.setText("+");
    }

    @Override
    public void setCurrent(Object object) {
        SegmentItem segmentItem = object instanceof SegmentItem ? (SegmentItem) object : null;
        if (object instanceof BrowseMode) {
            browseMode = (BrowseMode) object;
        }

        if (segmentItem != null) {
            label.setText(segmentItem.getLongName());
            editButton.setOnAction(e -> {
                if (segmentItem instanceof Title) {
                    Title title = (Title) object;
                    title.setName(ViewUtils.editTextDialog(title.getName()));
                    gameController.getTitleManager().updateTitle(title);
                } else if (segmentItem instanceof EventTemplate) {
                    EventTemplate eventTemplate = (EventTemplate) object;
                    eventTemplate.setName(ViewUtils.editTextDialog(eventTemplate.getName()));
                    gameController.getEventManager().updateEventName(eventTemplate);
                } else if (segmentItem instanceof TagTeam) {
                    TagTeam tagTeam = (TagTeam) object;
                    tagTeam.setName(ViewUtils.editTextDialog(tagTeam.getName()));
                } else if (segmentItem instanceof Stable) {
                    Stable stable = (Stable) object;
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
                if (segmentItem instanceof Title || BrowseMode.TITLES.equals(browseMode)) {
                    optionalResult = ViewUtils.createTitleViewDialog(gameController).showAndWait();
                } else if (segmentItem instanceof TagTeam || BrowseMode.TAG_TEAMS.equals(browseMode)) {
                    CreateTagTeamDialog createTagTeamDialog = new CreateTagTeamDialog();
                    optionalResult = createTagTeamDialog.getDialog(gameController).showAndWait();
                } else if (BrowseMode.STABLES.equals(browseMode) || BrowseMode.ROSTER_SPLIT.equals(browseMode)) {
                    boolean stableMode = BrowseMode.STABLES.equals(browseMode);
                    String groupName = ViewUtils.editTextDialog("", String.format("%s name:", stableMode ? "Stable" : "Roster split"));
                    if (StringUtils.isNotBlank(groupName)) {
                        if (stableMode) {
                            Stable stable = new Stable(groupName, playerPromotion());
                            gameController.getStableManager().addStable(stable);
                            mainApp.show(ScreenCode.BROWSER, stable);
                        } else {
                            RosterSplit rosterSplit = new RosterSplit(groupName, playerPromotion());
                            gameController.getRosterSplitManager().createRosterSplits(List.of(rosterSplit));
                            mainApp.show(ScreenCode.BROWSER, rosterSplit);
                        }
                    }
                }

                optionalResult.ifPresent((SegmentItem newSegmentItem) -> {
                    mainApp.show(ScreenCode.BROWSER, newSegmentItem);
                });
            }
        });

    }

}
