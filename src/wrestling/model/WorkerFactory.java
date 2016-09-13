package wrestling.model;

import com.opencsv.CSVParser;
import com.opencsv.CSVReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *
 * allows the creation of an arbitrary amount of random workers
 */
public class WorkerFactory implements Serializable {

    private List<String> firstNames;
    private List<String> lastNames;
    private Random random;

    public WorkerFactory() {
        this.firstNames = new ArrayList<String>();
        this.lastNames = new ArrayList<String>();
        random = new Random();
        prepareNameLists();
    }

    //public ArrayList roster;
    public ArrayList createRoster(int rosterSize) {

        ArrayList<Worker> roster = new ArrayList<Worker>();

        for (int i = 0; i < rosterSize; i++) {
            roster.add(randomWorker());

        }

        return roster;
    }

    public ArrayList createRoster(int rosterSize, int rosterLevel) {

        ArrayList<Worker> roster = new ArrayList<Worker>();

        for (int i = 0; i < rosterSize; i++) {
            roster.add(randomWorker(rosterLevel));

        }

        return roster;
    }

    //to generate a random worker at a given popularity level
    public Worker randomWorker(int level) {
        Worker worker = new Worker();

        //set the popularity to be proportionate to the level requested
        worker.setPopularity(rand20() + (rand20() * level));

        worker.setEndurance(rand100());
        worker.setFlying(rand100());
        worker.setLook(rand100());
        worker.setProficiency(rand100());
        worker.setReputation(rand100());
        worker.setWrestling(rand100());
        worker.setStrength(rand100());
        worker.setStriking(rand100());
        worker.setTalk(rand100());
        setRandomName(worker);

        return worker;
    }

    public Worker randomWorker() {
        Worker worker = new Worker();

        worker.setPopularity(rand100());
        worker.setEndurance(rand100());
        worker.setFlying(rand100());
        worker.setLook(rand100());
        worker.setProficiency(rand100());
        worker.setReputation(rand100());
        worker.setWrestling(rand100());
        worker.setStrength(rand100());
        worker.setStriking(rand100());
        worker.setTalk(rand100());
        setRandomName(worker);

        return worker;
    }

    private void setRandomName(Worker worker) {
        String nameString = new String();

        int index = random.nextInt(firstNames.size());
        String firstName = firstNames.get(index);
        firstName = firstName.toLowerCase();
        firstName = firstName.substring(0, 1).toUpperCase() + firstName.substring(1);

        index = random.nextInt(lastNames.size());
        String lastName = lastNames.get(index);

        nameString = firstName + " " + lastName;

        worker.setName(nameString);
        worker.setShortName(lastName);
    }

    private void prepareNameLists() {

        try {
            CSVReader reader = new CSVReader(new FileReader("commonNames.csv"), CSVParser.DEFAULT_SEPARATOR, CSVParser.DEFAULT_QUOTE_CHARACTER, 1);
            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                firstNames.add(nextLine[0]);
                if (nextLine[1].length() != 0) {
                    lastNames.add(nextLine[1].replace(";", ""));
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private int rand100() {
        Random r = new Random();
        return r.nextInt(100 - 0) + 0;
    }

    private int rand20() {
        Random r = new Random();
        return r.nextInt(20 - 0) + 0;

    }

}
