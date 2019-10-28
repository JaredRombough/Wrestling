package openwrestling.model;

import java.io.Serializable;

import openwrestling.model.gameObjects.Event;
import openwrestling.model.gameObjects.Worker;

/*
basically a booking. the worker was physically present at the event, but
not necessarily involved in a segment. potentially this could be cancelled?
*/
public class EventWorker implements Serializable{
    
    private final Event event;
    private final Worker worker;
    
    public EventWorker(Event event, Worker worker) {
        this.event = event;
        this.worker = worker;
    }

    /**
     * @return the event
     */
    public Event getEvent() {
        return event;
    }

    /**
     * @return the worker
     */
    public Worker getWorker() {
        return worker;
    }

}
