package openwrestling.model.modelView;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import openwrestling.entities.WorkerEntity;

import java.util.Collection;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@DatabaseTable(tableName = "entourages")
public class Entourage {

    @DatabaseField(generatedId = true)
    private long entourageID;

    @ForeignCollectionField
    private Collection<WorkerEntity> followers;
}
