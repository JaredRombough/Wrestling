package wrestling.model;

import wrestling.model.factory.EventFactory;
import wrestling.model.utility.UtilityFunctions;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Worker implements Serializable {

    /**
     * @return the serialNumber
     */
    public static int getSerialNumber() {
        return serialNumber;
    }

    /**
     * @param aSerialNumber the serialNumber to set
     */
    public static void setSerialNumber(int aSerialNumber) {
        serialNumber = aSerialNumber;
    }

    private String name;
    private String shortName;

    private static int serialNumber = 0;

    private int striking;
    private int flying;
    private int wrestling;
    private int charisma;
    private int endurance;
    private int proficiency;
    private int behaviour;
    private int popularity;
    private int fatigue;

    //should these be specified in the contract, instead of in the worker object?
    private boolean manager;
    private boolean fullTime;
    private boolean mainRoster;

    private final List<Contract> contracts = new ArrayList<>();
    private final List<EventFactory> bookings = new ArrayList<>();
    private final List<Title> titles = new ArrayList<>();

    public Worker() {

        matchRecords = new ArrayList<>();

        minimumPopularity = 0;

        name = "Worker #" + serialNumber;
        serialNumber++;

    }

    public void addBooking(EventFactory event) {
        bookings.add(event);
    }

    public List<EventFactory> getBookings() {
        return bookings;
    }

    public void addContract(Contract contract) {
        this.contracts.add(contract);
    }

    public void removeContract(Contract contract) {
        this.contracts.remove(contract);
    }

    public void addTitle(Title title) {
        this.getTitles().add(title);
    }

    public void removeTitle(Title title) {
        this.getTitles().remove(title);
    }

    public List<Contract> getContracts() {
        return contracts;
    }

    public boolean canNegotiate(Promotion promotion) {
        //this would have to be more robust
        //such as checking how much time is left on our contract
        boolean canNegotiate = true;

        if (popularity > promotion.maxPopularity()) {
            canNegotiate = false;
        }

        if (this.hasContract()) {
            for (Contract contract : contracts) {
                if (contract.isExclusive() || contract.getPromotion().equals(promotion)) {
                    canNegotiate = false;
                }

            }
        }

        return canNegotiate;
    }

    private boolean hasContract() {
        return (this.contracts.size() > 0);
    }

    public Contract getContract(Promotion promotion) {

        Contract thisContract = null;
        for (Contract current : contracts) {
            if (current.getPromotion().equals(promotion)) {
                thisContract = current;
            }
        }

        if (thisContract == null) {
            System.out.println("NULL CONTRACT\n" + name + "\n" + promotion.getName());
        }

        return thisContract;
    }

    public String contractString() {
        String string = new String();
        for (Contract current : contracts) {
            string += current.getTerms();
            string += "\n";
        }
        return string;
    }

    @Override
    public String toString() {
        return this.getName();
    }

    //checks if a worker is booked at all on a given date
    public boolean isBooked(LocalDate date) {
        boolean isBooked = false;

        for (Contract contract : contracts) {
            if (contract.getBookedDates().contains(date)) {
                isBooked = true;
            }
        }

        return isBooked;

    }

    //checks if a worker is booked on a certain date
    //returns false if the booking is with the given promotion
    public boolean isBooked(LocalDate date, Promotion p) {
        boolean isBooked = isBooked(date);

        if (getContract(p).getBookedDates().contains(date)) {
            isBooked = false;
        }

        return isBooked;

    }

    private int minimumPopularity;

    //once workers reach a level of popularity, they can never  drop below 50% of that
    private void updateMinimumPopularity() {

        if ((popularity / 2) > minimumPopularity) {
            minimumPopularity = popularity / 2;
        }
    }

    public void gainPopularity() {

        int maxPopularity = 0;

        for (Contract contract : contracts) {
            if (contract.getPromotion().maxPopularity() > maxPopularity) {
                maxPopularity = contract.getPromotion().maxPopularity();
            }
        }

        if (popularity < maxPopularity
                || UtilityFunctions.randRange(1, 10) == 1) {

            int range = 0;

            if (popularity >= 90) {
                range = 20;
            } else if (popularity < 90 && popularity >= 80) {
                range = 10;
            } else if (popularity < 80 && popularity >= 70) {
                range = 7;
            } else if (popularity < 70) {
                range = 5;
            }

            if (UtilityFunctions.randRange(1, range) == 1) {

                this.popularity += 1;
            }
        }

        if (popularity > 100) {
            popularity = 100;
        }

        updateMinimumPopularity();

    }

    public void losePopularity() {

        if (UtilityFunctions.randRange(1, 10) == 10) {
            popularity -= 1;
        }

        if (popularity < 0) {
            popularity = 0;
        }

        if (popularity < minimumPopularity) {
            popularity = minimumPopularity;
        }
    }

    private final List<MatchRecord> matchRecords;

    public void addMatchRecord(MatchRecord record) {
        matchRecords.add(record);
    }

    public List<MatchRecord> getMatchRecods() {
        return matchRecords;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the striking
     */
    public int getStriking() {
        return striking;
    }

    /**
     * @param striking the striking to set
     */
    public void setStriking(int striking) {
        this.striking = striking;
    }

    /**
     * @return the flying
     */
    public int getFlying() {
        return flying;
    }

    /**
     * @param flying the flying to set
     */
    public void setFlying(int flying) {
        this.flying = flying;
    }

    /**
     * @return the wrestling
     */
    public int getWrestling() {
        return wrestling;
    }

    /**
     * @param wrestling the wrestling to set
     */
    public void setWrestling(int wrestling) {
        this.wrestling = wrestling;
    }

    /**
     * @return the look
     */
    public int getCharisma() {
        return charisma;
    }

    /**
     * @param charisma the look to set
     */
    public void setCharisma(int charisma) {
        this.charisma = charisma;
    }

    /**
     * @return the endurance
     */
    public int getEndurance() {
        return endurance;
    }

    /**
     * @param endurance the endurance to set
     */
    public void setEndurance(int endurance) {
        this.endurance = endurance;
    }

    /**
     * @return the proficiency
     */
    public int getProficiency() {
        return proficiency;
    }

    /**
     * @param proficiency the proficiency to set
     */
    public void setProficiency(int proficiency) {
        this.proficiency = proficiency;
    }

    /**
     * @return the reputation
     */
    public int getBehaviour() {
        return behaviour;
    }

    /**
     * @param behaviour the reputation to set
     */
    public void setBehaviour(int behaviour) {
        this.behaviour = behaviour;
    }

    /**
     * @return the popularity
     */
    public int getPopularity() {
        return popularity;
    }

    /**
     * @param popularity the popularity to set
     */
    public void setPopularity(int popularity) {
        this.popularity = popularity;

        updateMinimumPopularity();

    }

    /**
     * @return the shortName
     */
    public String getShortName() {
        return shortName;
    }

    /**
     * @param shortName the shortName to set
     */
    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    /**
     * @return the manager
     */
    public boolean isManager() {
        return manager;
    }

    /**
     * @param manager the manager to set
     */
    public void setManager(boolean manager) {
        this.manager = manager;
    }

    /**
     * @return the fullTime
     */
    public boolean isFullTime() {
        return fullTime;
    }

    /**
     * @param fullTime the fullTime to set
     */
    public void setFullTime(boolean fullTime) {
        this.fullTime = fullTime;
    }

    /**
     * @return the mainRoster
     */
    public boolean isMainRoster() {
        return mainRoster;
    }

    /**
     * @param mainRoster the mainRoster to set
     */
    public void setMainRoster(boolean mainRoster) {
        this.mainRoster = mainRoster;
    }

    /**
     * @return the titles
     */
    public List<Title> getTitles() {
        return titles;
    }

    /**
     * @return the fatigue
     */
    public int getFatigue() {
        return fatigue;
    }

    /**
     * @param fatigue the fatigue to set
     */
    public void setFatigue(int fatigue) {
        this.fatigue = fatigue;
    }

}
