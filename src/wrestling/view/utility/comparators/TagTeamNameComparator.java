package wrestling.view.utility.comparators;

import java.util.Comparator;
import wrestling.model.TagTeam;

public class TagTeamNameComparator implements Comparator<TagTeam> {

    @Override
    public int compare(TagTeam o1, TagTeam o2) {
        if (o1 != null && o2 != null) {
            if (null != o1.getName() && null != o2.getName()) {
                return o1.getName().compareTo(o2.getName());
            }
        }
        return 0;
    }

    @Override
    public String toString() {
        return "Name";
    }

}
