package wrestling.model.manager;

import java.util.ArrayList;
import java.util.List;
import wrestling.model.Promotion;
import wrestling.model.TagTeam;
import wrestling.model.TagTeamWorker;
import wrestling.model.Worker;

public class TagTeamManager {

    private final List<TagTeam> tagTeams;
    private final List<TagTeamWorker> tagTeamWorkers;
    private final ContractManager contractManager;

    public TagTeamManager(ContractManager contractManager) {
        tagTeams = new ArrayList<>();
        tagTeamWorkers = new ArrayList<>();
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

    public void addTagTeamWorker(TagTeamWorker tagTeamWorker) {
        this.tagTeamWorkers.add(tagTeamWorker);
    }

}
