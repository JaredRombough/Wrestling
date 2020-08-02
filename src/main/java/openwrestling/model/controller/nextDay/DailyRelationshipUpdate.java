package openwrestling.model.controller.nextDay;

import lombok.Builder;
import openwrestling.Logging;
import openwrestling.manager.ContractManager;
import openwrestling.manager.DateManager;
import openwrestling.manager.NewsManager;
import openwrestling.manager.RelationshipManager;
import openwrestling.model.NewsItem;
import openwrestling.model.gameObjects.Contract;
import openwrestling.model.gameObjects.MoraleRelationship;
import openwrestling.model.gameObjects.Worker;
import openwrestling.model.utility.ContractUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.time.temporal.ChronoUnit.DAYS;
import static openwrestling.model.constants.GameConstants.MORALE_PENALTY_DAYS_BETWEEN;


@Builder
public class DailyRelationshipUpdate extends Logging {

    private DateManager dateManager;
    private RelationshipManager relationshipManager;
    private ContractManager contractManager;
    private NewsManager newsManager;

    public List<MoraleRelationship> getUpdatedRelationshipsForDailyMoraleCheck() {
        List<MoraleRelationship> relationships = new ArrayList<>();
        contractManager.getActiveContracts().stream()
                .filter(contract -> ContractUtils.isMoraleCheckDay(contract, dateManager.today()))
                .forEach(contract -> {
                    long daysBetween = DAYS.between(contract.getLastShowDate(), dateManager.today());
                    int penalty = Math.round(daysBetween / MORALE_PENALTY_DAYS_BETWEEN);
                    if (penalty > 0) {
                        MoraleRelationship moraleRelationship = relationshipManager.getMoraleRelationship(contract.getWorker(), contract.getPromotion());

                        moraleRelationship.setLevel(moraleRelationship.getLevel() - penalty);
                        relationships.add(moraleRelationship);
                    }
                });
        return relationships;
    }

    public void updateRelationshipMap(Map<Worker, MoraleRelationship> moraleRelationshipMap, List<MoraleRelationship> dailyUpdateRelationships) {
        dailyUpdateRelationships.forEach(moraleRelationship -> {
            if (moraleRelationshipMap.containsKey(moraleRelationship.getWorker()) &&
                    moraleRelationshipMap.get(moraleRelationship.getWorker())
                            .getOtherSegmentItem(moraleRelationship.getOtherSegmentItem(moraleRelationship.getWorker()))
                            .equals(moraleRelationship.getPromotion())) {
                int sum = moraleRelationship.getLevel() + moraleRelationshipMap.get(moraleRelationship.getWorker()).getLevel();
                moraleRelationshipMap.get(moraleRelationship.getWorker()).setLevel(
                        sum == 0 ? sum : sum / 2
                );
            } else {
                moraleRelationshipMap.put(moraleRelationship.getWorker(), moraleRelationship);
            }
        });
    }

    public List<NewsItem> getUpdatedMoraleRelationshipNewsItems(List<MoraleRelationship> relationships) {
        return relationships.stream()
                .map(relationship -> {
                    Contract contract = contractManager.getContract(relationship.getWorker(), relationship.getPromotion());
                    long daysBetween = DAYS.between(contract.getLastShowDate(), dateManager.today());
                    int penalty = Math.round(daysBetween / MORALE_PENALTY_DAYS_BETWEEN);
                    return newsManager.getMoraleNewsItem(contract, daysBetween, penalty, dateManager.today());
                })
                .collect(Collectors.toList());
    }
}
