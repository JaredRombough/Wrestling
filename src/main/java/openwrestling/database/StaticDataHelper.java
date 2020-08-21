package openwrestling.database;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import openwrestling.entities.MatchRulesEntity;

import java.io.IOException;
import java.io.InputStream;

public class StaticDataHelper {

    public void insertStaticData(ConnectionSource connectionSource) {
        try {
            Dao dao = DaoManager.createDao(connectionSource, MatchRulesEntity.class);
            dao.executeRaw(readFile());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private String readFile() {
        byte[] encoded;

        try (InputStream in = getClass().getResourceAsStream("/static_data.sql")) {
            encoded = in.readAllBytes();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        return new String(encoded, java.nio.charset.StandardCharsets.UTF_8);
    }

}
