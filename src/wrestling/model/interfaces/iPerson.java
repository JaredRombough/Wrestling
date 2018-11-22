package wrestling.model.interfaces;

import wrestling.model.segmentEnum.Gender;

public interface iPerson {

    public iContract getContract();

    public Gender getGender();

    public void setName(String name);

    public void setShortName(String name);

}
