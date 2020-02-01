package openwrestling.entities;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@DatabaseTable(tableName = "news_items")
public class NewsItemEntity extends Entity {

    @DatabaseField(generatedId = true)
    private long newsItemID;

    @DatabaseField
    private Date date;

    @DatabaseField
    private String summary;

    @DatabaseField
    private String headline;

    @ForeignCollectionField(eager = true)
    public ForeignCollection<NewsItemWorkerEntity> newsItemWorkers;

    @ForeignCollectionField(eager = true)
    public ForeignCollection<NewsItemPromotionEntity> newsItemPromotions;

    private Collection<WorkerEntity> workers;
    private Collection<PromotionEntity> promotions;

    public List<? extends Entity> childrenToInsert() {
        if (CollectionUtils.isNotEmpty(workers)) {
            return workers.stream().map(worker ->
                    NewsItemWorkerEntity.builder()
                            .workerEntity(worker)
                            .newsItemEntity(this)
                            .build()
            ).collect(Collectors.toList());
        }
        return List.of();
    }

    public List<? extends Entity> childrenToInsert2() {
        if (CollectionUtils.isNotEmpty(promotions)) {
            return promotions.stream().map(promotion ->
                    NewsItemPromotionEntity.builder()
                            .promotionEntity(promotion)
                            .newsItemEntity(this)
                            .build()
            ).collect(Collectors.toList());
        }
        return List.of();
    }


}
