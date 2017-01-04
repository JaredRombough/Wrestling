package wrestling.model;

import wrestling.model.utility.UtilityFunctions;
import java.io.Serializable;
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
    private int look;
    private int talk;
    private int strength;
    private int endurance;
    private int proficiency;
    private int reputation;
    private int popularity;

    //should these be specified in the contract, instead of in the worker object?
    private boolean manager;
    private boolean fullTime;
    private boolean mainRoster;

    private final List<Contract> contracts;
    private final List<EventFactory> bookings;

    public Worker() {
        contracts = new ArrayList<>();
        bookings = new ArrayList<>();
        matchRecords = new ArrayList<>();

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

    public List getContracts() {
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

    public boolean isBooked(int date) {
        boolean isBooked = false;

        for (MatchRecord record : matchRecords) {
            if (record.getMatchDate() == date) {
                isBooked = true;
            }
        }

        return isBooked;

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

            if (UtilityFunctions.randRange(1, 3) == 1) {

                this.popularity += 1;
            }
        }

        if (popularity > 100) {
            popularity = 100;
        }

    }

    private List<MatchRecord> matchRecords;

    public void addMatchRecord(MatchRecord record) {
        matchRecords.add(record);
    }

    public List<MatchRecord> getMatchRecods() {
        return matchRecords;
    }

    public void losePopularity() {
        int rand = UtilityFunctions.randRange(1, 3);

        if (rand == 1) {

        } else if (rand == 2) {
            this.popularity -= 2;
        } else {
            this.popularity -= 1;
        }

        if (popularity < 0) {
            popularity = 0;
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
    public int getLook() {
        return look;
    }

    /**
     * @param look the look to set
     */
    public void setLook(int look) {
        this.look = look;
    }

    /**
     * @return the talk
     */
    public int getTalk() {
        return talk;
    }

    /**
     * @param talk the talk to set
     */
    public void setTalk(int talk) {
        this.talk = talk;
    }

    /**
     * @return the strength
     */
    public int getStrength() {
        return strength;
    }

    /**
     * @param strength the strength to set
     */
    public void setStrength(int strength) {
        this.strength = strength;
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
    public int getReputation() {
        return reputation;
    }

    /**
     * @param reputation the reputation to set
     */
    public void setReputation(int reputation) {
        this.reputation = reputation;
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

}
