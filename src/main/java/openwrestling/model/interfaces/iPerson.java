package openwrestling.model.interfaces;

import openwrestling.model.gameObjects.Promotion;
import openwrestling.model.segment.constants.Gender;

import java.util.List;

public interface iPerson {

    public iContract getContract();

    public iContract getContract(Promotion promotion);

    public List<? extends iContract> getContracts();

    public Gender getGender();

    public String getName();

    public void setName(String name);

    public void setShortName(String name);

}
