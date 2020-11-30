package openwrestling.view.event.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import openwrestling.model.segment.constants.MatchFinish;
import openwrestling.model.segment.opitons.MatchRules;
import openwrestling.view.utility.interfaces.ControllerBase;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class MatchOptions extends ControllerBase implements Initializable {

    @FXML
    private ComboBox<MatchRules> matchRules;

    @FXML
    private ComboBox<MatchFinish> matchFinishes;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    @Override
    public void initializeMore() {
        initializeMatchFinshesCombobox();

        initializeMatchRulesCombobox();

        updateMatchRulesCombobox();
    }

    private void initializeMatchFinshesCombobox() {
        matchFinishes.setItems(FXCollections.observableArrayList(MatchFinish.values()));
    }

    @Override
    public void updateLabels() {
        updateMatchRulesCombobox();
    }

    private void initializeMatchRulesCombobox() {
        matchRules.setItems(FXCollections.observableArrayList(gameController.getMatchRulesManager().getMatchRules()));
        matchRules.getSelectionModel().selectFirst();
        StringBuilder sb = new StringBuilder();
        matchRules.getItems().forEach(rule -> {
            sb.append(rule);
            if (rule.getStrikingModifier() != 0) {
                sb.append(String.format(" %d%% STR", rule.getStrikingModifier()));
            }
            if (rule.getWrestingModifier() != 0) {
                sb.append(String.format(" %d%% WRE", rule.getWrestingModifier()));
            }
            if (rule.getFlyingModifier() != 0) {
                sb.append(String.format(" %d%% FLY", rule.getFlyingModifier()));
            }
            if (rule.getInjuryModifier() != 0) {
                sb.append(String.format(" %d%% INJ", rule.getInjuryModifier()));
            }
            sb.append("\n");
        });
    }

    private void updateMatchRulesCombobox() {
        MatchRules current = (MatchRules) getMatchRules().getSelectionModel().getSelectedItem();
        MatchFinish lastFinish = (MatchFinish) getMatchFinishes().getSelectionModel().getSelectedItem();
        List<MatchFinish> finishes = new ArrayList<>();
        for (MatchFinish f : MatchFinish.values()) {

            if (current.isNoDQ() && f.nodq()) {
                finishes.add(f);
            } else if (!current.isNoDQ()) {
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

    public MatchRules getMatchRule() {
        return (MatchRules) getMatchRules().getSelectionModel().getSelectedItem();
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
