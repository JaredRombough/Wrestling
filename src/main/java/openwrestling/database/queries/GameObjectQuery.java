package openwrestling.database.queries;

import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;

public abstract class GameObjectQuery {
    public abstract QueryBuilder getQueryBuilder(ConnectionSource connectionSource) throws SQLException;
    public abstract Class sourceClass();
}
