package openwrestling.view.event.controller;

import lombok.Getter;
import lombok.Setter;
import openwrestling.model.gameObjects.SegmentTemplate;
import openwrestling.model.gameObjects.Stable;
import openwrestling.model.segment.constants.AngleType;
import openwrestling.model.segment.constants.JoinTeamType;
import openwrestling.model.segment.constants.PresenceType;
import openwrestling.model.segment.constants.PromoType;
import openwrestling.model.segment.constants.ShowType;

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
