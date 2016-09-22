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

        //default popularity of 50 for now
        popularity = 50;

        serialNumber++;
    }

    private PromotionAi ai;

    public void setAi(PromotionAi ai) {
        this.ai = ai;
    }

    public PromotionAi getAi() {
        return ai;
    }

    private List<Worker> roster;

    public List<Worker> getRoster() {
        return this.roster;
    }

    private String name;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
    private static int serialNumber = 0;

    private int popularity;

    public int getPopulatirty() {
        return popularity;
    }

    public void setPopularity(int popularity) {
        if (popularity > 100) {
            popularity = 100;
        } else if (popularity < 1) {
            popularity = 1;
        }
        this.popularity = popularity;

    }

    public void gainPopularity(int score) {
        if (score > (level * 20)) {
            gainPopularity();
        }
    }

    public void gainPopularity() {
        popularity += 1;
        if (popularity >= 100) {
            if (level != 4) {
                level += 1;
                popularity = 10;
            } else {
                popularity = 100;
            }
        }
    }

    private int level;

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        if (level > 5) {
            level = 5;
        }
        if (level < 0) {
            level = 0;
        }
        this.level = level;
    }

    private List<Event> events;

    public List<Event> getEvents() {
        return Collections.unmodifiableList(events);
    }

    public List<Event> getPastEvents() {
        List<Event> pastEvents = new ArrayList<>();
        for (Event event : events) {
            if (event.isComplete()) {
                pastEvents.add(event);
            }
        }
        return pastEvents;
    }

    public List<Event> getFutureEvents() {
        List<Event> futureEvents = new ArrayList<>();
        for (Event event : events) {
            if (!event.isComplete()) {
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
        this.roster.add(contract.getWorker());

    }

    public void removeContract(Contract contract) {
        this.contracts.remove(contract);
        this.roster.remove(contract.getWorker());

    }

    public List<Contract> getContracts() {
        return contracts;
    }
}
