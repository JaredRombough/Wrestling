package openwrestling.model.gameObjects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class EntourageMember extends GameObject {

    private Worker leader;
    private Worker follower;
    private long entourageMemberID;
    private boolean active;

}
