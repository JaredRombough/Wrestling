package wrestling.model.controller;

import java.util.ArrayList;
import java.util.List;
import wrestling.model.Promotion;
import wrestling.model.TagTeam;

public class TagTeamManager {

    private final List<TagTeam> tagTeams;
    private final ContractManager contractManager;

    public TagTeamManager(ContractManager contractManager) {
        tagTeams = new ArrayList();
        this.contractManager = contractManager;
    }

    public List<TagTeam> getTagTeams(Promotion promotion) {
        List<TagTeam> teams = new ArrayList<>();
        tagTeams.stream().filter((tt) -> (contractManager.getFullRoster(promotion).containsAll(tt.getWorkers()))).forEach((tt) -> {
            teams.add(tt);
        });
        return teams;
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

}
