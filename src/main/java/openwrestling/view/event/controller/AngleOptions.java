package openwrestling.view.event.controller;

import lombok.Getter;
import lombok.Setter;
import openwrestling.model.SegmentTemplate;
import openwrestling.model.gameObjects.Stable;
import openwrestling.model.segmentEnum.AngleType;
import openwrestling.model.segmentEnum.JoinTeamType;
import openwrestling.model.segmentEnum.PresenceType;
import openwrestling.model.segmentEnum.PromoType;
import openwrestling.model.segmentEnum.ShowType;

@Getter
@Setter
public class AngleOptions {
    private AngleType angleType;
    private JoinTeamType joinTeamType;
    private PresenceType presenceType;
    private PromoType promoType;
    private ShowType showType;
    private Stable joinStable;
    private SegmentTemplate challengeSegment;
}
