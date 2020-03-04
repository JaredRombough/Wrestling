package openwrestling.manager;

import lombok.Getter;
import openwrestling.Logging;
import openwrestling.database.Database;

public abstract class GameObjectManager extends Logging {

    @Getter
    private Database database;

    public GameObjectManager(Database database) {
        this.database = database;
    }

    public void selectData() {
    }

}
