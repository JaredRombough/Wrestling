package openwrestling.model;

public class MatchTitle {
    private final Title title;
    private final Match match;
    
    public MatchTitle(Match match, Title title) {
        this.match = match;
        this.title = title;
    }

    /**
     * @return the title
     */
    public Title getTitle() {
        return title;
    }

    /**
     * @return the match
     */
    public Match getMatch() {
        return match;
    }

}
