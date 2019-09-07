package openwrestling.database;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import lombok.AllArgsConstructor;
import openwrestling.entities.ContractEntity;
import openwrestling.entities.WorkerEntity;
import openwrestling.model.gameObjects.Promotion;

import java.sql.SQLException;

@AllArgsConstructor
public class FullRosterQuery extends GameObjectQuery {

    private Promotion promotion;

    @Override
    public QueryBuilder getQueryBuilder(ConnectionSource connectionSource) throws SQLException {
        Dao<WorkerEntity, String> workerDao = DaoManager.createDao(connectionSource, WorkerEntity.class);
        Dao<ContractEntity, Integer> contractDao = DaoManager.createDao(connectionSource, ContractEntity.class);

        QueryBuilder<ContractEntity, Integer> contractQb = contractDao.queryBuilder();
        contractQb.where().eq("promotion_id", promotion.getPromotionID());
        QueryBuilder<WorkerEntity, String> workerQb = workerDao.queryBuilder();

        return workerQb.join(contractQb);
    }
}
