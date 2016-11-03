package wrestling.file;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.DatatypeConverter;
import wrestling.model.Contract;
import wrestling.model.GameController;
import wrestling.model.Promotion;
import wrestling.model.Worker;
import wrestling.model.WorkerFactory;

/**
 *
 * for importing roster files etc
 */
public class Import {

    public GameController importController() throws IOException {

        promotionsDat();
        workersDat();
        GameController gameController = new GameController();
        gameController.setPromotions(promotions);
        gameController.setWorkers(allWorkers);

        /*
        for statistical evaluation of data only
        
        boolean evaluate = false;
        
        if (evaluate) {
            EvaluateData evaluateData = new EvaluateData();
            evaluateData.evaluateData(promotions, allWorkers);
        }
         */
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

    private void promotionsDat() throws IOException {

        Path path = Paths.get("promos.dat");
        byte[] data = Files.readAllBytes(path);

        List<Integer> promotionKeys = new ArrayList<>();
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

        WorkerFactory workerFactory = new WorkerFactory();

        for (int i = 0; i < fileString.length(); i += 2) {

            //combine the two characters into one string
            String hexValueString = new StringBuilder().append(fileString.charAt(i)).append(fileString.charAt(i + 1)).toString();

            currentLine += hexStringToLetter(hexValueString);

            counter++;

            switch (counter) {
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
                Worker worker = workerFactory.randomWorker();
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
                        Contract contract = new Contract(worker, promotion);
                        promotion.addContract(contract);
                        worker.addContract(contract);
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

            }
        }
    }

}
