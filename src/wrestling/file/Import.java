package wrestling.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.DayOfWeek;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.xml.bind.DatatypeConverter;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import wrestling.model.EventName;
import wrestling.model.Promotion;
import wrestling.model.TagTeam;
import wrestling.model.TagTeamWorker;
import wrestling.model.Television;
import wrestling.model.Worker;
import wrestling.model.controller.GameController;

/**
 *
 * for importing roster files etc
 */
public class Import {

    private GameController gameController;

    private transient Logger logger = LogManager.getLogger(getClass());

    private File importFolder;

    private final List<Promotion> allPromotions = new ArrayList<>();
    private final List<Integer> promotionKeys = new ArrayList<>();
    private final List<String> otherPromotionNames = new ArrayList<>();

    private final List<Worker> allWorkers = new ArrayList<>();
    private final List<String> workerIDs = new ArrayList<>();
    private final List<Worker> otherWorkers = new ArrayList<>();
    private final List<String> otherWorkerPromotions = new ArrayList<>();

    private final List<String> beltWorkerIDs = new ArrayList<>();
    private final List<String> beltWorkerIDs2 = new ArrayList<>();

    private final List<String> titleNames = new ArrayList<>();

    private final List<TagTeam> allTagTeams = new ArrayList<>();

    private final List<Television> allTelevision = new ArrayList<>();

    private final List<EventName> allEventNames = new ArrayList<>();

    private final List<String> filesNeeded = new ArrayList<>(Arrays.asList(
            "promos",
            "belt",
            "teams",
            "wrestler"
    ));

    public String tryImport(File importFolder) throws Exception {

        StringBuilder sb = new StringBuilder();
        this.importFolder = importFolder;
        filesNeeded.stream().map((s) -> new File(importFolder.getPath() + "\\" + s + ".dat")).filter((f) -> (!f.exists() || f.isDirectory())).forEach((f) -> {
            sb.append(f.toString()).append(" not found.").append("\n");
        });

        if (sb.length() == 0) {
            try {
                gameController = new GameController(false);
                promotionsDat();
                workersDat();
                teamsDat();
                beltDat();
                tvDat();
                eventDat();
            } catch (Exception ex) {

                sb.append(ex);
                logger.log(Level.ERROR, ex);
                throw ex;
            }
            processOther();
            gameController.getPromotionManager().addPromotions(allPromotions);
            gameController.getEventManager().addEventNames(allEventNames);
            gameController.getWorkerManager().addWorkers(allWorkers);
            gameController.getTagTeamManager().addTagTeams(allTagTeams);
            gameController.getTelevisionManager().addTelevision(allTelevision);

            //for statistical evaluation of data only
            /* boolean evaluate = false;
            if (evaluate) {
                EvaluateData.evaluateData(allPromotions, allWorkers);
            }*/
        }

        return sb.toString();

    }

    private Promotion getPromotionFromKey(int key) {
        for (int i = 0; i < promotionKeys.size(); i++) {
            if (promotionKeys.get(i).equals(key)) {
                return allPromotions.get(i);
            }
        }
        return null;
    }

    private int hexStringToInt(String hexValueString) {
        return Integer.parseInt(hexValueString, 16);
    }

    private String hexStringToLetter(String hexValueString) {
        //take the characters in two positions, since they combine to make
        //up one hex value that we have to translate
        String letter = "";
        //translate the hex value string to an int value
        int intLetter = hexStringToInt(hexValueString);

        //only keep numbers that translate to an ascii alphabet value
        //otherwise just put a blank in our string
        //this will need to be more complex if we import more than just names
        if (intLetter >= 0 && intLetter <= 499) {
            letter += String.valueOf((char) (intLetter));
        } else {
            letter += " ";
        }

        return letter;
    }

    private void processOther() {
        otherPromotionNames.stream().map((s) -> {
            Promotion p = gameController.getPromotionFactory().newPromotion();
            p.setName(s);
            p.setShortName(s);
            return p;
        }).map((p) -> {
            //calculate promotion level
            int totalPop = 0;
            int totalWorkers = 0;
            for (int i = 0; i < otherWorkers.size(); i++) {
                if (otherWorkerPromotions.get(i).equals(p.getName())) {
                    totalPop += otherWorkers.get(i).getPopularity();
                    totalWorkers++;
                }
            }
            int avgPop = totalPop / totalWorkers;
            p.setLevel((int) ((avgPop - (avgPop % 20)) / 20) + 1);
            p.setPromotionID(allPromotions.size());
            allPromotions.add(p);
            return p;
        }).forEach((p) -> {
            for (int i = 0; i < otherWorkers.size(); i++) {
                if (otherWorkerPromotions.get(i).equals(p.getName())) {
                    getGameController().getContractFactory().createContract(otherWorkers.get(i), p, getGameController().getDateManager().today(), false);
                }
            }
        });

        updateOtherPromotions(allPromotions, importFolder);
    }

    private void tvDat() throws IOException {
        Path path = Paths.get(importFolder.getPath() + "\\tv.dat");
        byte[] data = Files.readAllBytes(path);

        String fileString = DatatypeConverter.printHexBinary(data);
        String currentLine = "";
        List<String> currentHexLine = new ArrayList<>();
        List<String> currentStringLine = new ArrayList<>();
        int counter = 0;

        for (int i = 0; i < fileString.length(); i += 2) {

            //combine the two characters into one string
            String hexValueString = new StringBuilder().append(fileString.charAt(i)).append(fileString.charAt(i + 1)).toString();

            currentLine += hexStringToLetter(hexValueString);
            currentHexLine.add(hexValueString);
            currentStringLine.add(hexStringToLetter(hexValueString));

            counter++;

            if (counter == 51) {

                Television tv = new Television();
                tv.setName(currentLine.substring(1, 21).trim());
                String day = currentLine.substring(22, 32);

                switch (day.trim().toUpperCase()) {
                    case "MONDAY":
                        tv.setDay(DayOfWeek.MONDAY);
                        break;
                    case "TUESDAY":
                        tv.setDay(DayOfWeek.TUESDAY);
                        break;
                    case "WEDNESDAY":
                        tv.setDay(DayOfWeek.WEDNESDAY);
                        break;
                    case "THURSDAY":
                        tv.setDay(DayOfWeek.THURSDAY);
                        break;
                    case "FRIDAY":
                        tv.setDay(DayOfWeek.FRIDAY);
                        break;
                    case "SATURDAY":
                        tv.setDay(DayOfWeek.SATURDAY);
                        break;
                    case "SUNDAY":
                        tv.setDay(DayOfWeek.SUNDAY);
                        break;

                }
                int key = hexStringToInt(currentHexLine.get(21));
                for (int x = 0; x < promotionKeys.size(); x++) {
                    if (promotionKeys.get(x) == key) {
                        tv.setPromotion(allPromotions.get(x));
                    }
                }

                allTelevision.add(tv);

                counter = 0;
                currentLine = "";
                currentHexLine = new ArrayList<>();
                currentStringLine = new ArrayList<>();

            }
        }
    }

    private void promotionsDat() throws IOException {

        Path path = Paths.get(importFolder.getPath() + "\\promos.dat");
        byte[] data = Files.readAllBytes(path);

        String fileString = DatatypeConverter.printHexBinary(data);
        String currentLine = "";
        int counter = 0;
        int level = 0;
        for (int i = 0; i < fileString.length(); i += 2) {

            //combine the two characters into one string
            String hexValueString = new StringBuilder().append(fileString.charAt(i)).append(fileString.charAt(i + 1)).toString();

            currentLine += hexStringToLetter(hexValueString);

            //track the key number for this promotion
            if (counter == 1) {
                promotionKeys.add(hexStringToInt(hexValueString));
            } else if (counter == 89) {
                level = hexStringToInt(hexValueString);
                if (level == 6) {
                    level = 5;
                }
            }

            counter++;

            if (counter == (25 * 16) - 3) {

                Promotion promotion = gameController.getPromotionFactory().newPromotion();

                counter = 0;

                //trim the line to get the promotion name etc
                promotion.setPromotionID(promotionKeys.get(promotionKeys.size() - 1));
                promotion.setName(currentLine.substring(3, 43).trim());
                promotion.setShortName(currentLine.substring(43, 49).trim());
                promotion.setImagePath(currentLine.substring(49, 65).trim());
                promotion.setLevel(6 - level);

                //game model easier to manage if we only have 5 levels
                if (promotion.getLevel() == 0) {
                    promotion.setLevel(1);
                }

                allPromotions.add(promotion);

                //reset the line for the next loop
                currentLine = "";
                level = 0;

            }
        }
    }

    private void teamsDat() throws IOException {
        Path path = Paths.get(importFolder.getPath() + "\\teams.dat");
        byte[] data = Files.readAllBytes(path);

        String fileString = DatatypeConverter.printHexBinary(data);
        String currentLine = "";
        List<String> currentHexLine = new ArrayList<>();
        List<String> currentStringLine = new ArrayList<>();
        int counter = 0;

        for (int i = 0; i < fileString.length(); i += 2) {

            //combine the two characters into one string
            String hexValueString = new StringBuilder().append(fileString.charAt(i)).append(fileString.charAt(i + 1)).toString();

            currentLine += hexStringToLetter(hexValueString);
            currentHexLine.add(hexValueString);
            currentStringLine.add(hexStringToLetter(hexValueString));

            counter++;

            if (counter == 59) {

                TagTeam team = new TagTeam();
                String id1 = currentHexLine.get(26) + currentHexLine.get(27);
                String id2 = currentHexLine.get(28) + currentHexLine.get(29);

                team.setName(currentLine.substring(1, 18).trim());
                for (int x = 0; x < allWorkers.size(); x++) {

                    if (workerIDs.get(x).equals(id1)
                            || workerIDs.get(x).equals(id2)) {
                        TagTeamWorker tagTeamWorker = new TagTeamWorker(team, allWorkers.get(x));
                        gameController.getTagTeamManager().addTagTeamWorker(tagTeamWorker);
                    }
                }
                team.setExperience(hexStringToInt(currentHexLine.get(55)));

                team.setActive(currentHexLine.get(57).equals("FF"));

                allTagTeams.add(team);

                counter = 0;
                currentLine = "";
                currentHexLine = new ArrayList<>();
                currentStringLine = new ArrayList<>();

            }
        }
    }

    private void workersDat() throws IOException {

        Path path = Paths.get(importFolder.getPath() + "\\wrestler.dat");
        byte[] data = Files.readAllBytes(path);

        String fileString = DatatypeConverter.printHexBinary(data);
        String currentLine = "";
        List<String> currentHexLine = new ArrayList<>();
        List<String> currentStringLine = new ArrayList<>();
        int counter = 0;

        for (int i = 0; i < fileString.length(); i += 2) {

            //combine the two characters into one string
            String hexValueString = new StringBuilder().append(fileString.charAt(i)).append(fileString.charAt(i + 1)).toString();

            currentLine += hexStringToLetter(hexValueString);
            currentHexLine.add(hexValueString);
            currentStringLine.add(hexStringToLetter(hexValueString));

            counter++;

            if (counter == (19 * 16) + 3) {

                Worker worker = gameController.getWorkerFactory().randomWorker();

                worker.setName(currentLine.substring(3, 27).trim());
                worker.setShortName(currentLine.substring(28, 38).trim());
                worker.setImageString(currentLine.substring(45, 65).trim());
                worker.setFlying(hexStringToInt(currentHexLine.get(151)));
                worker.setStriking(hexStringToInt(currentHexLine.get(147)));
                worker.setWrestling(hexStringToInt(currentHexLine.get(149)));
                worker.setPopularity(hexStringToInt(currentHexLine.get(157)));
                worker.setCharisma(hexStringToInt(currentHexLine.get(159)));
                worker.setBehaviour(hexStringToInt(currentHexLine.get(255)));

                boolean manager;
                boolean fullTime;
                boolean mainRoster;

                switch (currentHexLine.get(82)) {
                    case "07":
                        //development
                        manager = false;
                        fullTime = true;
                        mainRoster = false;
                        break;
                    case "19":
                        //non-wrestler
                        manager = false;
                        fullTime = false;
                        mainRoster = true;
                        break;
                    case "32":
                        //manager
                        manager = true;
                        fullTime = true;
                        mainRoster = true;
                        break;
                    default:
                        //shouldn't happen
                        manager = false;
                        fullTime = true;
                        mainRoster = true;
                        break;
                }

                worker.setManager(manager);
                worker.setFullTime(fullTime);
                worker.setMainRoster(mainRoster);

                String otherPromotionName = (currentStringLine.get(76) + currentStringLine.get(77)).trim();
                if (!otherPromotionName.trim().isEmpty()) {
                    otherWorkers.add(worker);
                    otherWorkerPromotions.add(otherPromotionName);
                    if (!otherPromotionNames.contains(otherPromotionName)) {
                        otherPromotionNames.add(otherPromotionName);
                    }

                }

                //look for extra promotions
                //sign contracts for workers that match with promotion keys
                for (Promotion p : allPromotions) {
                    checkForContract(p, worker, currentHexLine);
                }

                allWorkers.add(worker);
                workerIDs.add(currentHexLine.get(1) + currentHexLine.get(2));
                counter = 0;
                currentLine = "";
                currentHexLine = new ArrayList<>();
                currentStringLine = new ArrayList<>();

            }
        }
    }

    private void checkForContract(Promotion p, Worker w, List<String> currentHexLine) {
        if (p.indexNumber() == (hexStringToInt(currentHexLine.get(65)))) {
            //handle written/open contracts
            if (hexStringToLetter(currentHexLine.get(71)).equals("W")) {
                getGameController().getContractFactory().createContract(w, p, getGameController().getDateManager().today(), true);
            } else {
                getGameController().getContractFactory().createContract(w, p, getGameController().getDateManager().today(), false);
            }
        } else if (p.indexNumber() == (hexStringToInt(currentHexLine.get(67)))) {
            getGameController().getContractFactory().createContract(w, p, getGameController().getDateManager().today());
            gameController.getContractManager().getContract(w, p).setExclusive(false);
        } else if (p.indexNumber() == (hexStringToInt(currentHexLine.get(69)))) {
            getGameController().getContractFactory().createContract(w, p, getGameController().getDateManager().today());
            gameController.getContractManager().getContract(w, p).setExclusive(false);
        }
    }

    private void eventDat() throws IOException {
        Path path = Paths.get(importFolder.getPath() + "\\event.dat");
        byte[] data = Files.readAllBytes(path);

        String fileString = DatatypeConverter.printHexBinary(data);
        String currentLine = "";
        Promotion promotion = null;
        Month month = null;
        int counter = 0;

        for (int i = 0; i < fileString.length(); i += 2) {

            String hexValueString = new StringBuilder().append(fileString.charAt(i)).append(fileString.charAt(i + 1)).toString();
            currentLine += hexStringToLetter(hexValueString);
            counter++;

            if (counter == 34) {
                promotion = getPromotionFromKey(hexStringToInt(hexValueString));
            }

            if (counter == 36) {
                month = Month.of(hexStringToInt(hexValueString));
            }

            if (counter == 47) {
                allEventNames.add(new EventName(
                        currentLine.substring(1, 32).trim(),
                        promotion,
                        month
                ));
                currentLine = "";
                counter = 0;

            }
        }
    }

    private void beltDat() throws IOException {
        Path path = Paths.get(importFolder.getPath() + "\\belt.dat");
        byte[] data = Files.readAllBytes(path);

        String workerId = new String();
        String workerId2 = new String();
        String titleName = new String();

        List<Integer> titlePromotionKeys = new ArrayList<>();
        String fileString = DatatypeConverter.printHexBinary(data);
        String currentLine = "";

        int counter = 0;

        for (int i = 0; i < fileString.length(); i += 2) {

            //combine the two characters into one string
            String hexValueString = new StringBuilder().append(fileString.charAt(i)).append(fileString.charAt(i + 1)).toString();

            currentLine += hexStringToLetter(hexValueString);

            switch (counter) {
                case 33:
                    //track the promotion number associated with this belt
                    titlePromotionKeys.add(hexStringToInt(hexValueString));
                    break;
                case 35:
                    //workerID is two hex values that correspond to the worker
                    //with the belt
                    workerId += hexValueString;
                    break;
                case 36:
                    workerId += hexValueString;
                    break;
                case 37:
                    workerId2 += hexValueString;
                    break;
                case 38:
                    workerId2 += hexValueString;
                    break;
                default:
                    break;
            }

            counter++;

            if (counter == (28 * 16) + 9) {

                counter = 0;

                currentLine = currentLine.substring(1, 31).trim();
                titleName += currentLine;

                //reset the line for the next loop
                currentLine = "";

                beltWorkerIDs.add(workerId);
                beltWorkerIDs2.add(workerId2);
                titleNames.add(titleName);
                workerId = "";
                workerId2 = "";
                titleName = "";

            }
        }

        //go through the promotions
        for (int p = 0; p < allPromotions.size(); p++) {

            //go through the titles
            for (int t = 0; t < titleNames.size(); t++) {

                //if promotion matches title
                if (titlePromotionKeys.get(t).equals(allPromotions.get(p).indexNumber())) {

                    //go through the workers
                    for (int w = 0; w < workerIDs.size(); w++) {

                        //list to hold the title holder(s) we find
                        List<Worker> titleHolders = new ArrayList<>();

                        if (workerIDs.get(w).equals(beltWorkerIDs.get(t))) {

                            titleHolders.add(allWorkers.get(w));

                            //check for a tag team partner
                            for (int w2 = 0; w2 < workerIDs.size(); w2++) {
                                if (workerIDs.get(w2).equals(beltWorkerIDs2.get(t))) {

                                    titleHolders.add(allWorkers.get(w2));
                                    break;
                                }
                            }

                            //create the title
                            getGameController().getTitleFactory().createTitle(allPromotions.get(p), titleHolders, titleNames.get(t));

                        }
                    }
                }
            }
        }
    }

    public void updateOtherPromotions(List<Promotion> promotions, File importFolder) {

        List<String> advancedImportData = new ArrayList();
        String path = "";

        try {
            path = importFolder.getPath() + "\\advancedImport.txt";
            BufferedReader br = new BufferedReader(new FileReader(path));
            String line;
            while ((line = br.readLine()) != null) {
                advancedImportData.add(line);
            }
        } catch (FileNotFoundException ex) {
            logger.log(Level.ERROR, "Advanced Import file not found at " + path, ex);
            logger.log(Level.ERROR, "Proceding without advanced import");
            advancedImportData = new ArrayList();
        } catch (IOException ex) {
            logger.log(Level.ERROR, "Advanced Import file read error", ex);
            logger.log(Level.ERROR, "Proceding without advanced import");
            advancedImportData = new ArrayList();
        }

        for (Promotion promotion : promotions) {
            for (int i = 0; i < advancedImportData.size(); i++) {
                if (advancedImportData.get(i).equals(promotion.getName())
                        && advancedImportData.size() >= i + 3) {
                    promotion.setName(advancedImportData.get(i + 1));
                    promotion.setShortName(advancedImportData.get(i + 2));
                    promotion.setImagePath(advancedImportData.get(i + 3));
                    break;
                }
            }
        }
    }

    /**
     * @return the gameController
     */
    public GameController getGameController() {
        return gameController;
    }

}
