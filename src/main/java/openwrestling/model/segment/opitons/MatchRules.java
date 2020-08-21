package openwrestling.model.segment.opitons;

import lombok.Getter;
import lombok.Setter;
import openwrestling.model.gameObjects.GameObject;

import java.util.Objects;

@Getter
@Setter
public class MatchRules extends GameObject {

    private long matchRulesID;
    private boolean noDQ;
    private String description;
    private int strikingModifier;
    private int flyingModifier;
    private int wrestingModifier;
    private int injuryModifier;

    @Override
    public String toString() {
        return description;
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof MatchRules &&
                Objects.equals(((MatchRules) object).getMatchRulesID(), matchRulesID);
    }

}
