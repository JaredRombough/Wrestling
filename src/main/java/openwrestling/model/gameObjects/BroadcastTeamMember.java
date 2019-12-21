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
public class BroadcastTeamMember extends GameObject {
    private long broadcastTeamID;
    private Worker worker;
    private StaffMember staffMember;
    private EventTemplate eventTemplate;
    private Event event;
    private Promotion promotion;
}
