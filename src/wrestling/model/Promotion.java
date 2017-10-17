package wrestling.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import wrestling.model.controller.PromotionController;
import wrestling.model.financial.BankAccount;

public class Promotion implements Serializable {

    private static int serialNumber = 0;

    private final BankAccount bankAccount;
    private String name;
    private String shortName;
    private String imagePath;
    private int indexNumber;
    private int popularity;
    private int level;
    private final List<Title> titles = new ArrayList<>();
    private final List<Contract> contracts;
    private PromotionController controller;

    public Promotion() {
        this.contracts = new ArrayList<>();

        bankAccount = new BankAccount();

        name = "Promotion #" + serialNumber;
        shortName = "PRO" + serialNumber;

        //default popularity of 50 for now
        popularity = 50;

        serialNumber++;
    }

    public BankAccount bankAccount() {
        return bankAccount;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setIndexNumber(int indexNumber) {
        this.indexNumber = indexNumber;
    }

    public int indexNumber() {
        return indexNumber;
    }

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

    public void addTitle(Title title) {
        this.getTitles().add(title);
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

    public List<Contract> getContracts() {
        return contracts;
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

    /**
     * @return the controller
     */
    public PromotionController getController() {
        return controller;
    }

    /**
     * @param controller the controller to set
     */
    public void setController(PromotionController controller) {
        this.controller = controller;
    }

}
