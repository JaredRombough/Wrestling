package openwrestling.model.segment.constants.browse.mode;

import openwrestling.Logging;
import openwrestling.model.NewsItem;
import openwrestling.model.SegmentItem;
import openwrestling.model.controller.GameController;
import openwrestling.model.gameObjects.GameObject;
import openwrestling.model.gameObjects.Promotion;
import openwrestling.model.gameObjects.Worker;
import openwrestling.model.segment.constants.Gender;
import openwrestling.model.segment.constants.StaffType;
import openwrestling.model.utility.ModelUtils;
import org.apache.logging.log4j.Level;

import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static openwrestling.model.segment.constants.Gender.FEMALE;
import static openwrestling.model.segment.constants.Gender.MALE;

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
            case TOP_POPULARITY_MEN:
                return getTop100Workers(Comparator.comparingInt(Worker::getPopularity), MALE);
            case TOP_STRIKING_MEN:
                return getTop100Workers(Comparator.comparingInt(Worker::getStriking), MALE);
            case TOP_WRESTLING_MEN:
                return getTop100Workers(Comparator.comparingInt(Worker::getWrestling), MALE);
            case TOP_FLYING_MEN:
                return getTop100Workers(Comparator.comparingInt(Worker::getFlying), MALE);
            case TOP_CHARISMA_MEN:
                return getTop100Workers(Comparator.comparingInt(Worker::getCharisma), MALE);
            case TOP_WORKRATE_MEN:
                return getTop100Workers(Comparator.comparingInt(ModelUtils::getMatchWorkRating), MALE);
            case TOP_POPULARITY_WOMEN:
                return getTop100Workers(Comparator.comparingInt(Worker::getPopularity), FEMALE);
            case TOP_STRIKING_WOMEN:
                return getTop100Workers(Comparator.comparingInt(Worker::getStriking), FEMALE);
            case TOP_WRESTLING_WOMEN:
                return getTop100Workers(Comparator.comparingInt(Worker::getWrestling), FEMALE);
            case TOP_FLYING_WOMEN:
                return getTop100Workers(Comparator.comparingInt(Worker::getFlying), FEMALE);
            case TOP_CHARISMA_WOMEN:
                return getTop100Workers(Comparator.comparingInt(Worker::getCharisma), FEMALE);
            case TOP_WORKRATE_WOMEN:
                return getTop100Workers(Comparator.comparingInt(ModelUtils::getMatchWorkRating), FEMALE);
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

    private List<Worker> getTop100Workers(Comparator<Worker> comparator, Gender gender) {
        return getTop100Workers(comparator, worker -> gender.equals(worker.getGender()));
    }

    private List<Worker> getTop100Workers(Comparator<Worker> comparator, Predicate<Worker> filter) {
        return gameController.getWorkerManager().getWorkers().stream()
                .filter(filter)
                .sorted(comparator.reversed())
                .limit(100)
                .collect(Collectors.toList());
    }
}
