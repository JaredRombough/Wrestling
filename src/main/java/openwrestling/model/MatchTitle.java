package openwrestling.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import openwrestling.model.gameObjects.Title;

@Getter
@AllArgsConstructor
public class MatchTitle {
    private final Title title;
    private final Match match;
}
