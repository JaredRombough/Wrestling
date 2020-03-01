package openwrestling.manager;


import openwrestling.database.Database;
import openwrestling.model.factory.PersonFactory;
import openwrestling.model.gameObjects.Promotion;
import openwrestling.model.gameObjects.StaffMember;
import openwrestling.model.segmentEnum.StaffType;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class BroadcastTeamManagerTest {


    private BroadcastTeamManager broadcastTeamManager;

    @Before
    public void setUp() {
        Database.createNewTempDatabase("testdb");
        broadcastTeamManager = new BroadcastTeamManager();
    }

    @Test
    public void setDefaultBroadcastTeam() {
        Promotion promotion = Database.insertGameObject(Promotion.builder().name(RandomStringUtils.random(10)).build());

        StaffMember staffMember = Database.insertGameObject(PersonFactory.randomStaff(1, StaffType.BROADCAST));
        StaffMember staffMember2 = Database.insertGameObject(PersonFactory.randomStaff(1, StaffType.BROADCAST));
        StaffMember staffMember3 = Database.insertGameObject(PersonFactory.randomStaff(1, StaffType.BROADCAST));

        broadcastTeamManager.setDefaultBroadcastTeam(promotion, List.of(staffMember, staffMember2));

        List<StaffMember> defaultBroadcastTeam = broadcastTeamManager.getDefaultBroadcastTeam(promotion);

        assertThat(defaultBroadcastTeam).hasSize(2);
        assertThat(defaultBroadcastTeam).extracting(StaffMember::getStaffMemberID).containsOnly(
                staffMember.getStaffMemberID(), staffMember2.getStaffMemberID()
        );
        assertThat(defaultBroadcastTeam).extracting(StaffMember::getName).containsOnly(
                staffMember.getName(), staffMember2.getName()
        );
        assertThat(defaultBroadcastTeam).extracting(StaffMember::getSkill).containsOnly(
                staffMember.getSkill(), staffMember2.getSkill()
        );

        broadcastTeamManager.setDefaultBroadcastTeam(promotion, List.of(staffMember3));

        List<StaffMember> defaultBroadcastTeam2 = broadcastTeamManager.getDefaultBroadcastTeam(promotion);

        assertThat(defaultBroadcastTeam2).hasSize(1);
        assertThat(defaultBroadcastTeam2).extracting(StaffMember::getStaffMemberID).containsOnly(
                staffMember3.getStaffMemberID()
        );
        assertThat(defaultBroadcastTeam2).extracting(StaffMember::getName).containsOnly(
                staffMember3.getName()
        );
        assertThat(defaultBroadcastTeam2).extracting(StaffMember::getSkill).containsOnly(
                staffMember3.getSkill()
        );

        broadcastTeamManager.setDefaultBroadcastTeam(promotion, List.of(staffMember, staffMember3));

        List<StaffMember> defaultBroadcastTeam3 = broadcastTeamManager.getDefaultBroadcastTeam(promotion);

        assertThat(defaultBroadcastTeam3).hasSize(2);
        assertThat(defaultBroadcastTeam3).extracting(StaffMember::getStaffMemberID).containsOnly(
                staffMember3.getStaffMemberID(), staffMember.getStaffMemberID()
        );
        assertThat(defaultBroadcastTeam3).extracting(StaffMember::getName).containsOnly(
                staffMember3.getName(), staffMember.getName()
        );
        assertThat(defaultBroadcastTeam3).extracting(StaffMember::getSkill).containsOnly(
                staffMember3.getSkill(), staffMember.getSkill()
        );

    }


}