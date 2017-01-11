package wrestling.file;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.DatatypeConverter;
import wrestling.model.factory.ContractFactory;
import wrestling.model.GameController;
import wrestling.model.Promotion;
import wrestling.model.Title;
import wrestling.model.Worker;
import wrestling.model.factory.TitleFactory;
import wrestling.model.factory.WorkerFactory;

/**
 *
 * for importing roster files etc
 */
public class Import {

    private GameController gameController;

    public GameController importController() throws IOException {

        gameController = new GameController();
        promotionsDat();
        workersDat();
        beltDat();
        gameController.setPromotions(promotions);
        gameController.setWorkers(allWorkers);

        //for statistical evaluation of data only
        boolean evaluate = false;

        if (evaluate) {
            EvaluateData evaluateData = new EvaluateData();
            evaluateData.evaluateData(promotions, allWorkers);
        }

        return gameController;
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
        if (intLetter >= 64 && intLetter <= 173) {

            letter += String.valueOf((char) (intLetter));

        } else {
            letter += " ";
        }

        return letter;
    }

    private final List<Promotion> promotions = new ArrayList<>();
    private final List<Integer> promotionKeys = new ArrayList<>();

    private void promotionsDat() throws IOException {

        Path path = Paths.get("promos.dat");
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

                Promotion promotion = new Promotion();

                counter = 0;

                //trim the line to get the promotion name
                currentLine = currentLine.substring(3, 43).trim();

                promotion.setIndexNumber(promotionKeys.get(promotionKeys.size() - 1));
                promotion.setName(currentLine);
                promotion.setLevel(6 - level);

                promotions.add(promotion);

                //reset the line for the next loop
                currentLine = "";
                level = 0;

            }
        }
    }

    private final List<Worker> allWorkers = new ArrayList<>();
    private List<String> workerIDs = new ArrayList<>();

    private void workersDat() throws IOException {

        Path path = Paths.get("wrestler.dat");
        byte[] data = Files.readAllBytes(path);

        String fileString = DatatypeConverter.printHexBinary(data);
        String currentLine = "";

        int counter = 0;
        int contractIndx = 0;
        int striking = 0;
        int wrestling = 0;
        int flying = 0;
        int popularity = 0;
        boolean manager = false;
        boolean fullTime = true;
        boolean mainRoster = true;

        String workerId = new String();
        for (int i = 0; i < fileString.length(); i += 2) {

            //combine the two characters into one string
            String hexValueString = new StringBuilder().append(fileString.charAt(i)).append(fileString.charAt(i + 1)).toString();

            currentLine += hexStringToLetter(hexValueString);

            counter++;

            switch (counter) {
                case 2:
                    workerId += hexValueString;

                    break;
                case 3:
                    workerId += hexValueString;

                    break;

                case 66:
                    contractIndx = hexStringToInt(hexValueString);
                    break;
                case 83:

                    switch (hexValueString) {
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
                            break;
                    }
                    break;
                case 148:
                    striking = hexStringToInt(hexValueString);
                    break;
                case 150:
                    wrestling = hexStringToInt(hexValueString);
                    break;
                case 152:
                    flying = hexStringToInt(hexValueString);
                    break;
                case 158:
                    popularity = hexStringToInt(hexValueString);
                    break;
                default:
                    break;
            }

            if (counter == (19 * 16) + 3) {

                currentLine = currentLine.substring(3, 28).trim();

                Worker worker = WorkerFactory.randomWorker();
                worker.setName(currentLine);
                worker.setShortName(currentLine);
                worker.setFlying(flying);
                worker.setStriking(striking);
                worker.setWrestling(wrestling);
                worker.setPopularity(popularity);
                worker.setManager(manager);
                worker.setFullTime(fullTime);
                worker.setMainRoster(mainRoster);

                //sign contracts for workers that match with promotion keys
                for (Promotion promotion : promotions) {
                    if (promotion.indexNumber() == contractIndx) {

                        ContractFactory.createContract(worker, promotion);

                    }
                }

                allWorkers.add(worker);

                counter = 0;
                contractIndx = 0;
                striking = 0;
                wrestling = 0;
                flying = 0;
                popularity = 0;
                manager = false;
                fullTime = true;
                mainRoster = true;

                currentLine = "";

                workerIDs.add(workerId);
                workerId = new String();

            }
        }
    }

    private List<String> beltWorkerIDs = new ArrayList<>();
    private List<Title> titles;
    private List<String> titleNames = new ArrayList<>();

    private void beltDat() throws IOException {
        Path path = Paths.get("belt.dat");
        byte[] data = Files.readAllBytes(path);

        String workerId = new String();
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
                titleNames.add(titleName);
                workerId = new String();
                titleName = new String();

            }
        }

        //add the belts
        for (int p = 0; p < promotions.size(); p++) {
            for (int t = 0; t < titleNames.size(); t++) {

                if (titlePromotionKeys.get(t).equals(promotions.get(p).indexNumber())) {

                    for (int w = 0; w < workerIDs.size(); w++) {

                        if (workerIDs.get(w).equals(beltWorkerIDs.get(t))) {
                            TitleFactory.createTitle(promotions.get(p), allWorkers.get(w), titleNames.get(t));

                        }
                    }

                }

            }
        }

    }

}
