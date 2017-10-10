package wrestling.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import wrestling.model.factory.EventFactory;
import wrestling.model.utility.ModelUtilityFunctions;

public class Worker implements Serializable {

    private static int serialNumber = 0;

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
    private String imageString;

    private int striking;
    private int flying;
    private int wrestling;
    private int charisma;
    private int behaviour;
    private int popularity;

    //should these be specified in the contract, instead of in the worker object?
    private boolean manager;
    private boolean fullTime;
    private boolean mainRoster;

    private final List<Contract> contracts = new ArrayList<>();
    private final List<EventFactory> bookings = new ArrayList<>();
    private final List<Title> titles = new ArrayList<>();
    private int minimumPopularity;

    public Worker() {

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

    public boolean hasContract() {
        return !this.contracts.isEmpty();
    }

    private transient Logger log = LogManager.getLogger(this.getClass());

    public Contract getContract(Promotion promotion) {

        Contract thisContract = null;
        for (Contract current : contracts) {
            if (current.getPromotion().equals(promotion)) {
                thisContract = current;
            }
        }

        if (thisContract == null) {
            log.log(Level.ERROR, "NULL CONTRACT\n" + name + "\n" + promotion.getName());
        }

        return thisContract;
    }

    public String contractString() {

        StringBuilder bld = new StringBuilder();
        for (Contract current : contracts) {

            bld.append(current.getTerms());
            bld.append("\n");
        }
        return bld.toString();
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
                || ModelUtilityFunctions.randRange(1, 10) == 1) {

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

            if (ModelUtilityFunctions.randRange(1, range) == 1) {

                this.popularity += 1;
            }
        }

        if (popularity > 100) {
            popularity = 100;
        }

        updateMinimumPopularity();

    }

    public void losePopularity() {

        if (ModelUtilityFunctions.randRange(1, 10) == 10) {
            popularity -= 1;
        }

        if (popularity < 0) {
            popularity = 0;
        }

        if (popularity < minimumPopularity) {
            popularity = minimumPopularity;
        }
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
     * @return the imageString
     */
    public String getImageString() {
        return imageString;
    }

    /**
     * @param imageString the imageString to set
     */
    public void setImageString(String imageString) {
        this.imageString = imageString;
    }

}
