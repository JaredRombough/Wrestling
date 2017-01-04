package wrestling.model;

/**
 *
 * a record of a match to be kept by the workers who participate in it
 */
public class MatchRecord {
    
    private final String matchString;
    private final int matchDate;
    
    public MatchRecord(String string, int date) {
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
    public int getMatchDate() {
        return matchDate;
    }
    
}
