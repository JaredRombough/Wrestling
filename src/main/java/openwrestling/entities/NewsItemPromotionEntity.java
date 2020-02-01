package openwrestling.entities;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@DatabaseTable(tableName = "news_item_promotions")
public class NewsItemPromotionEntity extends Entity {
    @DatabaseField(generatedId = true)
    long newsItemPromotionID;

    @DatabaseField(foreign = true)
    PromotionEntity promotionEntity;

    @DatabaseField(foreign = true)
    NewsItemEntity newsItemEntity;
}
