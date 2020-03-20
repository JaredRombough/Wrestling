package openwrestling.manager;

import openwrestling.database.Database;
import openwrestling.model.gameObjects.BroadcastTeamMember;
import openwrestling.model.gameObjects.EventTemplate;
import openwrestling.model.gameObjects.Promotion;
import openwrestling.model.gameObjects.StaffMember;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BroadcastTeamManager extends GameObjectManager implements Serializable {

    private List<BroadcastTeamMember> broadcastTeamMembers = new ArrayList<>();

    public BroadcastTeamManager(Database database) {
        super(database);
    }

    @Override
    public void selectData() {
        broadcastTeamMembers = getDatabase().selectAll(BroadcastTeamMember.class);
    }

    public void setDefaultBroadcastTeam(Promotion promotion, List<StaffMember> newTeam) {
        List<BroadcastTeamMember> defaultBroadcastTeam = broadcastTeamMembers.stream()
                .filter(broadcastTeamMember -> promotion.equals(broadcastTeamMember.getPromotion()))
                .collect(Collectors.toList());

        defaultBroadcastTeam.forEach(broadcastTeamMember ->
                getDatabase().deleteByID(BroadcastTeamMember.class, broadcastTeamMember.getBroadcastTeamID())
        );

        List<BroadcastTeamMember> toInsert = newTeam.stream()
                .map(staffMember ->
                        BroadcastTeamMember.builder()
                                .staffMember(staffMember)
                                .promotion(promotion)
                                .build())
                .collect(Collectors.toList());

        getDatabase().insertList(toInsert);

        broadcastTeamMembers = getDatabase().selectAll(BroadcastTeamMember.class);
    }

    public void setDefaultBroadcastTeam(EventTemplate eventTemplate, List<StaffMember> newTeam) {
        List<BroadcastTeamMember> eventTemplateBroadcastTeam = broadcastTeamMembers.stream()
                .filter(broadcastTeamMember -> eventTemplate.equals(broadcastTeamMember.getEventTemplate()))
                .collect(Collectors.toList());

        eventTemplateBroadcastTeam.forEach(broadcastTeamMember ->
                getDatabase().deleteByID(BroadcastTeamMember.class, broadcastTeamMember.getBroadcastTeamID())
        );

        List<BroadcastTeamMember> toInsert = newTeam.stream()
                .map(staffMember ->
                        BroadcastTeamMember.builder()
                                .staffMember(staffMember)
                                .eventTemplate(eventTemplate)
                                .build())
                .collect(Collectors.toList());

        getDatabase().insertList(toInsert);

        broadcastTeamMembers = getDatabase().selectAll(BroadcastTeamMember.class);
    }

    public List<StaffMember> getDefaultBroadcastTeam(Promotion promotion) {
        return broadcastTeamMembers.stream()
                .filter(broadcastTeamMember -> promotion.equals(broadcastTeamMember.getPromotion()))
                .map(BroadcastTeamMember::getStaffMember)
                .collect(Collectors.toList());
    }

    public List<StaffMember> getDefaultBroadcastTeam(EventTemplate eventTemplate) {
        return broadcastTeamMembers.stream()
                .filter(broadcastTeamMember -> eventTemplate.equals(broadcastTeamMember.getEventTemplate()))
                .map(BroadcastTeamMember::getStaffMember)
                .collect(Collectors.toList());
    }


}
