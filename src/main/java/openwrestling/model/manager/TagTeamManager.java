package openwrestling.model.manager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import openwrestling.model.TagTeam;
import openwrestling.model.TagTeamWorker;
import openwrestling.model.modelView.PromotionView;
import openwrestling.model.modelView.TagTeamView;
import openwrestling.model.modelView.WorkerView;

public class TagTeamManager implements Serializable {

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

    public List<TagTeam> getTagTeams(PromotionView promotion) {
        List<TagTeam> teams = new ArrayList<>();
        tagTeams.stream().filter((tt) -> (promotion.getFullRoster()
                .containsAll(getWorkers(tt)))).forEach((tt) -> {
            teams.add(tt);
        });
        return teams;
    }

    public List<TagTeamView> getTagTeamViews(PromotionView promotion) {
        List<TagTeamView> teamViews = new ArrayList<>();
        getTagTeamViews().stream().filter((tagTeamView) -> (promotion.getFullRoster()
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
