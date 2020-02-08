package openwrestling.manager;

import lombok.NoArgsConstructor;
import openwrestling.database.Database;
import openwrestling.model.gameObjects.BroadcastTeamMember;
import openwrestling.model.gameObjects.EventTemplate;
import openwrestling.model.gameObjects.Promotion;
import openwrestling.model.gameObjects.StaffMember;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor
public class BroadcastTeamManager extends GameObjectManager implements Serializable {

    private List<BroadcastTeamMember> broadcastTeamMembers = new ArrayList<>();

    @Override
    public void selectData() {
        broadcastTeamMembers = Database.selectAll(BroadcastTeamMember.class);
    }

    public void setDefaultBroadcastTeam(Promotion promotion, List<StaffMember> newTeam) {
        List<BroadcastTeamMember> defaultBroadcastTeam = broadcastTeamMembers.stream()
                .filter(broadcastTeamMember -> promotion.equals(broadcastTeamMember.getPromotion()))
                .collect(Collectors.toList());

        defaultBroadcastTeam.forEach(broadcastTeamMember ->
                Database.deleteByID(BroadcastTeamMember.class, broadcastTeamMember.getBroadcastTeamID())
        );

        List<BroadcastTeamMember> toInsert = newTeam.stream()
                .map(staffMember ->
                        BroadcastTeamMember.builder()
                                .staffMember(staffMember)
                                .promotion(promotion)
                                .build())
                .collect(Collectors.toList());

        Database.insertList(toInsert);

        broadcastTeamMembers = Database.selectAll(BroadcastTeamMember.class);
    }

    public void setDefaultBroadcastTeam(EventTemplate eventTemplate, List<StaffMember> newTeam) {
        List<BroadcastTeamMember> eventTemplateBroadcastTeam = broadcastTeamMembers.stream()
                .filter(broadcastTeamMember -> eventTemplate.equals(broadcastTeamMember.getEventTemplate()))
                .collect(Collectors.toList());

        eventTemplateBroadcastTeam.forEach(broadcastTeamMember ->
                Database.deleteByID(BroadcastTeamMember.class, broadcastTeamMember.getBroadcastTeamID())
        );

        List<BroadcastTeamMember> toInsert = newTeam.stream()
                .map(staffMember ->
                        BroadcastTeamMember.builder()
                                .staffMember(staffMember)
                                .eventTemplate(eventTemplate)
                                .build())
                .collect(Collectors.toList());

        Database.insertList(toInsert);

        broadcastTeamMembers = Database.selectAll(BroadcastTeamMember.class);
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
