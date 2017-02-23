package wrestling.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/*
A title links a worker or workers with a promotion
 */
public class Title implements Serializable {

    private List<Worker> workers;

    private int teamSize;
    private Promotion promotion;
    private String name;

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

    public String getTitleHistory() {

        String string = "";

        for (TitleRecord tr : titleHistory) {
            string = tr.workers + " Day " + tr.startDate + " to " + tr.endDate + "\n" + string;

        }

        return string;
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

    private int dayWon;

    /**
     * @return the dayWon
     */
    public int getDayWon() {
        return dayWon;
    }

    /**
     * @param dayWon the dayWon to set
     */
    public void setDayWon(int dayWon) {
        this.dayWon = dayWon;
    }

    public void addRecord(int currentDate) {

        List<Worker> recordWorkers = new ArrayList<>();
        if (!isVacant()) {
            recordWorkers.addAll(workers);

        }

        if (isVacant() && dayWon != currentDate) {
            titleHistory.add(new TitleRecord(recordWorkers, dayWon, currentDate));
        } else if (!isVacant()) {
            titleHistory.add(new TitleRecord(recordWorkers, dayWon, currentDate));
        }

    }

    private List<TitleRecord> titleHistory = new ArrayList<>();

    /**
     * @return the teamSize
     */
    public int getTeamSize() {
        return teamSize;
    }

    private class TitleRecord {

        private final List<Worker> workers;
        private final int startDate;
        private final int endDate;

        public TitleRecord(List<Worker> workers, int startDate, int endDate) {
            this.workers = workers;
            this.startDate = startDate;
            this.endDate = endDate;
        }
    }

}
