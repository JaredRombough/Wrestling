package openwrestling.database;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import ma.glasnost.orika.BoundMapperFacade;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import openwrestling.entities.ContractEntity;
import openwrestling.entities.Entity;
import openwrestling.entities.PromotionEntity;
import openwrestling.entities.RosterSplitEntity;
import openwrestling.entities.RosterSplitWorkerEntity;
import openwrestling.entities.StableEntity;
import openwrestling.entities.StableWorkerEntity;
import openwrestling.entities.TagTeamEntity;
import openwrestling.entities.TagTeamWorkerEntity;
import openwrestling.entities.TitleEntity;
import openwrestling.entities.TitleReignEntity;
import openwrestling.entities.TitleReignWorkerEntity;
import openwrestling.entities.WorkerEntity;
import openwrestling.model.gameObjects.Contract;
import openwrestling.model.gameObjects.GameObject;
import openwrestling.model.gameObjects.Promotion;
import openwrestling.model.gameObjects.RosterSplit;
import openwrestling.model.gameObjects.Stable;
import openwrestling.model.gameObjects.TagTeam;
import openwrestling.model.gameObjects.Title;
import openwrestling.model.gameObjects.TitleReign;
import openwrestling.model.gameObjects.Worker;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

public class Database {

    private static String dbUrl;

    private static MapperFactory getMapperFactory() {
        MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();
        mapperFactory.getConverterFactory().registerConverter(new LocalDateConverter());
        return mapperFactory;
    }

    public static Map<Class<? extends GameObject>, Class<? extends Entity>> daoClassMap = new HashMap<>() {{
        put(Promotion.class, PromotionEntity.class);
        put(Worker.class, WorkerEntity.class);
        put(Stable.class, StableEntity.class);
        put(RosterSplit.class, RosterSplitEntity.class);
        put(Contract.class, ContractEntity.class);
        put(TagTeam.class, TagTeamEntity.class);
        put(Title.class, TitleEntity.class);
        put(TitleReign.class, TitleReignEntity.class);
    }};

    public static String createNewDatabase(String fileName) {

        String url = "jdbc:sqlite:C:/temp/" + fileName + ".db";

        try (Connection conn = DriverManager.getConnection(url)) {
            if (conn != null) {
                DatabaseMetaData meta = conn.getMetaData();
                System.out.println("The driver name is " + meta.getDriverName());
                System.out.println("A new database has been created. " + url);
                dbUrl = url;
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

    public static void insertEntityList(List<? extends Entity> toInsert, ConnectionSource connectionSource) {
        if (toInsert.isEmpty()) {
            return;
        }

        try {
            Dao dao = DaoManager.createDao(connectionSource, toInsert.get(0).getClass());

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

    public static List selectList(GameObjectQuery gameObjectQuery) {
        try {
            ConnectionSource connectionSource = new JdbcConnectionSource(Database.dbUrl);
            MapperFacade mapper = Database.getMapperFactory().getMapperFacade();
            List<WorkerEntity> results = gameObjectQuery.getQueryBuilder(connectionSource).query();
            List<Worker> roster = new ArrayList<>();
            results.forEach(entity -> roster.add(mapper.map(entity, Worker.class)));
            return roster;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return List.of();
    }

    public static List selectAll(Class sourceClass) {
        try {
            Class<? extends Entity> targetClass = daoClassMap.get(sourceClass);
            ConnectionSource connectionSource = new JdbcConnectionSource(dbUrl);
            Dao dao = DaoManager.createDao(connectionSource, targetClass);

            MapperFacade mapper = getMapperFactory().getMapperFacade();
            List entities = dao.queryForAll();
            List targets = new ArrayList();

            entities.stream().forEach(entity -> targets.add(mapper.map(entity, sourceClass)));
            return targets;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static <T extends GameObject> T insertGameObject(GameObject gameObject) {
        return (T) insertList(List.of(gameObject)).get(0);
    }

    public static List<? extends GameObject> insertList(List<? extends GameObject> gameObjects) {
        if (gameObjects.isEmpty()) {
            return gameObjects;
        }

        try {
            ConnectionSource connectionSource = new JdbcConnectionSource(dbUrl);

            Class<? extends Entity> targetClass = daoClassMap.get(gameObjects.get(0).getClass());
            Class sourceClass = gameObjects.get(0).getClass();

            Dao dao = DaoManager.createDao(connectionSource, targetClass);

            BoundMapperFacade boundedMapper = getMapperFactory().getMapperFacade(gameObjects.get(0).getClass(), targetClass);

            List toInsert = gameObjects.stream().map(gameObject -> {
                Object entity = null;
                try {
                    entity = targetClass.getConstructor().newInstance();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (entity == null) {
                    return null;
                }

                for (Field field : entity.getClass().getDeclaredFields()) {
                    if (field.isAnnotationPresent(ForeignCollectionField.class)) {
                        try {

                            field.set(entity, dao.getEmptyForeignCollection(field.getName()));

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }
                Object result = boundedMapper.map(gameObject, entity);

                return result;
            })
                    .collect(Collectors.toList());

            MapperFacade mapper = getMapperFactory().getMapperFacade();
            List toReturn = new ArrayList();
            dao.callBatchTasks((Callable<Void>) () -> {
                for (Object object : toInsert) {
                    Entity entity = (Entity) object;
                    dao.create(entity);
                    insertEntityList(entity.childrenToInsert(), connectionSource);
                    toReturn.add(mapper.map(entity, sourceClass));
                }
                return null;
            });

            return toReturn;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private static void createTables(String url) {
        try {

            ConnectionSource connectionSource = new JdbcConnectionSource(url);

            List<Class> classes = List.of(
                    WorkerEntity.class,
                    PromotionEntity.class,
                    StableEntity.class,
                    StableWorkerEntity.class,
                    RosterSplitEntity.class,
                    RosterSplitWorkerEntity.class,
                    ContractEntity.class,
                    TagTeamEntity.class,
                    TagTeamWorkerEntity.class,
                    TitleEntity.class,
                    TitleReignEntity.class,
                    TitleReignWorkerEntity.class);

            for (Class entityClass : classes) {
                Dao dao = DaoManager.createDao(connectionSource, entityClass);
                TableUtils.dropTable(dao, true);
                TableUtils.createTable(connectionSource, entityClass);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
