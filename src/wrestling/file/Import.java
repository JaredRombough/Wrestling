package wrestling.file;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.DatatypeConverter;
import wrestling.model.GameController;
import wrestling.model.Promotion;
import wrestling.model.Worker;
import wrestling.model.factory.ContractFactory;
import wrestling.model.factory.TitleFactory;
import wrestling.model.factory.WorkerFactory;

/**
 *
 * for importing roster files etc
 */
public class Import {

    private GameController gameController;

    private File importFolder;

    private final List<Promotion> promotions = new ArrayList<>();
    private final List<Integer> promotionKeys = new ArrayList<>();

    private final List<Worker> allWorkers = new ArrayList<>();
    private final List<String> workerIDs = new ArrayList<>();

    private final List<String> beltWorkerIDs = new ArrayList<>();
    private final List<String> beltWorkerIDs2 = new ArrayList<>();

    private final List<String> titleNames = new ArrayList<>();

    public GameController importController(File importFolder) throws IOException {
        this.importFolder = importFolder;
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
        if (intLetter >= 0 && intLetter <= 499) {

            letter += String.valueOf((char) (intLetter));

        } else {

            letter += " ";

        }

        return letter;
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

                Promotion promotion = new Promotion();

                counter = 0;

                //trim the line to get the promotion name etc
                promotion.setIndexNumber(promotionKeys.get(promotionKeys.size() - 1));
                promotion.setName(currentLine.substring(3, 43).trim());
                promotion.setShortName(currentLine.substring(43, 49).trim());
                promotion.setImagePath(currentLine.substring(49, 65).trim());
                promotion.setLevel(6 - level);

                //game model easier to manage if we only have 5 levels
                if (promotion.getLevel() == 0) {
                    promotion.setLevel(1);
                }

                promotions.add(promotion);

                //reset the line for the next loop
                currentLine = "";
                level = 0;

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

                Worker worker = WorkerFactory.randomWorker();
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
                        manager = false;
                        fullTime = true;
                        mainRoster = true;
                        break;
                }

                worker.setManager(manager);
                worker.setFullTime(fullTime);
                worker.setMainRoster(mainRoster);

                //sign contracts for workers that match with promotion keys
                for (Promotion promotion : promotions) {
                    if (promotion.indexNumber() == (hexStringToInt(currentHexLine.get(65)))) {
                        //handle written/open contracts
                        if (hexStringToLetter(currentHexLine.get(71)).equals("W")) {
                            ContractFactory.createContract(worker, promotion, gameController.date(), true);
                        } else {
                            ContractFactory.createContract(worker, promotion, gameController.date(), false);
                        }
                    } else if (promotion.indexNumber() == (hexStringToInt(currentHexLine.get(67)))) {
                        ContractFactory.createContract(worker, promotion, gameController.date());
                        worker.getContract(promotion).setExclusive(false);
                    } else if (promotion.indexNumber() == (hexStringToInt(currentHexLine.get(69)))) {
                        ContractFactory.createContract(worker, promotion, gameController.date());
                        worker.getContract(promotion).setExclusive(false);
                    }

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
        for (int p = 0; p < promotions.size(); p++) {

            //go through the titles
            for (int t = 0; t < titleNames.size(); t++) {

                //if promotion matches title
                if (titlePromotionKeys.get(t).equals(promotions.get(p).indexNumber())) {

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
                            TitleFactory.createTitle(promotions.get(p), titleHolders, titleNames.get(t));

                        }
                    }

                }

            }
        }

    }

}
