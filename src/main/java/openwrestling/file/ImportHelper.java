package openwrestling.file;

import lombok.AllArgsConstructor;
import openwrestling.model.factory.PersonFactory;
import openwrestling.model.gameObjects.Contract;
import openwrestling.model.gameObjects.EventTemplate;
import openwrestling.model.gameObjects.Promotion;
import openwrestling.model.gameObjects.RosterSplit;
import openwrestling.model.gameObjects.Stable;
import openwrestling.model.gameObjects.StaffContract;
import openwrestling.model.gameObjects.StaffMember;
import openwrestling.model.gameObjects.TagTeam;
import openwrestling.model.gameObjects.Title;
import openwrestling.model.gameObjects.TitleReign;
import openwrestling.model.gameObjects.Worker;
import openwrestling.model.gameObjects.WorkerRelationship;
import openwrestling.model.interfaces.iRosterSplit;
import openwrestling.model.segmentEnum.ActiveType;
import openwrestling.model.segmentEnum.EventBroadcast;
import openwrestling.model.segmentEnum.EventFrequency;
import openwrestling.model.segmentEnum.Gender;
import openwrestling.model.segmentEnum.StaffType;
import openwrestling.model.utility.ContractUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static openwrestling.file.ImportUtils.*;
import static openwrestling.model.constants.GameConstants.*;
import static openwrestling.model.utility.ContractUtils.calculateWorkerContractCost;

@AllArgsConstructor
public class ImportHelper {

    private File importFolder;


    List<EventTemplate> tvDat(List<Promotion> promotions, List<RosterSplit> rosterSplits) {
        List<EventTemplate> eventTemplates = new ArrayList<>();
        List<List<String>> hexLines = getHexLines(importFolder, "tv", 51);

        int promotionKeyIndex = 21;

        hexLines.forEach(hexLine -> {
            String textLine = hexLineToTextString(hexLine);

            EventTemplate eventTemplate = new EventTemplate();
            eventTemplate.setEventBroadcast(EventBroadcast.TELEVISION);
            eventTemplate.setEventFrequency(EventFrequency.WEEKLY);
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

    List<EventTemplate> eventDat(List<RosterSplit> rosterSplits, List<Promotion> promotions) {
        List<EventTemplate> eventTemplates = new ArrayList<>();
        List<List<String>> hexLines = getHexLines(importFolder, "event", 47);

        hexLines.forEach(hexLine -> {
            String textLine = hexLineToTextString(hexLine);
            EventTemplate eventTemplate = new EventTemplate();

            Month month;
            int monthInt = hexStringToInt(hexLine.get(35));
            if (monthInt < 1 || monthInt > 12) {
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

    List<RosterSplit> rosterSplits(String fileName, List<Promotion> promotions) {
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
                                    .workers(new ArrayList<>())
                                    .build()
                    );
                }
            });
        });
        return rosterSplits;
    }

    List<Promotion> promotionsDat(String fileName) {
        List<Promotion> promotions = new ArrayList<>();
        List<List<String>> hexLines = getHexLines(importFolder, fileName, 397);

        hexLines.forEach(hexLine -> {
            Promotion promotion = new Promotion();
            String textLine = hexLineToTextString(hexLine);

            promotion.setImportKey(hexStringToInt(hexLine.get(1)));
            promotion.setName(textLine.substring(3, 43).trim());
            promotion.setShortName(textLine.substring(43, 49).trim());
            promotion.setImagePath(textLine.substring(49, 65).trim());
            promotion.setLevel(6 - hexStringToInt(hexLine.get(89)));

            promotions.add(promotion);
        });
        return promotions;
    }

    List<TagTeam> teamsDat(List<Worker> workers) {
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

    List<Stable> stablesDat(List<Worker> workers, List<Promotion> promotions) {
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

    List<StaffMember> staffDat() {
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


    List<StaffContract> staffContracts(List<Promotion> promotions, List<StaffMember> staffMembers, LocalDate startDate) {
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

    List<Worker> setManagers(List<Worker> workers) {
        List<Worker> updatedWorkers = new ArrayList<>();
        List<List<String>> hexLines = getHexLines(importFolder, "wrestler", 307);
        hexLines.forEach(hexLine -> {
            int workerID = hexStringToInt(hexLine.get(1) + hexLine.get(2));
            int managerID = hexStringToInt(hexLine.get(121) + hexLine.get(122));
            if (managerID != 0) {
                Worker worker = workers.stream().filter(worker1 -> worker1.getImportKey() == workerID).findFirst().orElse(null);
                Worker manager = workers.stream().filter(worker1 -> worker1.getImportKey() == managerID).findFirst().orElse(null);
                if (worker != null && manager != null) {
                    worker.setManager(manager);
                    updatedWorkers.add(worker);
                }
            }
        });

        return updatedWorkers;
    }

    List<Promotion> updateOther(List<Promotion> otherPromotions, List<Contract> otherPromotionContracts) {
        for (Promotion promotion : otherPromotions) {
            List<Worker> otherPromotionWorkers = otherPromotionContracts.stream()
                    .filter(contract -> contract.getPromotion().getName().equals(promotion.getName()))
                    .map(Contract::getWorker).collect(Collectors.toList());
            int totalPop = 0;
            int totalWorkers = otherPromotionWorkers.size();
            for (Worker worker : otherPromotionWorkers) {
                totalPop += worker.getPopularity();
            }
            int avgPop = totalWorkers == 0 ? 0 : totalPop / totalWorkers;
            promotion.setLevel(((avgPop - (avgPop % 20)) / 20) + 1);
        }
        return otherPromotions;
    }

    List<Contract> otherPromotionContracts(List<Promotion> otherPromotions, List<Worker> workers, LocalDate gameStartDate) {
        List<Contract> contracts = new ArrayList<>();
        List<List<String>> hexLines = getHexLines(importFolder, "wrestler", 307);

        hexLines.forEach(hexLine -> {
            String currentLine = hexLineToTextString(hexLine);
            String promotionName = currentLine.substring(76, 78);
            if (StringUtils.isNotBlank(promotionName)) {
                Worker worker = workers.stream()
                        .filter(worker1 -> worker1.getImportKey() == hexStringToInt(hexLine.get(1) + hexLine.get(2)))
                        .findFirst()
                        .orElse(null);
                Promotion promotion = otherPromotions.stream()
                        .filter(promotion1 -> promotion1.getName().equals(promotionName))
                        .findFirst()
                        .orElse(null);

                if (worker != null && promotion != null) {
                    contracts.add(Contract.builder()
                            .worker(worker)
                            .promotion(promotion)
                            .exclusive(false)
                            .active(true)
                            .startDate(gameStartDate)
                            .endDate(ContractUtils.contractEndDate(gameStartDate, RandomUtils.nextInt(0, 12)))
                            .build());
                }
            }
        });

        return contracts;
    }

    List<Promotion> otherPromotions() {
        List<Promotion> promotions = new ArrayList<>();

        List<List<String>> hexLines = getHexLines(importFolder, "wrestler", 307);

        hexLines.forEach(hexLine -> {
            String currentLine = hexLineToTextString(hexLine);
            String promotionName = currentLine.substring(76, 78);
            if (StringUtils.isNotBlank(promotionName) && promotions.stream().noneMatch(promotion -> promotion.getName().equals(promotionName))) {
                Promotion promotion = new Promotion();
                promotion.setName(promotionName);
                promotion.setShortName(promotionName);
                promotions.add(promotion);
            }
        });

        return promotions;
    }

    List<Worker> workersDat() {
        List<Worker> workers = new ArrayList<>();

        List<List<String>> hexLines = getHexLines(importFolder, "wrestler", 307);

        hexLines.forEach(hexLine -> {
            Worker worker = PersonFactory.randomWorker();

            String currentLine = hexLineToTextString(hexLine);

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

            switch (hexLine.get(82)) {
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

    List<Contract> contracts(List<Worker> workers, List<Promotion> promotions, LocalDate gameStartDate) {
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
                        Contract contract = Contract.builder()
                                .worker(worker)
                                .promotion(promotion)
                                .exclusive(exclusive)
                                .active(true)
                                .startDate(gameStartDate)
                                .endDate(ContractUtils.contractEndDate(gameStartDate, RandomUtils.nextInt(0, 12)))
                                .build();
                        int cost = calculateWorkerContractCost(worker, exclusive);

                        if (exclusive) {
                            contract.setMonthlyCost(cost);
                        } else {
                            contract.setAppearanceCost(cost);
                        }

                        contracts.add(contract);
                    });
        });
        return contracts;
    }

    List<RosterSplit> assignWorkersToRosterSplits(List<Worker> workers, List<RosterSplit> rosterSplits) {
        List<List<String>> hexLines = getHexLines(importFolder, "wrestler", 307);

        hexLines.forEach(hexLine -> {
            String currentLine = hexLineToTextString(hexLine);
            List<String> rosterSplitNames = List.of(currentLine.substring(91, 100), currentLine.substring(101, 110), currentLine.substring(111, 120))
                    .stream()
                    .filter(Objects::nonNull)
                    .map(String::trim)
                    .filter(rosterSplitName -> !rosterSplitName.equals("None"))
                    .collect(Collectors.toList());

            rosterSplitNames.forEach(rosterSplitName -> {
                rosterSplits.stream()
                        .filter(rosterSplit -> rosterSplit.getName().equals(rosterSplitName))
                        .findFirst()
                        .ifPresent(split ->
                                workers.stream()
                                        .filter(worker1 -> worker1.getImportKey() == hexStringToInt(hexLine.get(1) + hexLine.get(2)))
                                        .findFirst()
                                        .ifPresent(worker -> split.getWorkers().add(worker))
                        );
            });


        });
        return rosterSplits;
    }


    List<WorkerRelationship> relateDat(List<Worker> workers) {
        List<WorkerRelationship> relationships = new ArrayList<>();
        List<List<String>> hexLines = getHexLines(importFolder, "relate", 37);

        hexLines.forEach(hexLine -> {
            int id1 = hexStringToInt(hexLine.get(31) + hexLine.get(32));
            int id2 = hexStringToInt(hexLine.get(33) + hexLine.get(34));

            List<Worker> relationshipWorkers = workers.stream()
                    .filter(worker -> worker.getImportKey() == id1 || worker.getImportKey() == id2)
                    .collect(Collectors.toList());

            if (relationshipWorkers.size() == 2) {
                int level;
                switch (hexStringToInt(hexLine.get(35))) {
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
                relationships.add(
                        WorkerRelationship.builder()
                                .worker1(relationshipWorkers.get(0))
                                .worker2(relationshipWorkers.get(1))
                                .level(level)
                                .build()
                );
            }

        });

        return relationships;
    }


    private void assignRosterSplit(iRosterSplit item, Promotion promotion, List<RosterSplit> rosterSplits) {
        RosterSplit rosterSplit = rosterSplits.stream().filter(rs ->
                rs.getOwner().getPromotionID() == promotion.getPromotionID() &&
                        item.toString().contains(rs.getName())
        ).findFirst().orElse(null);
        item.setRosterSplit(rosterSplit);
    }

    List<Title> beltDat(List<Promotion> promotions, List<Worker> workers, LocalDate dayWon, List<RosterSplit> rosterSplits) {
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
                    .teamSize(CollectionUtils.isEmpty(champions) ? 1 : champions.size())
                    .build();

            if (!champions.isEmpty()) {
                TitleReign titleReign = TitleReign.builder()
                        .workers(champions)
                        .dayWon(dayWon)
                        .sequenceNumber(1)
                        .build();
                title.setTitleReigns(List.of(titleReign));
            }

            assignRosterSplit(title, promotion, rosterSplits);

            titles.add(title);
        });
        return titles;
    }

}
