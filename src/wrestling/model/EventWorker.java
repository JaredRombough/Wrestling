package wrestling.model;

import java.io.Serializable;
import wrestling.model.modelView.WorkerView;

/*
basically a booking. the worker was physically present at the event, but
not necessarily involved in a segment. potentially this could be cancelled?
*/
public class EventWorker implements Serializable{
    
    private final Event event;
    private final WorkerView worker;
    
    public EventWorker(Event event, WorkerView worker) {
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
    public WorkerView getWorker() {
        return worker;
    }

}
