package wrestling.model.manager;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import wrestling.model.Injury;
import wrestling.model.modelView.WorkerView;

public class InjuryManager {

    private final NewsManager newsManager;
    private final List<Injury> injuries = new ArrayList();

    public InjuryManager(NewsManager newsManager) {
        this.newsManager = newsManager;
    }

    public void dailyUpdate(LocalDate date) {
        for (Injury injury : injuries) {
            if (injury.getExpiryDate().equals(date)) {
                WorkerView worker = injury.getWorkerView();
                worker.setInjury(null);
            }
        }
        injuries.removeIf(injury -> injury.getExpiryDate().equals(date));
    }

    /**
     * @return the injuries
     */
    public List<Injury> getInjuries() {
        return injuries;
    }

    public void addInjury(Injury injury) {
        injuries.add(injury);
        newsManager.addNews(injury);
    }

}
