package wrestling.model.interfaces;

public interface iMatchRule {

    boolean nodq();

    String description();

    default int getStrikingModifier() {
        return 0;
    }

    default int getFlyingModifier() {
        return 0;
    }

    default int getWrestingModifier() {
        return 0;
    }

    default int getInjuryModifier() {
        return 0;
    }

}
