package openwrestling.file;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import openwrestling.MainApp;
import openwrestling.entities.Entity;
import openwrestling.entities.PromotionEntity;
import openwrestling.entities.WorkerEntity;
import openwrestling.model.gameObjects.GameObject;
import openwrestling.model.gameObjects.Promotion;
import openwrestling.model.gameObjects.Worker;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

public class Database {

    private static Map<Class<? extends GameObject>, Class<? extends Entity>> classMap = new HashMap<>() {{
        put(Promotion.class, PromotionEntity.class);
        put(Worker.class, WorkerEntity.class);
    }};

    public static String createNewDatabase(String fileName) {

        String url = "jdbc:sqlite:C:/temp/" + fileName + ".db";

        try (Connection conn = DriverManager.getConnection(url)) {
            if (conn != null) {
                DatabaseMetaData meta = conn.getMetaData();
                System.out.println("The driver name is " + meta.getDriverName());
                System.out.println("A new database has been created.");
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        createTables(url);

        return url;
    }


    public static Connection connect(String url) {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
        return connection;
    }

    public static void createEntityList(List<? extends GameObject> gameObjects) {
        if (gameObjects.isEmpty()) {
            return;
        }

        ConnectionSource connectionSource = null;
        try {
            connectionSource = new JdbcConnectionSource(MainApp.dbURL);

            Class<? extends Entity> targetClass = classMap.get(gameObjects.get(0).getClass());

            Dao dao = DaoManager.createDao(connectionSource, targetClass);
            MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();
            MapperFacade mapper = mapperFactory.getMapperFacade();

            List<? extends Entity> toInsert = gameObjects.stream().map(gameObject ->
                    mapper.map(gameObject, targetClass))
                    .collect(Collectors.toList());

            dao.callBatchTasks((Callable<Void>) () -> {
                for (Entity entity : toInsert) {
                    dao.create(entity);
                }
                return null;
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void createTables(String url) {
        try {
            ConnectionSource connectionSource = new JdbcConnectionSource(url);
            Dao<WorkerEntity, String> workerDao = DaoManager.createDao(connectionSource, WorkerEntity.class);
            Dao<PromotionEntity, String> promotionDao = DaoManager.createDao(connectionSource, PromotionEntity.class);
            TableUtils.dropTable(workerDao, true);
            TableUtils.dropTable(promotionDao, true);
            TableUtils.createTable(connectionSource, WorkerEntity.class);
            TableUtils.createTable(connectionSource, PromotionEntity.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
