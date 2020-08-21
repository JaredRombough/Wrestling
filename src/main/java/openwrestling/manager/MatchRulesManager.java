package openwrestling.manager;

import lombok.Getter;
import openwrestling.database.Database;
import openwrestling.model.segment.opitons.MatchRules;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static openwrestling.model.constants.GameConstants.DEFAULT_MATCH_RULE;

public class MatchRulesManager extends GameObjectManager implements Serializable {

    @Getter
    private List<MatchRules> matchRules = new ArrayList<>();

    public MatchRulesManager(Database database) {
        super(database);
    }

    @Override
    public void selectData() {
        matchRules = getDatabase().selectAll(MatchRules.class);
    }

    public MatchRules getDefaultRules() {
        MatchRules defaultRule = matchRules.stream()
                .filter(matchRule -> matchRule.getMatchRulesID() == DEFAULT_MATCH_RULE)
                .findFirst()
                .orElse(null);

        if (defaultRule != null) {
            return defaultRule;
        }

        return matchRules.stream()
                .findAny()
                .orElseThrow(() -> new RuntimeException("No match rules"));
    }

}
