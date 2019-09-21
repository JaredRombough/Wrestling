package openwrestling.file;

import lombok.Getter;
import openwrestling.model.EventTemplate;
import openwrestling.model.controller.GameController;
import openwrestling.model.factory.PersonFactory;
import openwrestling.model.gameObjects.Contract;
import openwrestling.model.gameObjects.Promotion;
import openwrestling.model.gameObjects.RosterSplit;
import openwrestling.model.gameObjects.Stable;
import openwrestling.model.gameObjects.TagTeam;
import openwrestling.model.gameObjects.Worker;
import openwrestling.model.interfaces.iRosterSplit;
import openwrestling.model.modelView.StaffView;
import openwrestling.model.modelView.TitleView;
import openwrestling.model.segmentEnum.ActiveType;
import openwrestling.model.segmentEnum.EventBroadcast;
import openwrestling.model.segmentEnum.EventFrequency;
import openwrestling.model.segmentEnum.EventRecurrence;
import openwrestling.model.segmentEnum.Gender;
import openwrestling.model.segmentEnum.StaffType;
import openwrestling.model.utility.ContractUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.xml.bind.DatatypeConverter;
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
import java.util.Objects;

import static openwrestling.file.ImportUtils.*;
import static openwrestling.model.constants.GameConstants.*;

public class Import {

    private GameController gameController;

    private final transient Logger logger = LogManager.getLogger(getClass());

    private File importFolder;

    private final List<Promotion> allPromotions = new ArrayList<>();
    @Getter
    private final List<Integer> promotionKeys = new ArrayList<>();
    private final List<String> otherPromotionNames = new ArrayList<>();

    private List<Worker> allWorkers = new ArrayList<>();
    private final List<String> workerIDs = new ArrayList<>();
    private final List<String> managerIDs = new ArrayList<>();
    private final List<Worker> otherWorkers = new ArrayList<>();
    private final List<String> otherWorkerPromotions = new ArrayList<>();

    private final List<TagTeam> allTagTeams = new ArrayList<>();

    private final List<EventTemplate> eventTemplates = new ArrayList<>();
    private final List<Stable> stablesToAdd = new ArrayList<>();
    private final List<RosterSplit> rosterSplitsToAdd = new ArrayList<>();
    private final List<Contract> contractsToAdd = new ArrayList<>();

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
                promotionsDat(importFolder);
                workersDat();
                setManagers();
                teamsDat();
                stablesDat();
                beltDat();
                tvDat();
                eventDat();
                staffDat();
                relateDat();
            } catch (Exception ex) {

                sb.append(ex);
                logger.log(Level.ERROR, ex);
                throw ex;
            }
            processOther();

            gameController.getPromotionManager().createPromotions(allPromotions);

            contractsToAdd.stream().forEach(contract -> {
                contract.setWorker(getMatchingWorker(contract.getWorker()));
            });
            gameController.getContractManager().createContracts(contractsToAdd);
            gameController.getEventManager().addEventTemplates(eventTemplates);
            gameController.getTagTeamManager().createTagTeams(allTagTeams);
            gameController.getStableManager().createStables(stablesToAdd);
            List<RosterSplit> updated = new ArrayList<>();

            rosterSplitsToAdd.forEach(rosterSplit -> {
                RosterSplit rosterSplit1 = RosterSplit.builder()
                        .name(rosterSplit.getName())
                        .owner(rosterSplit.getOwner())
                        .workers(new ArrayList<>())
                        .build();

                rosterSplit.getWorkers().forEach(worker -> {
                    Worker toAdd = allWorkers.stream().filter(allWorker -> workersMatch(allWorker, worker))
                            .findFirst()
                            .orElse(null);
                    rosterSplit1.getWorkers().add(toAdd);
                });
                updated.add(rosterSplit1);
            });
            gameController.getRosterSplitManager().createRosterSplits(updated);

            //for statistical evaluation of data only
            /* boolean evaluate = false;
            if (evaluate) {
                EvaluateData.evaluateData(allPromotions, allWorkers);
            }*/
        }

        return sb.toString();

    }

    private Worker getMatchingWorker(Worker worker) {
        return allWorkers.stream().filter(allWorker -> workersMatch(allWorker, worker))
                .findFirst()
                .orElse(null);
    }

    private boolean workersMatch(Worker worker1, Worker worker2) {
        return Objects.equals(worker1.getName(), worker2.getName()) &&
                Objects.equals(worker1.getShortName(), worker2.getShortName()) &&
                Objects.equals(worker1.getAge(), worker2.getAge()) &&
                Objects.equals(worker1.getStriking(), worker2.getStriking());
    }

    private Promotion getPromotionFromKey(int key) {
        for (int i = 0; i < promotionKeys.size(); i++) {
            if (promotionKeys.get(i).equals(key)) {
                return allPromotions.get(i);
            }
        }
        return null;
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
        }).forEach((promotion) -> {
            for (int i = 0; i < otherWorkers.size(); i++) {
                if (otherWorkerPromotions.get(i).equals(promotion.getName())) {
                    contractsToAdd.add(Contract.builder()
                            .worker(otherWorkers.get(i))
                            .promotion(promotion)
                            .exclusive(false)
                            .active(true)
                            .startDate(getGameController().getDateManager().today())
                            .endDate(ContractUtils.contractEndDate(getGameController().getDateManager().today(), RandomUtils.nextInt(0, 12)))
                            .build());
                }
            }
        });

        updateOtherPromotions(allPromotions, importFolder);
    }

    private void tvDat() throws IOException {
        Path path = Paths.get(importFolder.getPath() + "\\tv.dat");
        byte[] data = Files.readAllBytes(path);
        int timeSlotIndex = 32;
        int promotionKeyIndex = 21;

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

                EventTemplate eventTemplate = new EventTemplate();
                eventTemplate.setEventBroadcast(EventBroadcast.TELEVISION);
                eventTemplate.setEventFrequency(EventFrequency.WEEKLY);
                eventTemplate.setEventRecurrence(EventRecurrence.LIMITED);
                eventTemplate.setEventsLeft(RandomUtils.nextInt(30, 60));
                eventTemplate.setName(currentLine.substring(1, 21).trim());

                int duration = 0;
                switch (currentStringLine.get(timeSlotIndex)) {
                    case "P":
                        duration = 120;
                        break;
                    case "G":
                    case "E":
                    case "L":
                        duration = 60;
                        break;
                }
                eventTemplate.setDefaultDuration(duration);

                eventTemplate.setDayOfWeek(DayOfWeek.valueOf(
                        currentLine.substring(22, 32).toUpperCase().trim()));

                int key = hexStringToInt(currentHexLine.get(promotionKeyIndex));
                for (int x = 0; x < promotionKeys.size(); x++) {
                    if (promotionKeys.get(x) == key) {
                        eventTemplate.setPromotion(allPromotions.get(x));
                    }
                }

                assignRosterSplit(eventTemplate, eventTemplate.getPromotion(), eventTemplate.getName());
                eventTemplates.add(eventTemplate);

                counter = 0;
                currentLine = "";
                currentHexLine = new ArrayList<>();
                currentStringLine = new ArrayList<>();

            }
        }
    }

    List<Promotion> promotionsDat(File importFolder) {
        List<Promotion> promotions = new ArrayList<>();
        List<List<String>> hexLines = getHexLines(importFolder, "promos", 397);

        hexLines.forEach(hexLine -> {
            Promotion promotion = new Promotion();
            //TODO create bank accounts for imported promotions
            String textLine = hexLineToTextString(hexLine);

            promotionKeys.add(hexStringToInt(hexLine.get(1)));

            promotion.setImportKey(hexStringToInt(hexLine.get(1)));
            promotion.setName(textLine.substring(3, 43).trim());
            promotion.setShortName(textLine.substring(43, 49).trim());
            promotion.setImagePath(textLine.substring(49, 65).trim());
            promotion.setLevel(6 - hexStringToInt(hexLine.get(89)));

            allPromotions.add(promotion);
            promotions.add(promotion);
        });
        return promotions;
    }

    private void teamsDat() throws IOException {
        Path path = Paths.get(importFolder.getPath() + "\\teams.dat");
        byte[] data = Files.readAllBytes(path);

        String fileString = DatatypeConverter.printHexBinary(data);
        String currentLine = "";
        List<String> currentHexLine = new ArrayList<>();
        List<String> currentStringLine = new ArrayList<>();
        int counter = 0;
        int lineLength = 59;

        for (int i = 0; i < fileString.length(); i += 2) {

            //combine the two characters into one string
            String hexValueString = new StringBuilder().append(fileString.charAt(i)).append(fileString.charAt(i + 1)).toString();

            currentLine += hexStringToLetter(hexValueString);
            currentHexLine.add(hexValueString);
            currentStringLine.add(hexStringToLetter(hexValueString));

            counter++;

            if (counter == lineLength) {

                TagTeam tagTeam = new TagTeam();
                String id1 = currentHexLine.get(26) + currentHexLine.get(27);
                String id2 = currentHexLine.get(28) + currentHexLine.get(29);

                tagTeam.setName(currentLine.substring(1, 18).trim());
                for (int x = 0; x < allWorkers.size(); x++) {

                    if (workerIDs.get(x).equals(id1)
                            || workerIDs.get(x).equals(id2)) {
                        tagTeam.addWorker(allWorkers.get(x));
                    }
                }
                tagTeam.setExperience(hexStringToInt(currentHexLine.get(55)));
                tagTeam.setActiveType(currentHexLine.get(57).equals("FF")
                        ? ActiveType.ACTIVE : ActiveType.INACTIVE);

                allTagTeams.add(tagTeam);

                counter = 0;
                currentLine = "";
                currentHexLine = new ArrayList<>();
                currentStringLine = new ArrayList<>();

            }
        }
    }

    private void stablesDat() throws IOException {
        Path path = Paths.get(importFolder.getPath() + "\\stables.dat");
        byte[] data = Files.readAllBytes(path);

        String fileString = DatatypeConverter.printHexBinary(data);
        String currentLine = "";
        List<String> currentHexLine = new ArrayList<>();
        List<String> currentStringLine = new ArrayList<>();
        int counter = 0;
        int lineLength = 69;

        for (int i = 0; i < fileString.length(); i += 2) {

            String hexValueString = new StringBuilder().append(fileString.charAt(i)).append(fileString.charAt(i + 1)).toString();

            if (counter == lineLength) {
                Stable stable = new Stable();
                stable.setName(currentLine.substring(1, 24).trim());
                stable.setOwner(getPromotionFromKey(hexStringToInt(currentHexLine.get(26))));

                for (int f = 28; f < currentHexLine.size() - 1; f += 2) {
                    String id1 = currentHexLine.get(f) + currentHexLine.get(f + 1);
                    if (workerIDs.indexOf(id1) > -1
                            && !stable.getWorkers().contains(allWorkers.get(workerIDs.indexOf(id1)))) {
                        stable.getWorkers().add(allWorkers.get(workerIDs.indexOf(id1)));
                    }
                }

                stablesToAdd.add(stable);

                counter = 0;
                currentLine = "";
                currentHexLine = new ArrayList<>();
                currentStringLine = new ArrayList<>();
            } else {
                currentLine += hexStringToLetter(hexValueString);
                currentHexLine.add(hexValueString);
                currentStringLine.add(hexStringToLetter(hexValueString));
                counter++;
            }
        }

    }

    private void staffDat() throws IOException {
        List<StaffView> staffViews = new ArrayList<>();

        Path path = Paths.get(importFolder.getPath() + "\\staff.dat");
        byte[] data = Files.readAllBytes(path);

        String fileString = DatatypeConverter.printHexBinary(data);
        String currentLine = "";
        List<String> currentHexLine = new ArrayList<>();
        List<String> currentStringLine = new ArrayList<>();
        int counter = 0;
        int lineLength = 79;

        for (int i = 0; i < fileString.length(); i += 2) {

            //combine the two characters into one string
            String hexValueString = new StringBuilder().append(fileString.charAt(i)).append(fileString.charAt(i + 1)).toString();

            currentLine += hexStringToLetter(hexValueString);
            currentHexLine.add(hexValueString);
            currentStringLine.add(hexStringToLetter(hexValueString));

            counter++;

            if (counter == lineLength) {
                StaffView staff = new StaffView();
                staff.setName(currentLine.substring(3, 27).trim());
                staff.setImageString(currentLine.substring(34, 53).trim());
                staff.setGender(
                        currentStringLine.get(28).equals("ÿ")
                                ? Gender.MALE : Gender.FEMALE);
                staff.setAge(hexStringToInt(currentHexLine.get(32)));
                staff.setSkill(hexStringToInt(currentHexLine.get(67)));
                staff.setBehaviour(hexStringToInt(currentHexLine.get(71)));
                switch (hexStringToInt(currentHexLine.get(65))) {
                    case 1:
                        staff.setStaffType(StaffType.OWNER);
                        break;
                    case 2:
                        staff.setStaffType(StaffType.BROADCAST);
                        break;
                    case 3:
                        staff.setStaffType(StaffType.REFEREE);
                        break;
                    case 4:
                        staff.setStaffType(StaffType.PRODUCTION);
                        break;
                    case 5:
                        staff.setStaffType(StaffType.MEDICAL);
                        break;
                    case 6:
                        staff.setStaffType(StaffType.CREATIVE);
                        break;
                    case 7:
                        staff.setStaffType(StaffType.ROAD_AGENT);
                        break;
                    case 8:
                        staff.setStaffType(StaffType.TRAINER);
                        break;
                }

                for (Promotion p : allPromotions) {
                    checkForStaffContract(p, staff, currentHexLine);
                }

                staffViews.add(staff);

                counter = 0;
                currentLine = "";
                currentHexLine = new ArrayList<>();
                currentStringLine = new ArrayList<>();
            }

        }

        gameController.getStaffManager().addStaff(staffViews);
    }

    private void workersDat() throws IOException {

        Path path = Paths.get(importFolder.getPath() + "\\wrestler.dat");
        byte[] data = Files.readAllBytes(path);

        String fileString = DatatypeConverter.printHexBinary(data);
        String currentLine = "";
        List<String> currentHexLine = new ArrayList<>();
        List<String> currentStringLine = new ArrayList<>();
        int counter = 0;
        int lineLength = 307;
        int rosterPositionIndex = 82;

        for (int i = 0; i < fileString.length(); i += 2) {

            //combine the two characters into one string
            String hexValueString = new StringBuilder().append(fileString.charAt(i)).append(fileString.charAt(i + 1)).toString();

            currentLine += hexStringToLetter(hexValueString);
            currentHexLine.add(hexValueString);
            currentStringLine.add(hexStringToLetter(hexValueString));

            counter++;

            if (counter == lineLength) {

                Worker worker = PersonFactory.randomWorker();

                worker.setName(currentLine.substring(3, 27).trim());
                worker.setShortName(currentLine.substring(28, 38).trim());
                worker.setImageString(currentLine.substring(45, 65).trim());
                worker.setFlying(hexStringToInt(currentHexLine.get(151)));
                worker.setStriking(hexStringToInt(currentHexLine.get(147)));
                worker.setWrestling(hexStringToInt(currentHexLine.get(149)));
                worker.setPopularity(hexStringToInt(currentHexLine.get(157)));
                worker.setCharisma(hexStringToInt(currentHexLine.get(159)));
                worker.setBehaviour(hexStringToInt(currentHexLine.get(255)));
                worker.setAge(hexStringToInt(currentHexLine.get(42)));
                worker.setGender(
                        currentStringLine.get(293).equals("ÿ")
                                ? Gender.FEMALE : Gender.MALE);

                boolean fullTime;
                boolean mainRoster;

                switch (currentHexLine.get(rosterPositionIndex)) {
                    case "07":
                        //development
                        fullTime = true;
                        mainRoster = false;
                        break;
                    case "19":
                        //non-wrestler
                        fullTime = false;
                        mainRoster = true;
                        break;
                    default:
                        //shouldn't happen
                        fullTime = true;
                        mainRoster = true;
                        break;
                }

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
                    checkForWorkerContract(p, worker, currentHexLine, currentLine);
                }

                allWorkers.add(worker);
                workerIDs.add(currentHexLine.get(1) + currentHexLine.get(2));
                managerIDs.add(currentHexLine.get(121) + currentHexLine.get(122));
                counter = 0;
                currentLine = "";
                currentHexLine = new ArrayList<>();
                currentStringLine = new ArrayList<>();

            }
        }
        allWorkers = gameController.getWorkerManager().createWorkers(allWorkers);
    }

    private void relateDat() throws IOException {
        Path path = Paths.get(importFolder.getPath() + "\\relate.dat");
        byte[] data = Files.readAllBytes(path);

        String fileString = DatatypeConverter.printHexBinary(data);
        List<String> currentHexLine = new ArrayList<>();
        int counter = 0;
        int lineLength = 37;

        for (int i = 0; i < fileString.length(); i += 2) {

            String hexValueString = new StringBuilder().append(fileString.charAt(i)).append(fileString.charAt(i + 1)).toString();

            currentHexLine.add(hexValueString);

            counter++;

            if (counter == lineLength) {

                String id1 = currentHexLine.get(31) + currentHexLine.get(32);
                String id2 = currentHexLine.get(33) + currentHexLine.get(34);

                Worker worker1 = null;
                Worker worker2 = null;

                for (int x = 0; x < allWorkers.size(); x++) {

                    if (workerIDs.get(x).equals(id1)) {
                        worker1 = allWorkers.get(x);
                    } else if (workerIDs.get(x).equals(id2)) {
                        worker2 = allWorkers.get(x);
                    }
                    if (worker1 != null && worker2 != null) {
                        int level;
                        switch (hexStringToInt(currentHexLine.get(35))) {
                            case 0:
                                level = MAX_RELATIONSHIP_LEVEL;
                                break;
                            case 1:
                                level = MAX_RELATIONSHIP_LEVEL;
                                break;
                            case 2:
                                level = MIN_RELATIONSHIP_LEVEL;
                                break;
                            case 3:
                                level = MIN_RELATIONSHIP_LEVEL + 50;
                                break;
                            case 4:
                                level = MAX_RELATIONSHIP_LEVEL - 50;
                                break;
                            case 5:
                                level = MAX_RELATIONSHIP_LEVEL;
                                break;
                            default:
                                level = DEFAULT_RELATIONSHIP_LEVEL;
                                break;
                        }
                        gameController.getRelationshipManager().setRelationshipLevel(worker1, worker2, level);
                        break;
                    }
                }

                counter = 0;
                currentHexLine = new ArrayList<>();
            }
        }
    }

    private void setManagers() {
        for (int i = 0; i < workerIDs.size(); i++) {
            if (workerIDs.indexOf(managerIDs.get(i)) > -1) {
                allWorkers.get(i).setManager(allWorkers.get(workerIDs.indexOf(managerIDs.get(i))));
            }
        }
    }

    private void checkForWorkerContract(Promotion promotion, Worker worker, List<String> currentHexLine, String currentLine) {
        boolean exclusive = hexStringToLetter(currentHexLine.get(71)).equals("W");

        int a = hexStringToInt(currentHexLine.get(65));
        int b = hexStringToInt(currentHexLine.get(65));
        int c = hexStringToInt(currentHexLine.get(65));
        String rosterSplitName = "";

        if (promotion.indexNumber() == a) {
            rosterSplitName = currentLine.substring(91, 100);
        } else if (promotion.indexNumber() == b) {
            rosterSplitName = currentLine.substring(101, 110);
        } else if (promotion.indexNumber() == c) {
            rosterSplitName = currentLine.substring(111, 120);
        } else {
            return;
        }
        checkForRosterSplit(promotion, worker, rosterSplitName);

        contractsToAdd.add(Contract.builder()
                .worker(worker)
                .promotion(promotion)
                .exclusive(exclusive)
                .active(true)
                .startDate(getGameController().getDateManager().today())
                .endDate(ContractUtils.contractEndDate(getGameController().getDateManager().today(), RandomUtils.nextInt(0, 12)))
                .build());

    }

    private void checkForRosterSplit(Promotion promotion, Worker worker, String groupName) {
        final String trimmedName = groupName.trim();
        if (trimmedName.equalsIgnoreCase("NONE")) {
            return;
        }
        RosterSplit existingGroup = rosterSplitsToAdd.stream()
                .filter(group -> group.getOwner().equals(promotion) && group.getName().equals(trimmedName))
                .findFirst().orElse(null);

        if (existingGroup != null) {
            existingGroup.getWorkers().add(worker);
        } else {
            RosterSplit newGroup = new RosterSplit();
            newGroup.setName(trimmedName);
            newGroup.setOwner(promotion);
            newGroup.getWorkers().add(worker);
            rosterSplitsToAdd.add(newGroup);
        }
    }

    private void checkForStaffContract(Promotion p, StaffView s, List<String> currentHexLine) {
        if (p.indexNumber() == (hexStringToInt(currentHexLine.get(54)))) {
            getGameController().getContractFactory().createContract(s, p, getGameController().getDateManager().today());
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
        int lineLength = 47;
        int promotionKeyIndex = 34;
        int monthValueIndex = 36;

        for (int i = 0; i < fileString.length(); i += 2) {

            String hexValueString = new StringBuilder().append(fileString.charAt(i)).append(fileString.charAt(i + 1)).toString();
            currentLine += hexStringToLetter(hexValueString);
            counter++;

            if (counter == promotionKeyIndex) {
                promotion = getPromotionFromKey(hexStringToInt(hexValueString));
            }

            if (counter == monthValueIndex) {
                int monthInt = hexStringToInt(hexValueString);
                if (monthInt < 1 || monthInt > 12) {
                    logger.log(Level.WARN, String.format("Invalid month of %d for %s", monthInt, currentLine.substring(1, 32).trim()));
                    month = Month.of(1);
                } else {
                    month = Month.of(monthInt);
                }

            }

            if (counter == lineLength) {
                EventTemplate eventTemplate = new EventTemplate();
                eventTemplate.setName(currentLine.substring(1, 32).trim());
                eventTemplate.setPromotion(promotion);
                eventTemplate.setMonth(month.getValue());
                eventTemplate.setEventBroadcast(EventBroadcast.NONE);
                eventTemplate.setEventFrequency(EventFrequency.ANNUAL);
                assignRosterSplit(eventTemplate, eventTemplate.getPromotion(), eventTemplate.getName());
                eventTemplates.add(eventTemplate);
                currentLine = "";
                counter = 0;

            }
        }
    }

    private void assignRosterSplit(iRosterSplit item, Promotion promotion, String name) {
        for (RosterSplit rosterSplit : gameController.getRosterSplitManager().getRosterSplits()) {
            if (rosterSplit.getOwner().equals(promotion)
                    && name.toLowerCase().contains(rosterSplit.getName().toLowerCase())) {
                item.setRosterSplit(rosterSplit);
                break;
            }
        }
    }

    private void beltDat() throws IOException {
        Path path = Paths.get(importFolder.getPath() + "\\belt.dat");
        byte[] data = Files.readAllBytes(path);

        String workerId = new String();
        String workerId2 = new String();
        String titleName = new String();

        List<String> titleNames = new ArrayList<>();
        List<String> beltWorkerIDs = new ArrayList<>();
        List<String> beltWorkerIDs2 = new ArrayList<>();
        List<Integer> titlePromotionKeys = new ArrayList<>();
        List<Integer> titlePrestigeInts = new ArrayList<>();
        String fileString = DatatypeConverter.printHexBinary(data);
        String currentLine = "";

        int counter = 0;
        int lineLength = 457;
        int promotionKeyIndex = 33;
        int prestigeIndex = 43;
        int worker1IdIndex1 = 35;
        int worker1IdIndex2 = 36;
        int worker2IdIndex1 = 37;
        int worker2IdIndex2 = 38;

        for (int i = 0; i < fileString.length(); i += 2) {

            //combine the two characters into one string
            String hexValueString = new StringBuilder().append(fileString.charAt(i)).append(fileString.charAt(i + 1)).toString();

            currentLine += hexStringToLetter(hexValueString);

            if (counter == promotionKeyIndex) {
                titlePromotionKeys.add(hexStringToInt(hexValueString));
            } else if (counter == worker1IdIndex1 || counter == worker1IdIndex2) {
                workerId += hexValueString;
            } else if (counter == worker2IdIndex1 || counter == worker2IdIndex2) {
                workerId2 += hexValueString;
            } else if (counter == prestigeIndex) {
                titlePrestigeInts.add(hexStringToInt(hexValueString));
            }

            counter++;

            if (counter == lineLength) {

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
                            TitleView titleView = getGameController().getTitleFactory().createTitle(allPromotions.get(p), titleHolders, titleNames.get(t));
                            titleView.getTitle().setPrestige(titlePrestigeInts.get(t));
                            assignRosterSplit(titleView, titleView.getTitle().getPromotion(), titleView.getLongName());
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
            logger.log(Level.ERROR, "Advanced Import file not found at " + path);
            logger.log(Level.ERROR, "Proceding without advanced import");
            advancedImportData = new ArrayList();
        } catch (IOException ex) {
            logger.log(Level.ERROR, "Advanced Import file read error");
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
