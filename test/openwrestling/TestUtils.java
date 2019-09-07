package openwrestling;

import openwrestling.model.gameObjects.Promotion;
import org.apache.commons.lang3.RandomStringUtils;

public class TestUtils {

    public static Promotion randomPromotion() {
        return Promotion.builder()
                .name(RandomStringUtils.random(10))
                .level(2)
                .shortName(RandomStringUtils.random(3))
                .popularity(56)
                .build();
    }
}
