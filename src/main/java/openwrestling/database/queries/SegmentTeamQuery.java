package openwrestling.database.queries;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import lombok.NoArgsConstructor;
import openwrestling.entities.SegmentTeamEntity;
import openwrestling.entities.SegmentTeamWorkerEntity;
import openwrestling.entities.WorkerEntity;
import openwrestling.model.gameObjects.SegmentTeam;

import java.sql.SQLException;

@NoArgsConstructor
public class SegmentTeamQuery extends GameObjectQuery {

    @Override
    public Class sourceClass() {
        return SegmentTeam.class;
    }

    @Override
    public QueryBuilder getQueryBuilder(ConnectionSource connectionSource) throws SQLException {
        Dao<SegmentTeamEntity, String> segmentTeamDao = DaoManager.createDao(connectionSource, SegmentTeamEntity.class);
        Dao<SegmentTeamWorkerEntity, String> segmentTeamWorkerDao = DaoManager.createDao(connectionSource, SegmentTeamWorkerEntity.class);
        Dao<WorkerEntity, String> workerDao = DaoManager.createDao(connectionSource, WorkerEntity.class);

        QueryBuilder<SegmentTeamEntity, String> segmentTeamQb = segmentTeamDao.queryBuilder();
        QueryBuilder<SegmentTeamWorkerEntity, String> segmentTeamWorkerQb = segmentTeamWorkerDao.queryBuilder();
        QueryBuilder<WorkerEntity, String> workerQb = workerDao.queryBuilder();

        return segmentTeamQb.join(segmentTeamWorkerQb.join(workerQb)).distinct();
    }
}
