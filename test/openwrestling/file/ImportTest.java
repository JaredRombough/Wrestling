package openwrestling.file;

import openwrestling.database.Database;
import openwrestling.model.gameObjects.Promotion;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ImportTest {

    private static final File TEST_DATA_FOLDER = new File(".\\test_data");
    private Import testImport;

    @Before
    public void setUp() {
        testImport = new Import();
    }

    @Test
    public void promotionsDat() {
        List<Promotion> promotions = testImport.promotionsDat(TEST_DATA_FOLDER);
        assertThat(promotions).isNotNull().hasSize(18);
        assertThat(promotions.get(0).getLevel()).isEqualTo(5);
        assertThat(promotions.get(8).getLevel()).isEqualTo(1);
        assertThat(promotions.get(5).getLevel()).isEqualTo(3);
        assertThat(promotions.get(16).getLevel()).isEqualTo(2);
        for (int i = 0; i < promotions.size(); i++) {
            Promotion promotion=promotions.get(i);
            assertThat(promotion.getImportKey())
                    .isEqualTo(testImport.getPromotionKeys().get(i));
            assertThat(promotion.getName()).isNotNull();
            assertThat(promotion.getLevel()).isNotNull();
            assertThat(promotion.getShortName()).isNotNull();
            assertThat(promotion.getPopularity()).isEqualTo(50);
        }
    }

}