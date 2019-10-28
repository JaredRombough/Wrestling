package openwrestling;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class Logging {
    protected Logger logger = LogManager.getLogger(getClass());
}
