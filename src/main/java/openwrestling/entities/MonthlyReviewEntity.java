package openwrestling.entities;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@DatabaseTable(tableName = "monthly_reviews")
public class MonthlyReviewEntity extends Entity {
    @DatabaseField(generatedId = true)
    private long monthlyReviewID;

    @DatabaseField
    private long funds;

    @DatabaseField
    private long popularity;

    @DatabaseField
    private long level;

    @DatabaseField
    private Date date;
}
