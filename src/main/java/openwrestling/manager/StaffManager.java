package openwrestling.manager;


import openwrestling.database.Database;
import openwrestling.model.gameObjects.Promotion;
import openwrestling.model.gameObjects.StaffMember;
import openwrestling.model.segmentEnum.StaffType;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class StaffManager extends GameObjectManager implements Serializable {

    private final ContractManager contractManager;
    private List<StaffMember> staffMembers = new ArrayList<>();

    public StaffManager(Database database, ContractManager contractManager) {
        super(database);
        this.contractManager = contractManager;
    }

    @Override
    public void selectData() {
        staffMembers = getDatabase().selectAll(StaffMember.class);
    }

    public List<StaffMember> createStaffMembers(List<StaffMember> staffMembers) {
        List saved = getDatabase().insertList(staffMembers);
        this.staffMembers.addAll(saved);
        return saved;
    }

    public List<StaffMember> getStaffMembers(Promotion promotion) {
        List<StaffMember> staffMembersForPromotion = new ArrayList<>();
        contractManager.getStaffContracts().forEach(contract -> {
            if (contract.isActive() && contract.getPromotion().getPromotionID() == promotion.getPromotionID()) {
                staffMembersForPromotion.add(contract.getStaff());
            }
        });
        return staffMembersForPromotion;
    }

    public List<StaffMember> getStaff(StaffType staffType, Promotion promotion) {
        return getStaffMembers(promotion).stream()
                .filter(staffMember -> staffType.equals(staffMember.getStaffType()))
                .collect(Collectors.toList());
    }

    public StaffMember getOwner(Promotion promotion) {
        return getStaff(StaffType.OWNER, promotion).stream()
                .findFirst()
                .orElse(null);
    }

    public List<StaffMember> getAvailableStaff() {
        List<StaffMember> availableStaff = new ArrayList<>();
        for (StaffMember staff : staffMembers) {
            if (staff.getStaffContract() == null) {
                availableStaff.add(staff);
            }
        }
        return availableStaff;
    }

    public int getStaffSkillAverage(StaffType staffType, Promotion promotion) {
        double total = 0;
        List<StaffMember> staffOfType = getStaff(staffType, promotion);
        for (StaffMember staff : staffOfType) {
            total += staff.getSkill();
        }
        return (int) Math.ceil(total / staffOfType.size());
    }

    public int getStaffPayrollForMonth(LocalDate date, Promotion promotion) {
        return contractManager.getStaffContracts().stream()
                .filter(contract ->
                        contract.isActive() && contract.getPromotion().getPromotionID() == promotion.getPromotionID())
                .reduce(0, (partialAgeResult, contract) -> partialAgeResult + contract.getMonthlyCost(), Integer::sum);
    }

}
