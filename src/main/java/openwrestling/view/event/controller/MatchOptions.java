package openwrestling.view.event.controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import openwrestling.model.segmentEnum.MatchFinish;
import openwrestling.model.segmentEnum.MatchRule;
import openwrestling.view.utility.ViewUtils;
import openwrestling.view.utility.interfaces.ControllerBase;

public class MatchOptions extends ControllerBase implements Initializable {

    @FXML
    private ComboBox<MatchRule> matchRules;

    @FXML
    private Button rulesHelp;

    @FXML
    private Label rulesLabel;

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
        ViewUtils.initializeButtonHover(rulesLabel, rulesHelp);
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
        rulesHelp.setOnAction(a -> {
            Alert alert = ViewUtils.generateAlert("Match Rules help",
                    "Bonuses/penalties for matches",
                    sb.toString(),
                    Alert.AlertType.INFORMATION
            );
            alert.getDialogPane().setMinWidth(500);
            alert.showAndWait();
        });
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
