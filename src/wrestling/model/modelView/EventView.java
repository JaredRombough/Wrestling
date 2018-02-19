/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wrestling.model.modelView;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import wrestling.model.Promotion;
import wrestling.model.interfaces.Segment;
import wrestling.model.interfaces.iEvent;

public class EventView implements iEvent {

    private final List<Segment> segments;
    private final LocalDate date;
    private final Promotion promotion;

    public EventView(List<Segment> segments, LocalDate date, Promotion promotion) {
        this.segments = new ArrayList<>(segments);
        this.date = date;
        this.promotion = promotion;
    }

    /**
     * @return the segments
     */
    public List<Segment> getSegments() {
        return segments;
    }

    /**
     * @return the date
     */
    @Override
    public LocalDate getDate() {
        return date;
    }

    /**
     * @return the promotion
     */
    @Override
    public Promotion getPromotion() {
        return promotion;
    }
}
