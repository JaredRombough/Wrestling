package openwrestling.model.gameObjects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import openwrestling.model.interfaces.iDate;
import openwrestling.model.interfaces.iEvent;
import openwrestling.model.segment.constants.EventType;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Event extends GameObject implements Serializable, iEvent, iDate {

    private Promotion promotion;
    private long eventID;
    private LocalDate date;
    private EventType eventType;
    private int cost;
    private int gate;
    private int attendance;
    private int rating;
    private int defaultDuration;
    private String name;
    private EventTemplate eventTemplate;
    private List<Segment> segments;

    @Override
    public String toString() {
        if (promotion != null) {
            return StringUtils.containsIgnoreCase(name, promotion.getShortName())
                    ? name : promotion.getShortName() + " " + name;
        }
        return name;
    }


    public String getVerboseEventTitle() {
        if (toString().contains(promotion.getShortName())) {
            return String.format("%s (%s)",
                    toString(),
                    date);
        } else {
            return String.format("%s %s (%s)",
                    promotion.getShortName(),
                    toString(),
                    date);
        }
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof Event &&
                Objects.equals(((Event) object).getEventID(), eventID);
    }

}
