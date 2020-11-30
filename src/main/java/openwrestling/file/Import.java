package openwrestling.file;

import openwrestling.database.Database;
import openwrestling.model.controller.GameController;
import openwrestling.model.gameObjects.Contract;
import openwrestling.model.gameObjects.EventTemplate;
import openwrestling.model.gameObjects.Promotion;
import openwrestling.model.gameObjects.RosterSplit;
import openwrestling.model.gameObjects.Stable;
import openwrestling.model.gameObjects.StaffContract;
import openwrestling.model.gameObjects.StaffMember;
import openwrestling.model.gameObjects.TagTeam;
import openwrestling.model.gameObjects.Title;
import openwrestling.model.gameObjects.Worker;
import openwrestling.model.gameObjects.WorkerRelationship;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Import {

    private final transient Logger logger = LogManager.getLogger(getClass());
    private final List<String> filesNeeded = new ArrayList<>(Arrays.asList(
            "promos",
            "belt",
            "teams",
            "wrestler"
    ));
    private GameController gameController;

    public String tryImport(File dbFile, File importFolder) throws Exception {
        long start = System.currentTimeMillis();

        StringBuilder sb = new StringBuilder();
        filesNeeded.stream().map((s) -> new File(importFolder.getPath() + "\\" + s + ".dat")).filter((f) -> (!f.exists() || f.isDirectory())).forEach((f) -> {
            sb.append(f.toString()).append(" not found.").append("\n");
        });

        if (sb.length() == 0) {
            try {
                Database database = new Database(dbFile);
                database.createNewDatabase();
                gameController = new GameController(database, false);
                ImportHelper importHelper = new ImportHelper(importFolder);

                List<Promotion> promotions = importHelper.promotionsDat("promos");
                promotions = gameController.getPromotionManager().createPromotions(promotions);

                List<Worker> workers = importHelper.workersDat();
                workers = gameController.getWorkerManager().createWorkers(workers);

                List<Worker> workersWithManagers = importHelper.setManagers(workers);
                gameController.getWorkerManager().updateWorkers(workersWithManagers);
                workers = gameController.getWorkerManager().getWorkers();

                List<Promotion> otherPromotions = gameController.getPromotionManager().createPromotions(importHelper.otherPromotions());
                List<Contract> otherPromotionContracts = importHelper.otherPromotionContracts(otherPromotions, workers, gameController.getDateManager().today());
                otherPromotionContracts = gameController.getContractManager().createContracts(otherPromotionContracts);
                List<Promotion> updatedPromotions = importHelper.updateOther(otherPromotions, otherPromotionContracts);
                gameController.getPromotionManager().updatePromotions(updatedPromotions);

                List<RosterSplit> rosterSplits = importHelper.rosterSplits("promos", promotions);
                rosterSplits = importHelper.assignWorkersToRosterSplits(workers, rosterSplits);
                rosterSplits = gameController.getRosterSplitManager().createRosterSplits(rosterSplits);

                List<Contract> contracts = importHelper.contracts(workers, promotions, gameController.getDateManager().today());
                gameController.getContractManager().createContracts(contracts);

                List<TagTeam> tagTeams = importHelper.teamsDat(workers);
                gameController.getTagTeamManager().createTagTeams(tagTeams);

                List<Stable> stables = importHelper.stablesDat(workers, promotions);
                gameController.getStableManager().createStables(stables);

                List<Title> titles = importHelper.beltDat(promotions, workers, gameController.getDateManager().today(), rosterSplits);
                gameController.getTitleManager().createTitles(titles);

                List<EventTemplate> tvTemplates = importHelper.tvDat(promotions, rosterSplits);
                gameController.getEventManager().createEventTemplates(tvTemplates);

                List<EventTemplate> eventTemplates = importHelper.eventDat(rosterSplits, promotions);
                gameController.getEventManager().createEventTemplates(eventTemplates);

                List<StaffMember> staffMembers = importHelper.staffDat();
                staffMembers = gameController.getStaffManager().createStaffMembers(staffMembers);

                List<StaffContract> staffContracts = importHelper.staffContracts(promotions, staffMembers, gameController.getDateManager().today());
                gameController.getContractManager().createStaffContracts(staffContracts);

                List<WorkerRelationship> relationships = importHelper.relateDat(workers);
                gameController.getRelationshipManager().createWorkerRelationships(relationships);

            } catch (Exception ex) {

                sb.append(ex);
                logger.log(Level.ERROR, ex);
                throw ex;
            }

            //for statistical evaluation of data only
            /* boolean evaluate = false;
            if (evaluate) {
                EvaluateData.evaluateData(allPromotions, allWorkers);
            }*/
        }

        logger.log(Level.DEBUG, String.format("import took %d ms",
                System.currentTimeMillis() - start)
        );

        return sb.toString();

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
                    promotion.setImageFileName(advancedImportData.get(i + 3));
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
