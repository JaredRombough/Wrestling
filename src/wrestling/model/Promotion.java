package wrestling.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import wrestling.model.financial.BankAccount;

public class Promotion implements Serializable {

    public Promotion() {
        this.contracts = new ArrayList<>();

        eventArchives = new ArrayList<>();

        bankAccount = new BankAccount();

        name = "Promotion #" + serialNumber;

        //default popularity of 50 for now
        popularity = 50;

        serialNumber++;
    }

    private PromotionAi ai;

    private final BankAccount bankAccount;

    public BankAccount bankAccount() {
        return bankAccount;
    }

    public void setAi(PromotionAi ai) {
        this.ai = ai;
    }

    public PromotionAi getAi() {
        return ai;
    }

    private boolean hasAi() {
        return ai != null;
    }

    public List<Worker> getActiveRoster() {

        List<Worker> roster = new ArrayList<>();
        for (Contract contract : contracts) {
            if (contract.getWorker().isFullTime() && !contract.getWorker().isManager()) {
                roster.add(contract.getWorker());
            }

        }

        return roster;
    }

    public List<Worker> getFullRoster() {

        List<Worker> roster = new ArrayList<>();
        for (Contract contract : contracts) {
            roster.add(contract.getWorker());
        }

        return roster;
    }

    private String name;
    private String shortName;
    private String imagePath;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
    private static int serialNumber = 0;
    private int indexNumber;

    public void setIndexNumber(int indexNumber) {
        this.indexNumber = indexNumber;
    }

    public int indexNumber() {
        return indexNumber;
    }

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

    public int averageWorkerPopularity() {
        int totalPop = 0;
        int averagePop = 0;

        if (!getFullRoster().isEmpty()) {
            for (Worker worker : getFullRoster()) {
                totalPop += worker.getPopularity();
            }
            averagePop = totalPop / getFullRoster().size();
        }

        return averagePop;
    }

    public void gainPopularity(int score) {
        if (score > (level * 20)) {
            gainPopularity();
        }
    }

    public void gainPopularity() {
        popularity += 1;
        if (popularity >= 100) {
            if (level != 5) {
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
        if (level < 1) {
            level = 1;
        }
        this.level = level;
    }

    private final List<EventArchive> eventArchives;

    //used for browsing events
    public List<EventArchive> getEventArchives() {
        return Collections.unmodifiableList(eventArchives);
    }

    public void archiveEvent(EventArchive event) {
        eventArchives.add(event);
    }

    private final List<Title> titles = new ArrayList<>();

    public void addTitle(Title title) {
        this.getTitles().add(title);
    }

    @Override
    public String toString() {
        return name;
    }

    private final List<Contract> contracts;

    public void addContract(Contract contract) {
        this.contracts.add(contract);
        if (hasAi()) {
            getAi().updatePushList();
        }

    }

    public void removeContract(Contract contract) {
        this.contracts.remove(contract);
        if (hasAi()) {
            getAi().updatePushList();
        }

    }

    public List<Contract> getContracts() {
        return contracts;
    }

    //the maximum popularity worker the promotion can hire
    public int maxPopularity() {
        return level * 20;
    }

    /**
     * @return the titles
     */
    public List<Title> getTitles() {
        return titles;
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
     * @return the imagePath
     */
    public String getImagePath() {
        return imagePath;
    }

    /**
     * @param imagePath the imagePath to set
     */
    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

}
