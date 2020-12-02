package openwrestling.model.gameObjects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RosterSplitWorker extends GameObject {
    private long rosterSplitWorkerID;
    private Worker worker;
    private RosterSplit rosterSplit;
}
