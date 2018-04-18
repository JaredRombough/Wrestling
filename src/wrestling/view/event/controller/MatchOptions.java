package wrestling.view.event.controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import wrestling.model.segmentEnum.MatchFinish;
import wrestling.model.segmentEnum.MatchRule;
import wrestling.view.utility.interfaces.ControllerBase;

public class MatchOptions extends ControllerBase implements Initializable {

    

    @FXML
    private ComboBox matchRules;

    @FXML
    private ComboBox matchFinishes;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    @Override
    public void initializeMore() {
        intitializeMatchFinshesCombobox();

        initializeMatchRulesCombobox();

        updateMatchRulesCombobox();
    }

    private void intitializeMatchFinshesCombobox() {
        matchFinishes.setItems(FXCollections.observableArrayList(MatchFinish.values()));
    }

    @Override
    public void updateLabels() {
        updateMatchRulesCombobox();
    }

    private void initializeMatchRulesCombobox() {
        matchRules.setItems(FXCollections.observableArrayList(MatchRule.values()));
        matchRules.getSelectionModel().selectFirst();
    }

    private void updateMatchRulesCombobox() {
        MatchRule current = (MatchRule) getMatchRules().getSelectionModel().getSelectedItem();
        MatchFinish lastFinish = (MatchFinish) getMatchFinishes().getSelectionModel().getSelectedItem();
        List<MatchFinish> finishes = new ArrayList<>();
        for (MatchFinish f : MatchFinish.values()) {

            if (current.nodq() && f.nodq()) {
                finishes.add(f);
            } else if (!current.nodq()) {
                finishes.add(f);
            }
        }

        getMatchFinishes().setItems(FXCollections.observableArrayList(finishes));
        if (getMatchFinishes().getItems().contains(lastFinish)) {
            getMatchFinishes().getSelectionModel().select(lastFinish);
        } else {
            getMatchFinishes().getSelectionModel().selectFirst();
        }
    }

    public MatchRule getMatchRule() {
        return (MatchRule) getMatchRules().getSelectionModel().getSelectedItem();
    }

    public MatchFinish getMatchFinish() {
        return (MatchFinish) getMatchFinishes().getSelectionModel().getSelectedItem();
    }

    /**
     * @return the matchRules
     */
    public ComboBox getMatchRules() {
        return matchRules;
    }

    /**
     * @return the matchFinishes
     */
    public ComboBox getMatchFinishes() {
        return matchFinishes;
    }

}
