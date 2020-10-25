package openwrestling.model.segment.constants.browse.mode;

import openwrestling.Logging;
import openwrestling.model.NewsItem;
import openwrestling.model.SegmentItem;
import openwrestling.model.controller.GameController;
import openwrestling.model.gameObjects.GameObject;
import openwrestling.model.gameObjects.Promotion;
import openwrestling.model.gameObjects.Worker;
import openwrestling.model.segment.constants.StaffType;
import openwrestling.model.utility.ModelUtils;
import org.apache.logging.log4j.Level;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class GameObjectQueryHelper extends Logging {

    private final GameController gameController;

    public GameObjectQueryHelper(GameController gameController) {
        this.gameController = gameController;
    }


    public List<? extends SegmentItem> segmentItemsToBrowse(BrowseMode browseMode, Promotion promotion) {
        switch (browseMode) {
            case FREE_AGENTS:
                return gameController.getWorkerManager().freeAgents(promotion);
            case WORKERS:
                return gameController.getWorkerManager().getRoster(promotion);
            case HIRE_STAFF:
                return gameController.getStaffManager().getAvailableStaff();
            case STAFF:
                return gameController.getStaffManager().getStaffMembers(promotion);
            case REFS:
                return gameController.getStaffManager().getStaff(StaffType.REFEREE, promotion);
            case BROADCAST:
                return gameController.getStaffManager().getStaff(StaffType.BROADCAST, promotion);
            case TITLES:
                return gameController.getTitleManager().getTitles(promotion);
            case TAG_TEAMS:
                return gameController.getTagTeamManager().getTagTeams(promotion);
            case STABLES:
                return gameController.getStableManager().getStables()
                        .stream().filter((stable) -> stable.getOwner().equals(promotion)).collect(Collectors.toList());
            case ROSTER_SPLIT:
                return gameController.getRosterSplitManager().getRosterSplits()
                        .stream().filter((split) -> split.getOwner().equals(promotion)).collect(Collectors.toList());
            default:
                break;
        }
        logger.log(Level.WARN, "Unknown browse mode {}", browseMode);
        return List.of();
    }

    public List<? extends GameObject> listToBrowse(BrowseMode browseMode, Promotion promotion) {
        switch (browseMode) {
            case FREE_AGENTS:
                return gameController.getWorkerManager().freeAgents(promotion);
            case WORKERS:
                return gameController.getWorkerManager().getRoster(promotion);
            case HIRE_STAFF:
                return gameController.getStaffManager().getAvailableStaff();
            case STAFF:
                return gameController.getStaffManager().getStaffMembers(promotion);
            case REFS:
                return gameController.getStaffManager().getStaff(StaffType.REFEREE, promotion);
            case BROADCAST:
                return gameController.getStaffManager().getStaff(StaffType.BROADCAST, promotion);
            case TITLES:
                return gameController.getTitleManager().getTitles(promotion);
            case TAG_TEAMS:
                return gameController.getTagTeamManager().getTagTeams(promotion);
            case STABLES:
                return gameController.getStableManager().getStables()
                        .stream().filter((stable) -> stable.getOwner().equals(promotion)).collect(Collectors.toList());
            case ROSTER_SPLIT:
                return gameController.getRosterSplitManager().getRosterSplits()
                        .stream().filter((split) -> split.getOwner().equals(promotion)).collect(Collectors.toList());
            case EVENTS:
                return gameController.getEventManager().getEventTemplates(promotion);
            case NEWS:
                return gameController.getNewsManager().getNewsItems().stream()
                        .sorted(Comparator.comparing(NewsItem::getDate).reversed())
                        .collect(Collectors.toList());
            case MATCHES:
                return gameController.getSegmentManager().getMatches();
            case TOP_POPULARITY:
                return getTop100Workers(Comparator.comparingInt(Worker::getPopularity));
            case TOP_STRIKING:
                return getTop100Workers(Comparator.comparingInt(Worker::getStriking));
            case TOP_WRESTLING:
                return getTop100Workers(Comparator.comparingInt(Worker::getWrestling));
            case TOP_FLYING:
                return getTop100Workers(Comparator.comparingInt(Worker::getFlying));
            case TOP_CHARISMA:
                return getTop100Workers(Comparator.comparingInt(Worker::getCharisma));
            case TOP_WORKRATE:
                return getTop100Workers(Comparator.comparingInt(ModelUtils::getMatchWorkRating));
            default:
                break;
        }
        logger.log(Level.WARN, "Unknown browse mode {}", browseMode);
        return List.of();
    }

    private List<Worker> getTop100Workers(Comparator<Worker> comparator) {
        return gameController.getWorkerManager().getWorkers().stream()
                .sorted(comparator.reversed())
                .limit(100)
                .collect(Collectors.toList());
    }
}
