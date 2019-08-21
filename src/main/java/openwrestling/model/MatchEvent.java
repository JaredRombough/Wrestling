package openwrestling.model;

public class MatchEvent {

    private final Event event;
    private final Match match;
    private int sequenceNumber;

    public MatchEvent(Match match, Event event) {
        this.match = match;
        this.event = event;
    }

    /**
     * @return the event
     */
    public Event getEvent() {
        return event;
    }

    /**
     * @return the match
     */
    public Match getMatch() {
        return match;
    }

}
