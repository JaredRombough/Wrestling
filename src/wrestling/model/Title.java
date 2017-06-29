package wrestling.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/*
A title links a worker or workers with a promotion
 */
public class Title implements Serializable {

    private List<Worker> workers;

    private int teamSize;
    private Promotion promotion;
    private String name;
    private LocalDate dayWon;

    //vacant title
    public Title(Promotion promotion, int teamSize, String name) {
        this.promotion = promotion;
        this.teamSize = teamSize;
        this.name = name;
    }

    public Title(Promotion promotion, List<Worker> workers, String name) {
        this.promotion = promotion;
        this.workers = workers;
        this.name = name;
        this.teamSize = workers.size();

    }

    @Override
    public String toString() {
        return this.getName();
    }

    /**
     * @return the workers
     */
    public List<Worker> getWorkers() {

        List<Worker> w = workers;

        if (w == null) {
            w = new ArrayList<>();
        }

        return w;
    }

    public boolean isVacant() {
        return getWorkers().isEmpty();
    }

    /**
     * @param workers the workers to set
     */
    public void setWorkers(List<Worker> workers) {
        this.workers = workers;
    }

    public void vacateTitle() {
        this.workers = null;
    }

    /**
     * @return the promotion
     */
    public Promotion getPromotion() {
        return promotion;
    }

    /**
     * @param promotion the promotion to set
     */
    public void setPromotion(Promotion promotion) {
        this.promotion = promotion;
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
     * @return the dayWon
     */
    public LocalDate getDayWon() {
        return dayWon;
    }

    /**
     * @param dayWon the dayWon to set
     */
    public void setDayWon(LocalDate dayWon) {
        this.dayWon = dayWon;
    }

    /**
     * @return the teamSize
     */
    public int getTeamSize() {
        return teamSize;
    }

}
