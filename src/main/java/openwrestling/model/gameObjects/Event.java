package openwrestling.model.gameObjects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import openwrestling.model.interfaces.iDate;
import openwrestling.model.interfaces.iEvent;
import openwrestling.model.modelView.Segment;
import openwrestling.model.segmentEnum.EventType;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

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
    private int defaultDuration;
    private String name;
    private EventTemplate eventTemplate;
    private List<Segment> segments;

    public Event(EventTemplate eventTemplate, LocalDate date) {
        this.eventTemplate = eventTemplate;
        this.promotion = eventTemplate.getPromotion();
        this.date = date;
        this.name = eventTemplate.getName();
        this.defaultDuration = eventTemplate.getDefaultDuration();
    }

    public Event(Promotion promotion, LocalDate date) {
        this.promotion = promotion;
        this.date = date;
    }

    @Override
    public String toString() {
        return StringUtils.containsIgnoreCase(name, promotion.getShortName())
                ? name : promotion.getShortName() + " " + name;
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

}
