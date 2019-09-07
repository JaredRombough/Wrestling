package openwrestling.model.manager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import openwrestling.manager.ContractManager;
import openwrestling.manager.WorkerManager;
import openwrestling.model.TagTeam;
import openwrestling.model.TagTeamWorker;
import openwrestling.model.gameObjects.Promotion;
import openwrestling.model.modelView.TagTeamView;
import openwrestling.model.gameObjects.Worker;

public class TagTeamManager implements Serializable {

    private final List<TagTeam> tagTeams;
    private final List<TagTeamWorker> tagTeamWorkers;
    private final ContractManager contractManager;
    private final WorkerManager workerManager;
    private final List<TagTeamView> tagTeamViews;

    public TagTeamManager(ContractManager contractManager, WorkerManager workerManager) {
        tagTeams = new ArrayList<>();
        tagTeamWorkers = new ArrayList<>();
        tagTeamViews = new ArrayList<>();
        this.contractManager = contractManager;
        this.workerManager = workerManager;
    }

    public List<TagTeam> getTagTeams(Promotion promotion) {
        List<TagTeam> teams = new ArrayList<>();
        List<Worker> roster = workerManager.selectRoster(promotion);
        tagTeams.stream().filter((tt) -> (roster
                .containsAll(getWorkers(tt)))).forEach((tt) -> {
            teams.add(tt);
        });
        return teams;
    }

    public List<TagTeamView> getTagTeamViews(Promotion promotion) {
        List<TagTeamView> teamViews = new ArrayList<>();
        List<Worker> roster = workerManager.selectRoster(promotion);
        getTagTeamViews().stream().filter((tagTeamView) -> (roster
                .containsAll(tagTeamView.getWorkers()))).forEach((tagTeamView) -> {
            teamViews.add(tagTeamView);
        });
        return teamViews;
    }

    public List<Worker> getWorkers(TagTeam tagTeam) {
        List<Worker> workers = new ArrayList<>();
        tagTeamWorkers.stream().filter((tagTeamWorker) -> (tagTeamWorker.getTagTeam().equals(tagTeam))).forEach((tagTeamWorker) -> {
            workers.add(tagTeamWorker.getWorker());
        });
        return workers;
    }

    public TagTeamView createTagTeam(String teamName, Worker workerA, Worker workerB) {
        TagTeam tagTeam = new TagTeam();
        tagTeam.setName(teamName);
        tagTeams.add(tagTeam);

        TagTeamWorker tagTeamWorkerA = new TagTeamWorker(tagTeam, workerA);
        TagTeamWorker tagTeamWorkerB = new TagTeamWorker(tagTeam, workerB);
        tagTeamWorkers.add(tagTeamWorkerA);
        tagTeamWorkers.add(tagTeamWorkerB);

        TagTeamView tagTeamView = new TagTeamView();
        tagTeamView.setTagTeam(tagTeam);
        tagTeamView.addWorker(workerA);
        tagTeamView.addWorker(workerB);
        tagTeamViews.add(tagTeamView);

        return tagTeamView;

    }

    /**
     * @return the tagTeams
     */
    public List<TagTeam> getTagTeams() {
        return tagTeams;
    }

    /**
     * @param tagTeams the tagTeams to set
     */
    public void addTagTeams(List<TagTeam> tagTeams) {
        for (TagTeam team : tagTeams) {
            this.tagTeams.add(team);
        }
    }

    public void addTagTeamViews(List<TagTeamView> tagTeamViews) {
        for (TagTeamView team : tagTeamViews) {
            this.getTagTeamViews().add(team);
        }
    }

    public void addTagTeamWorker(TagTeamWorker tagTeamWorker) {
        this.tagTeamWorkers.add(tagTeamWorker);
    }

    /**
     * @return the tagTeamViews
     */
    public List<TagTeamView> getTagTeamViews() {
        return tagTeamViews;
    }

}
