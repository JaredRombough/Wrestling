package wrestling.model;

import java.time.LocalDate;

/**
 *
 * a record of a match to be kept by the workers who participate in it
 */
public class MatchRecord {
    
    private final String matchString;
    private final LocalDate matchDate;
    
    public MatchRecord(String string, LocalDate date) {
        matchString = string;
        matchDate = date;
    }

    /**
     * @return the matchString
     */
    public String getMatchString() {
        return matchString;
    }

    /**
     * @return the matchDate
     */
    public LocalDate getMatchDate() {
        return matchDate;
    }
    
}
