package wrestling.model;

import java.io.Serializable;
import wrestling.model.segmentEnum.Gender;

public class Worker implements Serializable, SegmentItem {

    private static int workerID = 0;

    /**
     * @return the workerID
     */
    public static int getWorkerID() {
        return workerID;
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
    private int age;

    private Gender gender;

    private boolean manager;
    private boolean fullTime;
    private boolean mainRoster;

    private int minimumPopularity;

    public Worker() {
        minimumPopularity = 0;
        name = "Worker #" + workerID;
        workerID++;
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
        //once workers reach a level of popularity, they can never  drop below 50% of that
        if ((popularity / 2) > getMinimumPopularity()) {
            setMinimumPopularity(popularity / 2);
        }
    }

    /**
     * @return the shortName
     */
    @Override
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

    /**
     * @return the minimumPopularity
     */
    public int getMinimumPopularity() {
        return minimumPopularity;
    }

    /**
     * @param minimumPopularity the minimumPopularity to set
     */
    public void setMinimumPopularity(int minimumPopularity) {
        this.minimumPopularity = minimumPopularity;
    }

    /**
     * @return the age
     */
    public int getAge() {
        return age;
    }

    /**
     * @param age the age to set
     */
    public void setAge(int age) {
        this.age = age;
    }

    /**
     * @return the gender
     */
    @Override
    public Gender getGender() {
        return gender;
    }

    /**
     * @param gender the gender to set
     */
    public void setGender(Gender gender) {
        this.gender = gender;
    }
}
