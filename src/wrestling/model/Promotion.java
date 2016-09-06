package wrestling.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class Promotion implements Serializable {


    public Promotion() {
        this.contracts = new ArrayList<>();

        roster = new ArrayList<Worker>();
        events = new ArrayList<Event>();
        funds = 0;
        name = "Promotion #" + serialNumber;
        serialNumber++;
    }
    
    private PromotionAi ai;
    public void setAi(PromotionAi ai) { this.ai = ai; }
    public PromotionAi getAi() { return ai; }

    public List<Worker> roster;

    private String name;
    public void setName(String name) { this.name = name; }
    public String getName() { return name; }
    private static int serialNumber = 0;

    private List<Event> events;
    public List<Event> getEvents() { return Collections.unmodifiableList(events); }
    public List<Event> getPastEvents() {
        List<Event> pastEvents = new ArrayList<>();
        for (Event event : events) {
            if(event.isComplete()) {
                pastEvents.add(event);
            }
        }
        return pastEvents;
    }
    public List<Event> getFutureEvents() {
        List<Event> futureEvents = new ArrayList<>();
        for (Event event : events) {
            if(!event.isComplete()) {
                futureEvents.add(event);
            }
        }
        return futureEvents;
        
    }
    public Event getEventByDate(int date) {
        Event event = null;
        for (Event e : events) {
            if (e.getDate() == date) {
                event = e;
                break;
            }
        }
        
        return event;
    }
    public void addEvent(Event event) {
        events.add(event);
    }
    
    private List<Contract> contracts;

    private Integer funds;

    public void addFunds(Integer income) {
        funds += income;
    }

    public void removeFunds(Integer expense) {
        funds -= expense;
    }

    public Integer getFunds() {
        return funds;
    }

    @Override
    public String toString() {
        return name;
    }

    public void addContract(Contract contract) {
        this.contracts.add(contract);
    }

    public void removeContract(Contract contract) {
        this.contracts.remove(contract);
    }

    public List getContracts() {
        return contracts;
    }
}
