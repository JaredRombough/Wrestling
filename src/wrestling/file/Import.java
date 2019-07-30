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
import org.apache.commons.lang3.RandomUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import wrestling.model.EventTemplate;
import wrestling.model.TagTeam;
import wrestling.model.TagTeamWorker;
import wrestling.model.controller.GameController;
import wrestling.model.factory.PersonFactory;
import wrestling.model.interfaces.iRosterSplit;
import wrestling.model.modelView.PromotionView;
import wrestling.model.modelView.WorkerGroup;
import wrestling.model.modelView.StaffView;
import wrestling.model.modelView.TagTeamView;
import wrestling.model.modelView.TitleView;
import wrestling.model.modelView.WorkerView;
import wrestling.model.segmentEnum.ActiveType;
import wrestling.model.segmentEnum.EventBroadcast;
import wrestling.model.segmentEnum.EventFrequency;
import wrestling.model.segmentEnum.EventRecurrence;
import wrestling.model.segmentEnum.Gender;
import wrestling.model.segmentEnum.StaffType;

public class Import {

    private GameController gameController;

    private final transient Logger logger = LogManager.getLogger(getClass());

    private File importFolder;

    private final List<PromotionView> allPromotions = new ArrayList<>();
    private final List<Integer> promotionKeys = new ArrayList<>();
    private final List<String> otherPromotionNames = new ArrayList<>();

    private final List<WorkerView> allWorkers = new ArrayList<>();
    private final List<String> workerIDs = new ArrayList<>();
    private final List<String> managerIDs = new ArrayList<>();
    private final List<WorkerView> otherWorkers = new ArrayList<>();
    private final List<String> otherWorkerPromotions = new ArrayList<>();

    private final List<TagTeam> allTagTeams = new ArrayList<>();
    private final List<TagTeamView> allTagTeamViews = new ArrayList<>();

    private final List<EventTemplate> eventTemplates = new ArrayList<>();

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
            gameController.getPromotionManager().addPromotions(allPromotions);
            gameController.getEventManager().addEventTemplates(eventTemplates);
            gameController.getWorkerManager().addWorkers(allWorkers);
            gameController.getTagTeamManager().addTagTeams(allTagTeams);
            gameController.getTagTeamManager().addTagTeamViews(allTagTeamViews);

            //for statistical evaluation of data only
            /* boolean evaluate = false;
            if (evaluate) {
                EvaluateData.evaluateData(allPromotions, allWorkers);
            }*/
        }

        return sb.toString();

    }

    private PromotionView getPromotionFromKey(int key) {
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
            PromotionView p = gameController.getPromotionFactory().newPromotion();
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
                        duration = 60;
                        break;
                    case "E":
                        duration = 60;
                        break;
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

    private void promotionsDat() throws IOException {

        Path path = Paths.get(importFolder.getPath() + "\\promos.dat");
        byte[] data = Files.readAllBytes(path);

        String fileString = DatatypeConverter.printHexBinary(data);
        String currentLine = "";
        int counter = 0;
        int level = 0;
        int lineLength = 397;
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

            if (counter == lineLength) {

                PromotionView promotion = gameController.getPromotionFactory().newPromotion();

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
        int lineLength = 59;

        for (int i = 0; i < fileString.length(); i += 2) {

            //combine the two characters into one string
            String hexValueString = new StringBuilder().append(fileString.charAt(i)).append(fileString.charAt(i + 1)).toString();

            currentLine += hexStringToLetter(hexValueString);
            currentHexLine.add(hexValueString);
            currentStringLine.add(hexStringToLetter(hexValueString));

            counter++;

            if (counter == lineLength) {

                TagTeam team = new TagTeam();
                TagTeamView tagTeamView = new TagTeamView();
                String id1 = currentHexLine.get(26) + currentHexLine.get(27);
                String id2 = currentHexLine.get(28) + currentHexLine.get(29);

                team.setName(currentLine.substring(1, 18).trim());
                for (int x = 0; x < allWorkers.size(); x++) {

                    if (workerIDs.get(x).equals(id1)
                            || workerIDs.get(x).equals(id2)) {
                        TagTeamWorker tagTeamWorker = new TagTeamWorker(team, allWorkers.get(x));
                        gameController.getTagTeamManager().addTagTeamWorker(tagTeamWorker);
                        tagTeamView.addWorker(tagTeamWorker.getWorker());
                    }
                }
                team.setExperience(hexStringToInt(currentHexLine.get(55)));
                team.setActiveType(currentHexLine.get(57).equals("FF")
                        ? ActiveType.ACTIVE : ActiveType.INACTIVE);

                tagTeamView.setTagTeam(team);

                allTagTeamViews.add(tagTeamView);
                allTagTeams.add(team);

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
                WorkerGroup stable = new WorkerGroup();
                stable.setName(currentLine.substring(1, 24).trim());
                stable.setOwner(getPromotionFromKey(hexStringToInt(currentHexLine.get(26))));

                for (int f = 28; f < currentHexLine.size() - 1; f += 2) {
                    String id1 = currentHexLine.get(f) + currentHexLine.get(f + 1);
                    if (workerIDs.indexOf(id1) > -1
                            && !stable.getWorkers().contains(allWorkers.get(workerIDs.indexOf(id1)))) {
                        stable.getWorkers().add(allWorkers.get(workerIDs.indexOf(id1)));
                    }
                }

                gameController.getStableManager().addStable(stable);

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

                for (PromotionView p : allPromotions) {
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

                WorkerView worker = PersonFactory.randomWorker();

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
                for (PromotionView p : allPromotions) {
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

                WorkerView worker1 = null;
                WorkerView worker2 = null;;

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
                                level = 200;
                                break;
                            case 1:
                                level = 200;
                                break;
                            case 2:
                                level = 0;
                                break;
                            case 3:
                                level = 50;
                                break;
                            case 4:
                                level = 150;
                                break;
                            case 5:
                                level = 200;
                                break;
                            default:
                                level = 100;
                                break;
                        }
                        gameController.getRelationshipManager().setRelationshipLevel(worker2, worker2, level);
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

    private void checkForWorkerContract(PromotionView promotion, WorkerView worker, List<String> currentHexLine, String currentLine) {
        boolean exclusive = hexStringToLetter(currentHexLine.get(71)).equals("W");

        int a = hexStringToInt(currentHexLine.get(65));
        int b = hexStringToInt(currentHexLine.get(65));
        int c = hexStringToInt(currentHexLine.get(65));
        String groupName = "";

        if (promotion.indexNumber() == a) {
            groupName = currentLine.substring(91, 100);
        } else if (promotion.indexNumber() == b) {
            groupName = currentLine.substring(101, 110);
        } else if (promotion.indexNumber() == c) {
            groupName = currentLine.substring(111, 120);
        } else {
            return;
        }
        checkForRosterSplit(promotion, worker, groupName);
        getGameController().getContractFactory().createContract(worker, promotion, getGameController().getDateManager().today(), exclusive);

    }

    private void checkForRosterSplit(PromotionView promotion, WorkerView worker, String groupName) {
        final String trimmedName = groupName.trim();
        if (trimmedName.equalsIgnoreCase("NONE")) {
            return;
        }
        WorkerGroup existingGroup = gameController.getStableManager().getRosterSplits().stream()
                .filter(group -> group.getOwner().equals(promotion) && group.getName().equals(trimmedName))
                .findFirst().orElse(null);

        if (existingGroup != null) {
            existingGroup.getWorkers().add(worker);
        } else {
            WorkerGroup newGroup = new WorkerGroup();
            newGroup.setName(trimmedName);
            newGroup.setOwner(promotion);
            newGroup.getWorkers().add(worker);
            gameController.getStableManager().addRosterSplit(newGroup);
        }
    }

    private void checkForStaffContract(PromotionView p, StaffView s, List<String> currentHexLine) {
        if (p.indexNumber() == (hexStringToInt(currentHexLine.get(54)))) {
            getGameController().getContractFactory().createContract(s, p, getGameController().getDateManager().today());
        }
    }

    private void eventDat() throws IOException {
        Path path = Paths.get(importFolder.getPath() + "\\event.dat");
        byte[] data = Files.readAllBytes(path);

        String fileString = DatatypeConverter.printHexBinary(data);
        String currentLine = "";
        PromotionView promotion = null;
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
                eventTemplate.setMonth(month);
                eventTemplate.setEventBroadcast(EventBroadcast.NONE);
                eventTemplate.setEventFrequency(EventFrequency.ANNUAL);
                assignRosterSplit(eventTemplate, eventTemplate.getPromotion(), eventTemplate.getName());
                eventTemplates.add(eventTemplate);
                currentLine = "";
                counter = 0;

            }
        }
    }

    private void assignRosterSplit(iRosterSplit item, PromotionView promotion, String name) {
        for (WorkerGroup rosterSplit : gameController.getStableManager().getRosterSplits()) {
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
                        List<WorkerView> titleHolders = new ArrayList<>();

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

    public void updateOtherPromotions(List<PromotionView> promotions, File importFolder) {

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

        for (PromotionView promotion : promotions) {
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
