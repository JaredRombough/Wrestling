package openwrestling.database;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import openwrestling.entities.MatchRulesEntity;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

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
        URL res = getClass().getClassLoader().getResource("static_data.sql");

        byte[] encoded;

        try {
            assert res != null;
            encoded = Files.readAllBytes(Paths.get(res.toURI()));
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return new String(encoded, java.nio.charset.StandardCharsets.UTF_8);
    }

}
