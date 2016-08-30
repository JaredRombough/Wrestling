package wrestling.model;

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
    
    private List<Contract> contracts;
    
   
    
    
    public Worker() {
        this.contracts = new ArrayList<>();
        
        name = "Worker #" + serialNumber;
        serialNumber++;
        
        
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
    
    public Contract getContract(Promotion promotion) {
        Contract thisContract = null;
        for (Contract current : contracts) {
            if (current.getPromotion().equals(promotion)) {
                thisContract = current;
            }
        }
        
        
        return thisContract;
    }
    
    public String contractString() {
        String string = new String();
        for (Contract current : contracts) {
            string += current;
            string += "\n";
        }
        return string;
    }
    
    @Override
    public String toString() {
        return this.getName();
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
    
}
