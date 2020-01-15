package openwrestling.model.gameObjects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SegmentTemplate extends GameObject {
    private long segmentTemplateID;
    private EventTemplate eventTemplate;
    private Title title;
    private List<SegmentTeam> segmentTeams = new ArrayList<>();
    private LocalDate sourceEventDate;
    private String sourceEventName;
}
