package wrestling.model.manager;

import java.util.ArrayList;
import java.util.List;
import wrestling.model.Promotion;
import wrestling.model.SegmentItem;
import wrestling.model.TagTeam;
import wrestling.model.TagTeamWorker;
import wrestling.model.Worker;
import wrestling.model.modelView.TagTeamView;

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

    public List<Worker> getWorkers(TagTeam tagTeam) {
        List<Worker> workers = new ArrayList<>();
        tagTeamWorkers.stream().filter((tagTeamWorker) -> (tagTeamWorker.getTagTeam().equals(tagTeam))).forEach((tagTeamWorker) -> {
            workers.add(tagTeamWorker.getWorker());
        });
        return workers;
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
