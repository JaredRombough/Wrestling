package wrestling.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 *
 */
public class Promotion implements Serializable {

    public Promotion() {
        this.contracts = new ArrayList<>();

        roster = new ArrayList<Worker>();
        events = new ArrayList<Event>();
        funds = 0;
        name = "Promotion #" + serialNumber;
        serialNumber++;
    }

    public List<Worker> roster;

    private String name;
    private static int serialNumber = 0;

    public List<Event> events;
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
