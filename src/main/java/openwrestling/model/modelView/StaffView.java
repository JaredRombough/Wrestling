package openwrestling.model.modelView;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import openwrestling.model.SegmentItem;
import openwrestling.model.StaffContract;
import openwrestling.model.gameObjects.Promotion;
import openwrestling.model.interfaces.iContract;
import openwrestling.model.interfaces.iPerson;
import openwrestling.model.segmentEnum.Gender;
import openwrestling.model.segmentEnum.StaffType;

public class StaffView implements Serializable, SegmentItem, iPerson {

    private String name;
    private Gender gender;
    private int age;
    private int skill;
    private int behaviour;
    private StaffType staffType;
    private String imageString;
    private StaffContract staffContract;

    /**
     * @return the name
     */
    @Override
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return getName();
    }

    /**
     * @param name the name to set
     */
    @Override
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the gender
     */
    @Override
    public Gender getGender() {
        return gender;
    }

    /**
     * @param gender the gender to set
     */
    public void setGender(Gender gender) {
        this.gender = gender;
    }

    /**
     * @return the age
     */
    @Override
    public int getAge() {
        return age;
    }

    /**
     * @param age the age to set
     */
    public void setAge(int age) {
        this.age = age;
    }

    /**
     * @return the skill
     */
    public int getSkill() {
        return skill;
    }

    /**
     * @param skill the skill to set
     */
    public void setSkill(int skill) {
        this.skill = skill;
    }

    /**
     * @return the behaviour
     */
    @Override
    public int getBehaviour() {
        return behaviour;
    }

    /**
     * @param behaviour the behaviour to set
     */
    public void setBehaviour(int behaviour) {
        this.behaviour = behaviour;
    }

    /**
     * @return the staffType
     */
    public StaffType getStaffType() {
        return staffType;
    }

    /**
     * @param staffType the staffType to set
     */
    public void setStaffType(StaffType staffType) {
        this.staffType = staffType;
    }

    /**
     * @return the imageString
     */
    @Override
    public String getImageString() {
        return imageString;
    }

    /**
     * @param imageString the imageString to set
     */
    public void setImageString(String imageString) {
        this.imageString = imageString;
    }

    /**
     * @return the staffContract
     */
    public StaffContract getStaffContract() {
        return staffContract;
    }

    /**
     * @param staffContract the staffContract to set
     */
    public void setStaffContract(StaffContract staffContract) {
        this.staffContract = staffContract;
    }

    @Override
    public iContract getContract() {
        return staffContract;
    }

    @Override
    public void setShortName(String name) {
        // no short name
    }

    @Override
    public String getShortName() {
        String[] splitName = name.split(" ");
        return splitName[splitName.length - 1];
    }

    @Override
    public iContract getContract(Promotion promotion) {
        return staffContract != null && staffContract.getPromotion().equals(promotion)
                ? staffContract : null;
    }

    @Override
    public List<? extends iContract> getContracts() {
        return staffContract != null ? Collections.singletonList(staffContract) : Collections.emptyList();
    }

}
