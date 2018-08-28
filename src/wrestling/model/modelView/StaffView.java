package wrestling.model.modelView;

import wrestling.model.SegmentItem;
import wrestling.model.segmentEnum.Gender;
import wrestling.model.segmentEnum.StaffType;

public class StaffView implements SegmentItem {

    private String name;
    private Gender gender;
    private int age;
    private int skill;
    private int behaviour;
    private StaffType staffType;
    private String imageString;

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the gender
     */
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
    public String getImageString() {
        return imageString;
    }

    /**
     * @param imageString the imageString to set
     */
    public void setImageString(String imageString) {
        this.imageString = imageString;
    }

}
