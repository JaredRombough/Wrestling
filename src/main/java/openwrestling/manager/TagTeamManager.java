package openwrestling.manager;

import lombok.Getter;
import openwrestling.database.Database;
import openwrestling.model.gameObjects.Promotion;
import openwrestling.model.gameObjects.TagTeam;
import openwrestling.model.gameObjects.Worker;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TagTeamManager extends GameObjectManager implements Serializable {

    private final WorkerManager workerManager;

    @Getter
    private List<TagTeam> tagTeams;

    public TagTeamManager(Database database, WorkerManager workerManager) {
        super(database);
        tagTeams = new ArrayList<>();
        this.workerManager = workerManager;
    }

    @Override
    public void selectData() {
        tagTeams = getDatabase().selectAll(TagTeam.class);
        tagTeams.forEach(tagTeam -> tagTeam.setWorkers(workerManager.refreshWorkers(tagTeam.getWorkers())));
    }

    public List<TagTeam> createTagTeams(List<TagTeam> toInsert) {
        long newTagTeamID = getDatabase().insertList(toInsert).get(0).getTagTeamID();
        tagTeams = getDatabase().selectAll(TagTeam.class);
        tagTeams.forEach(tagTeam -> tagTeam.setWorkers(workerManager.refreshWorkers(tagTeam.getWorkers())));
        return tagTeams.stream()
                .filter(tagTeam -> tagTeam.getTagTeamID() == newTagTeamID)
                .collect(Collectors.toList());
    }

    public TagTeam createTagTeam(TagTeam toInsert) {
        return createTagTeams(List.of(toInsert)).get(0);
    }

    public TagTeam updateTagTeam(TagTeam updatedTagTeam) {
        return updateTagTeams(List.of(updatedTagTeam)).get(0);
    }

    public List<TagTeam> updateTagTeams(List<TagTeam> updatedTagTeams) {
        getDatabase().updateList(updatedTagTeams);
        tagTeams = getDatabase().selectAll(TagTeam.class);
        tagTeams.forEach(tagTeam -> tagTeam.setWorkers(workerManager.refreshWorkers(tagTeam.getWorkers())));
        return tagTeams;
    }

    public List<TagTeam> getTagTeams(Promotion promotion) {
        List<TagTeam> teamViews = new ArrayList<>();
        List<Worker> roster = workerManager.getRoster(promotion);
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

}
