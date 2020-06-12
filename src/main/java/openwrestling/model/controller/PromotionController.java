package openwrestling.model.controller;

import openwrestling.Logging;
import openwrestling.manager.ContractManager;
import openwrestling.manager.DateManager;
import openwrestling.manager.NewsManager;
import openwrestling.manager.StaffManager;
import openwrestling.manager.TitleManager;
import openwrestling.manager.WorkerManager;
import openwrestling.model.factory.ContractFactory;
import openwrestling.model.factory.EventFactory;
import openwrestling.model.gameObjects.Event;
import openwrestling.model.gameObjects.Promotion;
import openwrestling.model.gameObjects.Segment;
import openwrestling.model.gameObjects.SegmentTeam;
import openwrestling.model.gameObjects.StaffMember;
import openwrestling.model.gameObjects.Title;
import openwrestling.model.gameObjects.Worker;
import openwrestling.model.segmentEnum.SegmentType;
import openwrestling.model.segmentEnum.StaffType;
import openwrestling.model.segmentEnum.TeamType;
import openwrestling.model.utility.ModelUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.logging.log4j.Level;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static openwrestling.model.constants.GameConstants.BASE_TRAINER_SUCCESS_RATE;

public class PromotionController extends Logging implements Serializable {

    private final ContractFactory contractFactory;
    private final EventFactory eventFactory;

    private final ContractManager contractManager;
    private final DateManager dateManager;
    private final TitleManager titleManager;
    private final WorkerManager workerManager;
    private final NewsManager newsManager;
    private final StaffManager staffManager;

    public PromotionController(
            ContractFactory contractFactory,
            EventFactory eventFactory,
            ContractManager contractManager,
            DateManager dateManager,
            TitleManager titleManager,
            WorkerManager workerManager,
            NewsManager newsManager,
            StaffManager staffManager) {
        this.contractFactory = contractFactory;
        this.eventFactory = eventFactory;
        this.contractManager = contractManager;
        this.dateManager = dateManager;
        this.titleManager = titleManager;
        this.workerManager = workerManager;
        this.newsManager = newsManager;
        this.staffManager = staffManager;
    }

    private int idealRosterSize(Promotion promotion) {
        return 10 + (promotion.getLevel() * 10);
    }

    private int maxPushListSize(Promotion promotion) {
        return 2 + (promotion.getLevel() * 2);
    }

    private void updatePushed(Promotion promotion) {

        List<Worker> pushList = contractManager.getPushed(promotion);
        int diff = maxPushListSize(promotion) - pushList.size();

        if (diff > 0) {
            int i = 0;
            List<Worker> roster = workerManager.selectRoster(promotion);
            for (Worker worker : roster) {
                if (!pushList.contains(worker) && worker.isFullTime()) {
                    contractManager.getContract(worker, promotion).setPushed(true);
                }
                if (i >= diff) {
                    break;
                }
                i++;
            }
        } else if (diff < 0) {
            for (int i = 0; i < pushList.size(); i++) {
                contractManager.getContract(pushList.get(i), promotion).setPushed(false);
                if (i >= Math.abs(diff)) {
                    break;
                }
            }
        }
    }


    //call this method every day for each ai
    //put the general decision making sequence here
    public void dailyUpdate(Promotion promotion) {
        logger.log(Level.DEBUG, "start dailyUpdate for " + promotion.getName());
        if (contractManager.getPushed(promotion).size() != maxPushListSize(promotion)) {
            updatePushed(promotion);
        }
        int activeRosterSize = contractManager.getActiveRoster(promotion).size();
        while (activeRosterSize < idealRosterSize(promotion) && !workerManager.freeAgents(promotion).isEmpty()) {
            //signContract(promotion);
            activeRosterSize++;
        }

        logger.log(Level.DEBUG, "end dailyUpdate for " + promotion.getName());
    }

    public void trainerUpdate(Promotion promotion) {
        for (StaffMember trainer : staffManager.getStaff(StaffType.TRAINER, promotion)) {
            if (RandomUtils.nextInt(0, BASE_TRAINER_SUCCESS_RATE) == 1 && RandomUtils.nextInt(0, BASE_TRAINER_SUCCESS_RATE) < trainer.getSkill()) {
                List<Worker> roster = workerManager.selectRoster(promotion);
                Worker worker = roster.get(RandomUtils.nextInt(0, roster.size() - 1));
                Map<String, Integer> properties = new HashMap<>();
                properties.put("striking", worker.getStriking());
                properties.put("flying", worker.getFlying());
                properties.put("wrestling", worker.getWrestling());
                properties.put("charisma", worker.getCharisma());
                List keys = new ArrayList(properties.keySet());
                Collections.shuffle(keys);
                boolean success = false;
                String stat = "";
                for (Object o : keys) {
                    int value = properties.get(o.toString());
                    if (value < trainer.getSkill()) {
                        stat = o.toString();
                        switch (o.toString()) {
                            case "striking":
                                worker.setStriking(value + 1);
                                break;
                            case "flying":
                                worker.setStriking(value + 1);
                                break;
                            case "wrestling":
                                worker.setStriking(value + 1);
                                break;
                            case "charisma":
                                worker.setStriking(value + 1);
                                break;
                        }
                        success = true;
                        break;
                    }

                }
                if (success) {
                    newsManager.addTrainingNewsItem(worker, trainer, promotion, stat, dateManager.today());
                }
            }
        }
    }

    private void sortByPopularity(List<Worker> workerList) {
        //sort roster by popularity
        Collections.sort(workerList, (Worker w1, Worker w2) -> -Integer.valueOf(w1.getPopularity()).compareTo(w2.getPopularity()));
    }

    //sign a contract with the first suitable worker found
    private void signContract(Promotion promotion) {

        for (Worker worker : workerManager.freeAgents(promotion)) {
            if (worker.getPopularity() <= ModelUtils.maxPopularity(promotion)) {
                contractFactory.createContract(worker, promotion, dateManager.today());
                break;
            }
        }
    }

    private List<Segment> bookSegments(Promotion promotion, int duration) {
        //maximum segments for the event
        int maxSegments = 8;

        List<Worker> pushList = contractManager.getPushed(promotion);

        //bigger promotions get more segments
        if (promotion.getLevel() > 3) {
            maxSegments += 2;
        }
        //lists to track workers the event roster
        //and workers that are already booked on this date
        List<Worker> eventRoster = workerManager.selectRoster(promotion);

        //list to track workers on the pushlist that are still available
        List<Worker> pushListPresent = new ArrayList<>();

        //move pushlist workers present to the pushlistpresent from the event roster
        for (int i = 0; i < pushList.size(); i++) {
            if (eventRoster.contains(pushList.get(i))) {
                eventRoster.remove(pushList.get(i));
                pushListPresent.add(pushList.get(i));
            }
        }

        //sort the lists of workers for the event by popularity
        sortByPopularity(pushList);
        sortByPopularity(eventRoster);

        //list to hold event segments
        List<Segment> segments = new ArrayList<>();

        //list to hold workers who have been booked for this event
        List<Worker> matchBooked = new ArrayList<>();

        //get a list of titles available for the event
        List<Title> eventTitles = titleManager.getEventTitles(promotion, eventRoster);

        //book title matches
        for (Title title : eventTitles) {

            //determine team size based on the title
            int teamSize = title.getTeamSize();

            //determine the number of teams (usually 2)
            int teamsNeeded = 2;
            int random = RandomUtils.nextInt(1, 10);
            if (random > 8) {
                teamsNeeded += 10 - random;
            }

            List<SegmentTeam> matchTeams = new ArrayList<>();
            List<Worker> champs = title.getChampions();

            //if the title is not vacant, make the title holders team 1
            if (!champs.isEmpty()) {
                matchTeams.add(
                        SegmentTeam.builder()
                                .workers(champs)
                                .type(TeamType.WINNER)
                                .build()
                );
                matchBooked.addAll(champs);
            }

            //list to hold the lists we will draw workers from
            //in order of priority
            List<List<Worker>> workerResources = new ArrayList<>();

            workerResources.add(pushListPresent);
            workerResources.add(eventRoster);

            //loop for the number of teams we want
            for (int i = 0; i < teamsNeeded; i++) {

                List<Worker> team = new ArrayList<>();
                boolean teamMade = false;

                //iterate through resources
                for (List<Worker> resouce : workerResources) {

                    //iterate through workers in the resource
                    for (Worker worker : resouce) {

                        //if the worker isn't in this team or already booked, add them
                        //to the team
                        if (!matchBooked.contains(worker) && !team.contains(worker)) {
                            team.add(worker);
                        }

                        //if the team is big enough, break out of the loop
                        if (team.size() >= teamSize) {
                            matchTeams.add(
                                    SegmentTeam.builder()
                                            .workers(team)
                                            .type(TeamType.LOSER)
                                            .build()
                            );
                            matchBooked.addAll(team);
                            teamMade = true;
                            break;
                        }

                    }
                    if (teamMade) {
                        break;
                    }
                }
            }

            //make sure we have enough workers for a match
            if (matchTeams.size() > 1) {
                //roll for title change
                if (RandomUtils.nextInt(1, 10) > 5) {
                    Collections.swap(matchTeams, 0, 1);
                }

                Segment segment = new Segment(SegmentType.MATCH);
                segment.setTeams(matchTeams);
                segment.addTitle(title);
                segments.add(segment);
            }
        }

        //fill up the segments if we don't have enough for some reason
        if (segments.size() < maxSegments) {

            //go through the roster by popularity and make singles matches
            for (int i = 0; i < eventRoster.size(); i += 2) {
                if (eventRoster.size() > i + 1) {
                    //move this somewhere else, like a matchFactory
                    List<Worker> teamA = new ArrayList<>(Arrays.asList(eventRoster.get(i)));
                    List<Worker> teamB = new ArrayList<>(Arrays.asList(eventRoster.get(i + 1)));
                    List<SegmentTeam> teams = new ArrayList<>();
                    teams.add(
                            SegmentTeam.builder()
                                    .workers(teamA)
                                    .type(TeamType.WINNER)
                                    .build()
                    );
                    teams.add(
                            SegmentTeam.builder()
                                    .workers(teamB)
                                    .type(TeamType.LOSER)
                                    .build()
                    );

                    Segment segment = new Segment(SegmentType.MATCH);
                    segment.setTeams(teams);

                    segments.add(segment);
                }

                if (segments.size() > maxSegments) {
                    break;
                }

            }
        }

        if (CollectionUtils.isNotEmpty(segments)) {
            segments.forEach(segment -> segment.setSegmentLength(duration / segments.size()));
        }

        return segments;
    }

    //book an event
    public Event bookEvent(Event event, Promotion promotion) {
        event.setSegments(bookSegments(promotion, event.getDefaultDuration()));
        return eventFactory.processEventView(
                event,
                true);
    }
}
