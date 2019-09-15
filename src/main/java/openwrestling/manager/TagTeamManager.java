package openwrestling.manager;

import openwrestling.database.Database;
import openwrestling.model.gameObjects.Promotion;
import openwrestling.model.gameObjects.Worker;
import openwrestling.model.gameObjects.TagTeam;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TagTeamManager implements Serializable {

    private final WorkerManager workerManager;
    private final List<TagTeam> tagTeams;

    public TagTeamManager( WorkerManager workerManager) {
        tagTeams = new ArrayList<>();
        this.workerManager = workerManager;
    }

    public List<TagTeam> createTagTeams(List<TagTeam> tagTeams) {
        List saved = Database.insertList(tagTeams);
        this.tagTeams.addAll(saved);
        return saved;
    }


    public List<TagTeam> getTagTeams(Promotion promotion) {
        List<TagTeam> teamViews = new ArrayList<>();
        List<Worker> roster = workerManager.selectRoster(promotion);
        getTagTeams().stream().filter((tagTeam) -> (roster
                .containsAll(tagTeam.getWorkers()))).forEach((tagTeam) -> {
            teamViews.add(tagTeam);
        });
        return teamViews;
    }


    public TagTeam createTagTeam(String teamName, Worker workerA, Worker workerB) {
        TagTeam tagTeam = new TagTeam();
        tagTeam.addWorker(workerA);
        tagTeam.addWorker(workerB);
        tagTeams.add(tagTeam);

        return tagTeam;
    }

    /**
     * @return the tagTeams
     */
    public List<TagTeam> getTagTeams() {
        return tagTeams;
    }

}
