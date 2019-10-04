package openwrestling.file;

import lombok.Getter;
import openwrestling.model.EventTemplate;
import openwrestling.model.controller.GameController;
import openwrestling.model.factory.PersonFactory;
import openwrestling.model.gameObjects.Contract;
import openwrestling.model.gameObjects.Promotion;
import openwrestling.model.gameObjects.RosterSplit;
import openwrestling.model.gameObjects.Stable;
import openwrestling.model.gameObjects.StaffContract;
import openwrestling.model.gameObjects.StaffMember;
import openwrestling.model.gameObjects.TagTeam;
import openwrestling.model.gameObjects.Title;
import openwrestling.model.gameObjects.TitleReign;
import openwrestling.model.gameObjects.Worker;
import openwrestling.model.interfaces.iRosterSplit;
import openwrestling.model.segmentEnum.ActiveType;
import openwrestling.model.segmentEnum.EventBroadcast;
import openwrestling.model.segmentEnum.EventFrequency;
import openwrestling.model.segmentEnum.EventRecurrence;
import openwrestling.model.segmentEnum.Gender;
import openwrestling.model.segmentEnum.StaffType;
import openwrestling.model.utility.ContractUtils;
import org.apache.commons.lang3.ArrayUtils;
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
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static openwrestling.file.ImportUtils.*;
import static openwrestling.model.constants.GameConstants.*;

public class Import {

    private GameController gameController;

    private final transient Logger logger = LogManager.getLogger(getClass());

    private File importFolder;

    private final List<Promotion> allPromotions = new ArrayList<>();
    @Getter
    private final List<Integer> promotionKeys = new ArrayList<>();
    private final List<String> workerIDs = new ArrayList<>();

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

                List<Promotion> promotions = promotionsDat(importFolder, "promos");
                promotions = gameController.getPromotionManager().createPromotions(promotions);
                List<Worker> workers = workersDat(importFolder);
                workers = gameController.getWorkerManager().createWorkers(workers);
                //TODO set managers
                List<RosterSplit> rosterSplits = rosterSplits(importFolder, "promos", promotions);
                rosterSplits = gameController.getRosterSplitManager().createRosterSplits(rosterSplits);
                List<Contract> contracts = contracts(importFolder, workers, promotions, gameController.getDateManager().today());
                contracts = gameController.getContractManager().createContracts(contracts);
                List<TagTeam> tagTeams = teamsDat(importFolder, workers);
                tagTeams = gameController.getTagTeamManager().createTagTeams(tagTeams);
                List<Stable> stables = stablesDat(importFolder, workers, promotions);
                stables = gameController.getStableManager().createStables(stables);
                List<Title> titles = beltDat(importFolder, promotions, workers, gameController.getDateManager().today(), rosterSplits);
                titles = gameController.getTitleManager().createTitles(titles);
                List<EventTemplate> tvTemplates = tvDat(importFolder, promotions, rosterSplits);
                tvTemplates = gameController.getEventManager().createEventTemplates(tvTemplates);
                List<EventTemplate> eventTemplates = eventDat(importFolder, rosterSplits, promotions);
                eventTemplates = gameController.getEventManager().createEventTemplates(eventTemplates);
                List<StaffMember> staffMembers = staffDat(importFolder);
                staffMembers = gameController.getStaffManager().creatStaffMembers(staffMembers);
                List<StaffContract> staffContracts = staffContracts(importFolder, promotions, staffMembers, gameController.getDateManager().today());
                staffContracts = gameController.getContractManager().createStaffContracts(staffContracts);
//                staffDat();
//                relateDat();
            } catch (Exception ex) {

                sb.append(ex);
                logger.log(Level.ERROR, ex);
                throw ex;
            }
//            processOther();

            gameController.getPromotionManager().createPromotions(allPromotions);
            gameController.getEventManager().addEventTemplates(eventTemplates);

            //for statistical evaluation of data only
            /* boolean evaluate = false;
            if (evaluate) {
                EvaluateData.evaluateData(allPromotions, allWorkers);
            }*/
        }

        return sb.toString();

    }

    private boolean workersMatch(Worker worker1, Worker worker2) {
        return Objects.equals(worker1.getName(), worker2.getName()) &&
                Objects.equals(worker1.getShortName(), worker2.getShortName()) &&
                Objects.equals(worker1.getAge(), worker2.getAge()) &&
                Objects.equals(worker1.getStriking(), worker2.getStriking());
    }


//
//    private void processOther() {
//        otherPromotionNames.stream().map((s) -> {
//            Promotion p = gameController.getPromotionFactory().newPromotion();
//            p.setName(s);
//            p.setShortName(s);
//            return p;
//        }).map((p) -> {
//            //calculate promotion level
//            int totalPop = 0;
//            int totalWorkers = 0;
//            for (int i = 0; i < otherWorkers.size(); i++) {
//                if (otherWorkerPromotions.get(i).equals(p.getName())) {
//                    totalPop += otherWorkers.get(i).getPopularity();
//                    totalWorkers++;
//                }
//            }
//            int avgPop = totalPop / totalWorkers;
//            p.setLevel((int) ((avgPop - (avgPop % 20)) / 20) + 1);
//            p.setPromotionID(allPromotions.size());
//            allPromotions.add(p);
//            return p;
//        }).forEach((promotion) -> {
//            for (int i = 0; i < otherWorkers.size(); i++) {
//                if (otherWorkerPromotions.get(i).equals(promotion.getName())) {
//                    contractsToAdd.add(Contract.builder()
//                            .worker(otherWorkers.get(i))
//                            .promotion(promotion)
//                            .exclusive(false)
//                            .active(true)
//                            .startDate(getGameController().getDateManager().today())
//                            .endDate(ContractUtils.contractEndDate(getGameController().getDateManager().today(), RandomUtils.nextInt(0, 12)))
//                            .build());
//                }
//            }
//        });
//
//        updateOtherPromotions(allPromotions, importFolder);
//    }

    List<EventTemplate> tvDat(File importFolder, List<Promotion> promotions, List<RosterSplit> rosterSplits) {
        List<EventTemplate> eventTemplates = new ArrayList<>();
        List<List<String>> hexLines = getHexLines(importFolder, "tv", 51);

        int promotionKeyIndex = 21;

        hexLines.forEach(hexLine -> {
            String textLine = hexLineToTextString(hexLine);

            EventTemplate eventTemplate = new EventTemplate();
            eventTemplate.setEventBroadcast(EventBroadcast.TELEVISION);
            eventTemplate.setEventFrequency(EventFrequency.WEEKLY);
            eventTemplate.setEventRecurrence(EventRecurrence.LIMITED);
            eventTemplate.setEventsLeft(RandomUtils.nextInt(30, 60));
            eventTemplate.setName(textLine.substring(1, 21).trim());

            int duration = 0;
            switch (hexStringToLetter(hexLine.get(32))) {
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
                    textLine.substring(22, 32).toUpperCase().trim()));

            int key = hexStringToInt(hexLine.get(promotionKeyIndex));
            Promotion promotion = promotions.stream().filter(promo -> promo.getImportKey() == key).findFirst().orElse(null);
            eventTemplate.setPromotion(promotion);

            assignRosterSplit(eventTemplate, promotion, rosterSplits);

            eventTemplates.add(eventTemplate);
        });
        return eventTemplates;
    }

    List<EventTemplate> eventDat(File importFolder, List<RosterSplit> rosterSplits, List<Promotion> promotions) {
        List<EventTemplate> eventTemplates = new ArrayList<>();
        List<List<String>> hexLines = getHexLines(importFolder, "event", 47);

        hexLines.forEach(hexLine -> {
            String textLine = hexLineToTextString(hexLine);
            EventTemplate eventTemplate = new EventTemplate();

            Month month;
            int monthInt = hexStringToInt(hexLine.get(35));
            if (monthInt < 1 || monthInt > 12) {
                logger.log(Level.WARN, String.format("Invalid month of %d for %s", monthInt, textLine.substring(1, 31).trim()));
                month = Month.of(1);
            } else {
                month = Month.of(monthInt);
            }
            eventTemplate.setMonth(month.getValue());

            eventTemplate.setName(textLine.substring(1, 32).trim());
            eventTemplate.setPromotion(promotions.stream().filter(promotion -> promotion.getImportKey() == hexStringToInt(hexLine.get(33))).findFirst().orElse(null));
            eventTemplate.setEventBroadcast(EventBroadcast.NONE);
            eventTemplate.setEventFrequency(EventFrequency.ANNUAL);
            assignRosterSplit(eventTemplate, eventTemplate.getPromotion(), rosterSplits);
            eventTemplates.add(eventTemplate);
        });
        return eventTemplates;
    }

    List<RosterSplit> rosterSplits(File importFolder, String fileName, List<Promotion> promotions) {
        List<RosterSplit> rosterSplits = new ArrayList<>();
        List<List<String>> hexLines = getHexLines(importFolder, fileName, 397);

        hexLines.forEach(hexLine -> {
            String textLine = hexLineToTextString(hexLine);
            List<String> rosterSplitNames = List.of(
                    textLine.substring(271, 281),
                    textLine.substring(281, 291),
                    textLine.substring(291, 301),
                    textLine.substring(301, 311)
            );
            rosterSplitNames.forEach(name -> {
                if (!"None".equals(name.trim())) {
                    Promotion promotion = promotions.stream()
                            .filter(promotion1 -> promotion1.getImportKey() == hexStringToInt(hexLine.get(1)))
                            .findFirst()
                            .orElse(null);
                    rosterSplits.add(
                            RosterSplit.builder()
                                    .name(name.trim())
                                    .owner(promotion)
                                    .build()
                    );
                }
            });
        });
        return rosterSplits;
    }

    List<Promotion> promotionsDat(File importFolder, String fileName) {
        List<Promotion> promotions = new ArrayList<>();
        List<List<String>> hexLines = getHexLines(importFolder, fileName, 397);

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

            promotions.add(promotion);
        });
        return promotions;
    }

    List<TagTeam> teamsDat(File importFolder, List<Worker> workers) {
        List<TagTeam> tagTeams = new ArrayList<>();
        List<List<String>> hexLines = getHexLines(importFolder, "teams", 59);

        hexLines.forEach(hexLine -> {
            String textLine = hexLineToTextString(hexLine);

            TagTeam tagTeam = new TagTeam();
            int id1 = hexStringToInt(hexLine.get(26) + hexLine.get(27));
            int id2 = hexStringToInt(hexLine.get(28) + hexLine.get(29));

            tagTeam.setName(textLine.substring(1, 18).trim());
            workers.forEach(worker -> {
                if (worker.getImportKey() == id1 ||
                        worker.getImportKey() == id2) {
                    tagTeam.addWorker(worker);
                }
            });

            tagTeam.setExperience(hexStringToInt(hexLine.get(55)));
            tagTeam.setActiveType(hexLine.get(57).equals("FF")
                    ? ActiveType.ACTIVE : ActiveType.INACTIVE);

            tagTeams.add(tagTeam);


        });
        return tagTeams;
    }

    List<Stable> stablesDat(File importFolder, List<Worker> workers, List<Promotion> promotions) {
        List<Stable> stables = new ArrayList<>();
        List<List<String>> hexLines = getHexLines(importFolder, "stables", 70);

        hexLines.forEach(hexLine -> {
            String textLine = hexLineToTextString(hexLine);
            Stable stable = new Stable();
            stable.setName(textLine.substring(1, 24).trim());
            stable.setOwner(
                    promotions.stream()
                            .filter(promotion -> promotion.getImportKey() == hexStringToInt(hexLine.get(26)))
                            .findFirst()
                            .orElse(null)
            );

            for (int f = 28; f < hexLine.size() - 1; f += 2) {
                int id = hexStringToInt(hexLine.get(f) + hexLine.get(f + 1));
                Optional<Worker> worker = workers.stream().filter(worker1 -> worker1.getImportKey() == id).findFirst();
                worker.ifPresent(worker1 -> {
                    stable.getWorkers().add(worker1);
                });
            }

            stables.add(stable);

        });
        return stables;
    }

    List<StaffMember> staffDat(File importFolder) {
        List<StaffMember> staffMembers = new ArrayList<>();
        List<List<String>> hexLines = getHexLines(importFolder, "staff", 79);

        hexLines.forEach(hexLine -> {
            String textLine = hexLineToTextString(hexLine);
            StaffMember staff = new StaffMember();
            staff.setName(textLine.substring(3, 27).trim());
            staff.setImageString(textLine.substring(34, 53).trim());
            staff.setGender(
                    textLine.charAt(28) == 'ÿ'
                            ? Gender.MALE : Gender.FEMALE);
            staff.setAge(hexStringToInt(hexLine.get(32)));
            staff.setSkill(hexStringToInt(hexLine.get(67)));
            staff.setBehaviour(hexStringToInt(hexLine.get(71)));
            switch (hexStringToInt(hexLine.get(65))) {
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
            staff.setImportKey(hexStringToInt(hexLine.get(1) + hexLine.get(2)));

            staffMembers.add(staff);
        });
        return staffMembers;
    }


    List<StaffContract> staffContracts(File importFolder, List<Promotion> promotions, List<StaffMember> staffMembers, LocalDate startDate) {
        List<StaffContract> staffContracts = new ArrayList<>();
        List<List<String>> hexLines = getHexLines(importFolder, "staff", 79);

        hexLines.forEach(hexLine -> {
            StaffMember staffMember = staffMembers.stream()
                    .filter(staff -> staff.getImportKey() == hexStringToInt(hexLine.get(1) + hexLine.get(2)))
                    .findFirst()
                    .orElse(null);
            Optional<Promotion> promotion = promotions.stream().filter(promo -> promo.getImportKey() == hexStringToInt(hexLine.get(54))).findFirst();
            promotion.ifPresent(promo -> {
                StaffContract staffContract = StaffContract.builder()
                        .staff(staffMember)
                        .promotion(promo)
                        .active(true)
                        .biWeeklyCost(ContractUtils.calculateStaffContractCost(staffMember))
                        .startDate(startDate)
                        .endDate(ContractUtils.contractEndDate(startDate, 12))
                        .build();
                staffContracts.add(staffContract);
            });
        });
        return staffContracts;
    }

    List<Worker> workersDat(File importFolder) {
        List<Worker> workers = new ArrayList<>();
        int rosterPositionIndex = 82;

        List<List<String>> hexLines = getHexLines(importFolder, "wrestler", 307);

        hexLines.forEach(hexLine -> {
            Worker worker = PersonFactory.randomWorker();

            String currentLine = hexLineToTextString(hexLine);

            workerIDs.add(hexLine.get(1) + hexLine.get(2));
            worker.setImportKey(hexStringToInt(hexLine.get(1) + hexLine.get(2)));

            worker.setName(currentLine.substring(3, 27).trim());
            worker.setShortName(currentLine.substring(28, 38).trim());
            worker.setImageString(currentLine.substring(45, 65).trim());
            worker.setFlying(hexStringToInt(hexLine.get(151)));
            worker.setStriking(hexStringToInt(hexLine.get(147)));
            worker.setWrestling(hexStringToInt(hexLine.get(149)));
            worker.setPopularity(hexStringToInt(hexLine.get(157)));
            worker.setCharisma(hexStringToInt(hexLine.get(159)));
            worker.setBehaviour(hexStringToInt(hexLine.get(255)));
            worker.setAge(hexStringToInt(hexLine.get(42)));
            worker.setGender(
                    currentLine.charAt(293) == 'ÿ'
                            ? Gender.FEMALE : Gender.MALE);

            boolean fullTime;
            boolean mainRoster;

            switch (hexLine.get(rosterPositionIndex)) {
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

            workers.add(worker);
        });
        return workers;
    }

    List<Contract> contracts(File importFolder, List<Worker> workers, List<Promotion> promotions, LocalDate gameStartDate) {
        List<Contract> contracts = new ArrayList<>();

        List<List<String>> hexLines = getHexLines(importFolder, "wrestler", 307);

        hexLines.forEach(hexLine -> {
            Worker worker = workers.stream()
                    .filter(worker1 -> worker1.getImportKey() == hexStringToInt(hexLine.get(1) + hexLine.get(2)))
                    .findFirst()
                    .orElse(null);
            int a = hexStringToInt(hexLine.get(65));
            int b = hexStringToInt(hexLine.get(67));
            int c = hexStringToInt(hexLine.get(69));
            int[] promoKeys = new int[]{a, b, c};
            boolean exclusive = hexStringToLetter(hexLine.get(71)).equals("W");
            promotions.stream()
                    .filter(promotion -> ArrayUtils.contains(promoKeys, promotion.getImportKey()))
                    .forEach(promotion -> {
                        contracts.add(Contract.builder()
                                .worker(worker)
                                .promotion(promotion)
                                .exclusive(exclusive)
                                .active(true)
                                .startDate(gameStartDate)
                                .endDate(ContractUtils.contractEndDate(gameStartDate, RandomUtils.nextInt(0, 12)))
                                .build());
                    });
        });
        return contracts;
    }


    private void relateDat(List<Worker> workers) throws IOException {
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

                for (int x = 0; x < workers.size(); x++) {

                    if (workerIDs.get(x).equals(id1)) {
                        worker1 = workers.get(x);
                    } else if (workerIDs.get(x).equals(id2)) {
                        worker2 = workers.get(x);
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

//    private void setManagers(List<Worker>workers) {
//        for (int i = 0; i < workerIDs.size(); i++) {
//            if (workerIDs.indexOf(managerIDs.get(i)) > -1) {
//                workers.get(i).setManager(workers.get(workerIDs.indexOf(managerIDs.get(i))));
//            }
//        }
//    }

    private void assignRosterSplit(iRosterSplit item, Promotion promotion, List<RosterSplit> rosterSplits) {
        RosterSplit rosterSplit = rosterSplits.stream().filter(rs ->
                rs.getOwner().getPromotionID() == promotion.getPromotionID() &&
                        item.toString().contains(rs.getName())
        ).findFirst().orElse(null);
        item.setRosterSplit(rosterSplit);
    }

    List<Title> beltDat(File importFolder, List<Promotion> promotions, List<Worker> workers, LocalDate dayWon, List<RosterSplit> rosterSplits) {
        //TODO set roster splits
        List<Title> titles = new ArrayList<>();
        List<List<String>> hexLines = getHexLines(importFolder, "belt", 457);

        hexLines.forEach(hexline -> {
            String textLine = hexLineToTextString(hexline);
            int workerID1 = hexStringToInt(hexline.get(35) + hexline.get(36));
            int workerID2 = hexStringToInt(hexline.get(37) + hexline.get(38));
            int promotionKey = hexStringToInt(hexline.get(33));
            List<Worker> champions = workers.stream()
                    .filter(worker -> worker.getImportKey() == workerID1 || worker.getImportKey() == workerID2)
                    .collect(Collectors.toList());
            Promotion promotion = promotions.stream()
                    .filter(p -> p.getImportKey() == promotionKey)
                    .findFirst()
                    .orElse(null);
            Title title = Title.builder()
                    .name(textLine.substring(1, 31).trim())
                    .promotion(promotion)
                    .prestige(hexStringToInt(hexline.get(43)))
                    .build();

            if (!champions.isEmpty()) {
                TitleReign titleReign = TitleReign.builder()
                        .workers(champions)
                        .dayWon(dayWon)
                        .sequenceNumber(1)
                        .build();
                title.setChampionTitleReign(titleReign);
            }

            assignRosterSplit(title, promotion, rosterSplits);

            titles.add(title);
        });
        return titles;
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
