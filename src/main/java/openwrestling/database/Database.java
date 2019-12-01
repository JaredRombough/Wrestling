package openwrestling.database;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import ma.glasnost.orika.BoundMapperFacade;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import openwrestling.entities.BankAccountEntity;
import openwrestling.entities.ContractEntity;
import openwrestling.entities.Entity;
import openwrestling.entities.EntourageMemberEntity;
import openwrestling.entities.EventEntity;
import openwrestling.entities.EventTemplateEntity;
import openwrestling.entities.MatchTitleEntity;
import openwrestling.entities.MoraleRelationshipEntity;
import openwrestling.entities.PromotionEntity;
import openwrestling.entities.RosterSplitEntity;
import openwrestling.entities.RosterSplitWorkerEntity;
import openwrestling.entities.SegmentEntity;
import openwrestling.entities.SegmentTeamEntity;
import openwrestling.entities.SegmentTeamEntourageEntity;
import openwrestling.entities.SegmentTeamWorkerEntity;
import openwrestling.entities.StableEntity;
import openwrestling.entities.StableWorkerEntity;
import openwrestling.entities.StaffContractEntity;
import openwrestling.entities.StaffMemberEntity;
import openwrestling.entities.TagTeamEntity;
import openwrestling.entities.TagTeamWorkerEntity;
import openwrestling.entities.TitleEntity;
import openwrestling.entities.TitleReignEntity;
import openwrestling.entities.TitleReignWorkerEntity;
import openwrestling.entities.TransactionEntity;
import openwrestling.entities.WorkerEntity;
import openwrestling.entities.WorkerRelationshipEntity;
import openwrestling.model.gameObjects.Contract;
import openwrestling.model.gameObjects.EntourageMember;
import openwrestling.model.gameObjects.Event;
import openwrestling.model.gameObjects.EventTemplate;
import openwrestling.model.gameObjects.GameObject;
import openwrestling.model.gameObjects.MoraleRelationship;
import openwrestling.model.gameObjects.Promotion;
import openwrestling.model.gameObjects.RosterSplit;
import openwrestling.model.gameObjects.Stable;
import openwrestling.model.gameObjects.StaffContract;
import openwrestling.model.gameObjects.StaffMember;
import openwrestling.model.gameObjects.TagTeam;
import openwrestling.model.gameObjects.Title;
import openwrestling.model.gameObjects.TitleReign;
import openwrestling.model.gameObjects.Worker;
import openwrestling.model.gameObjects.WorkerRelationship;
import openwrestling.model.gameObjects.financial.BankAccount;
import openwrestling.model.gameObjects.financial.Transaction;
import openwrestling.model.modelView.Segment;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
    private static Logger logger = LogManager.getLogger();

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
        put(EventTemplate.class, EventTemplateEntity.class);
        put(StaffMember.class, StaffMemberEntity.class);
        put(StaffContract.class, StaffContractEntity.class);
        put(WorkerRelationship.class, WorkerRelationshipEntity.class);
        put(MoraleRelationship.class, MoraleRelationshipEntity.class);
        put(BankAccount.class, BankAccountEntity.class);
        put(Transaction.class, TransactionEntity.class);
        put(EntourageMember.class, EntourageMemberEntity.class);
        put(Event.class, EventEntity.class);
        put(Segment.class, SegmentEntity.class);
    }};

    public static String createNewDatabase(String fileName) {

        String url = "jdbc:sqlite:C:/temp/" + fileName + ".db";

        try (Connection conn = DriverManager.getConnection(url)) {
            if (conn != null) {
                DatabaseMetaData meta = conn.getMetaData();
                logger.log(Level.DEBUG, "The driver name is " + meta.getDriverName());
                logger.log(Level.DEBUG, "A new database has been created. " + url);
                dbUrl = url;
            }

        } catch (SQLException e) {
            logger.log(Level.ERROR, ExceptionUtils.getStackTrace(e));
            throw new RuntimeException(e);
        }

        createTables(url);

        return url;
    }


    public static Connection connect(String url) {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(url);
        } catch (SQLException e) {
            logger.log(Level.ERROR, ExceptionUtils.getStackTrace(e));
            throw new RuntimeException(e);
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                logger.log(Level.ERROR, ExceptionUtils.getStackTrace(e));
                throw new RuntimeException(e);
            }
        }
        return connection;
    }

    private static void insertOrUpdateChildList(List<? extends Entity> toInsert, ConnectionSource connectionSource) {
        if (toInsert.isEmpty()) {
            return;
        }

        try {
            Dao dao = DaoManager.createDao(connectionSource, toInsert.get(0).getClass());

            dao.callBatchTasks((Callable<Void>) () -> {
                for (Entity entity : toInsert) {
                    if (isCreate(entity)) {
                        dao.create(entity);
                    } else {
                        dao.update(entity);
                    }
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

    public static <T> List selectAll(Class sourceClass) {
        try {
            Class<? extends Entity> targetClass = daoClassMap.get(sourceClass);
            ConnectionSource connectionSource = new JdbcConnectionSource(dbUrl);
            Dao dao = DaoManager.createDao(connectionSource, targetClass);

            MapperFacade mapper = getMapperFactory().getMapperFacade();
            List entities = dao.queryForAll();
            List targets = new ArrayList();

            return entitiesToGameObjects(entities, sourceClass);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static <T extends GameObject> T insertGameObject(GameObject gameObject) {

        return (T) insertOrUpdateList(List.of(gameObject)).get(0);

    }

    public static <T extends GameObject> List<T> insertOrUpdateList(List<T> gameObjects) {
        try {
            if (gameObjects.isEmpty()) {
                return gameObjects;
            }
            logger.log(Level.DEBUG, String.format("insertOrUpdateList size %d class %s",
                    gameObjects.size(),
                    gameObjects.get(0).getClass())
            );
            List<? extends Entity> entities = gameObjectsToEntities(gameObjects);
            List<? extends Entity> saved = insertOrUpdateEntityList(entities);
            return entitiesToGameObjects(saved, gameObjects.get(0).getClass()).stream().map(o -> (T) o).collect(Collectors.toList());
        } catch (Exception e) {
            logger.log(Level.ERROR, ExceptionUtils.getStackTrace(e));
            throw e;
        }
    }

    public static <T extends Entity> List<T> insertOrUpdateEntityList(List<T> entities) {
        if (entities.isEmpty()) {
            return entities;
        }

        try {
            ConnectionSource connectionSource = new JdbcConnectionSource(dbUrl);

            Class<? extends Entity> targetClass = entities.get(0).getClass();

            Dao dao = DaoManager.createDao(connectionSource, targetClass);

            dao.callBatchTasks((Callable<Void>) () -> {
                for (Entity entity : entities) {
                    if (isCreate(entity)) {
                        dao.create(entity);
                    } else {
                        dao.update(entity);
                    }
                    insertOrUpdateChildList(entity.childrenToInsert(), connectionSource);
                }
                return null;
            });

            return entities;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static List<? extends Entity> gameObjectsToEntities(List<? extends GameObject> gameObjects) {
        if (gameObjects.isEmpty()) {
            return List.of();
        }

        Class<? extends Entity> targetClass = daoClassMap.get(gameObjects.get(0).getClass());

        BoundMapperFacade boundedMapper = getMapperFactory().getMapperFacade(gameObjects.get(0).getClass(), targetClass);

        return gameObjects.stream()
                .map(gameObject -> {
                    Object entity = null;
                    try {
                        entity = targetClass.getConstructor().newInstance();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (entity == null) {
                        return null;
                    }
//
//                    for (Field field : entity.getClass().getDeclaredFields()) {
//                        if (field.isAnnotationPresent(ForeignCollectionField.class)) {
//                            try {
//
//                                field.set(entity, dao.getEmptyForeignCollection(field.getName()));
//
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//
//                        }
//                    }
                    Object result = boundedMapper.map(gameObject, entity);

                    return result;
                })
                .map(targetClass::cast)
                .collect(Collectors.toList());
    }

    public static List<? extends GameObject> entitiesToGameObjects(List<? extends Entity> entities, Class<? extends GameObject> targetClass) {
        if (entities.isEmpty()) {
            return List.of();
        }

        BoundMapperFacade boundedMapper = getMapperFactory().getMapperFacade(entities.get(0).getClass(), targetClass);

        return entities.stream()
                .map(entity -> {
                    Object gameObject = null;
                    try {
                        gameObject = targetClass.getConstructor().newInstance();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (gameObject == null) {
                        return null;
                    }
//
//                    for (Field field : entity.getClass().getDeclaredFields()) {
//                        if (field.isAnnotationPresent(ForeignCollectionField.class)) {
//                            try {
//
//                                field.set(entity, dao.getEmptyForeignCollection(field.getName()));
//
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//
//                        }
//                    }
                    Object result = boundedMapper.map(entity, gameObject);

                    return result;
                })
                .map(targetClass::cast)
                .collect(Collectors.toList());
    }


    private static boolean isCreate(Entity entity) {
        return List.of(entity.getClass().getDeclaredFields()).stream().anyMatch(field -> {
                    try {
                        boolean isCreate = false;
                        if (field.isAnnotationPresent(DatabaseField.class) &&
                                field.getAnnotation(DatabaseField.class).generatedId()) {
                            field.setAccessible(true);
                            isCreate = field.getLong(entity) == 0;
                            field.setAccessible(false);
                        }
                        return isCreate;
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    return true;
                }
        );
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
                    TitleReignWorkerEntity.class,
                    EventTemplateEntity.class,
                    StaffMemberEntity.class,
                    StaffContractEntity.class,
                    WorkerRelationshipEntity.class,
                    MoraleRelationshipEntity.class,
                    BankAccountEntity.class,
                    TransactionEntity.class,
                    EntourageMemberEntity.class,
                    EventEntity.class,
                    SegmentEntity.class,
                    SegmentTeamEntity.class,
                    SegmentTeamEntourageEntity.class,
                    SegmentTeamWorkerEntity.class,
                    MatchTitleEntity.class);

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
