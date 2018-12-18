package wrestling.model.interfaces;

import java.util.List;
import wrestling.model.modelView.PromotionView;
import wrestling.model.segmentEnum.Gender;

public interface iPerson {

    public iContract getContract();

    public iContract getContract(PromotionView promotion);

    public List<? extends iContract> getContracts();

    public Gender getGender();

    public String getName();

    public void setName(String name);

    public void setShortName(String name);

}
