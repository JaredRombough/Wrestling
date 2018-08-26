package wrestling.model.manager;

import java.util.ArrayList;
import java.util.List;
import wrestling.model.Promotion;
import wrestling.model.TagTeam;
import wrestling.model.TagTeamWorker;
import wrestling.model.modelView.TagTeamView;
import wrestling.model.modelView.WorkerView;

public class TagTeamManager {

    private final List<TagTeam> tagTeams;
    private final List<TagTeamWorker> tagTeamWorkers;
    private final ContractManager contractManager;
    private final List<TagTeamView> tagTeamViews;

    public TagTeamManager(ContractManager contractManager) {
        tagTeams = new ArrayList<>();
        tagTeamWorkers = new ArrayList<>();
        tagTeamViews = new ArrayList<>();
        this.contractManager = contractManager;
    }

    public List<TagTeam> getTagTeams(Promotion promotion) {
        List<TagTeam> teams = new ArrayList<>();
        tagTeams.stream().filter((tt) -> (contractManager.getFullRoster(promotion)
                .containsAll(getWorkers(tt)))).forEach((tt) -> {
            teams.add(tt);
        });
        return teams;
    }

    public List<TagTeamView> getTagTeamViews(Promotion promotion) {
        List<TagTeamView> teamViews = new ArrayList<>();
        getTagTeamViews().stream().filter((tagTeamView) -> (contractManager.getFullRoster(promotion)
                .containsAll(tagTeamView.getWorkers()))).forEach((tagTeamView) -> {
            teamViews.add(tagTeamView);
        });
        return teamViews;
    }

    public List<WorkerView> getWorkers(TagTeam tagTeam) {
        List<WorkerView> workers = new ArrayList<>();
        tagTeamWorkers.stream().filter((tagTeamWorker) -> (tagTeamWorker.getTagTeam().equals(tagTeam))).forEach((tagTeamWorker) -> {
            workers.add(tagTeamWorker.getWorker());
        });
        return workers;
    }

    public TagTeamView createTagTeam(String teamName, WorkerView workerA, WorkerView workerB) {
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
