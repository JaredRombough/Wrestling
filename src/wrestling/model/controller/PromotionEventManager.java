package wrestling.model.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import wrestling.model.Promotion;
import wrestling.model.PromotionEvent;

public class PromotionEventManager {

    private final List<PromotionEvent> promotionEvents;

    public PromotionEventManager() {
        promotionEvents = new ArrayList();
    }

    public void addEventDate(LocalDate date, Promotion promotion) {

        PromotionEvent promotionEvent = new PromotionEvent(date, promotion);
        promotionEvents.add(promotionEvent);
    }

    public List<PromotionEvent> getEvents(Promotion promotion) {
        List<PromotionEvent> events = new ArrayList();
        for (PromotionEvent event : promotionEvents) {
            if (event.getPromotion().equals(promotion)) {
                events.add(event);
            }
        }

        return events;

    }

    public boolean hasEventOnDate(Promotion promotion, LocalDate date) {
        boolean hasEvent = false;
        for (PromotionEvent event : promotionEvents) {
            if (event.getLocalDate().equals(date)
                    && event.getPromotion().equals(promotion)) {
                hasEvent = true;
                break;
            }
        }
        return hasEvent;
    }

    public int eventsAfterDate(Promotion promotion, LocalDate date) {

        int futureEvents = 0;

        for (PromotionEvent event : promotionEvents) {
            if (event.getPromotion().equals(promotion)
                    && event.getLocalDate().isAfter(date)) {
                futureEvents++;
            }
        }

        return futureEvents;
    }
}
