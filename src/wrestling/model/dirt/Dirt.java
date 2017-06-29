package wrestling.model.dirt;

import java.time.LocalDate;
import java.util.List;
import wrestling.model.Worker;

public interface Dirt {

    public void setDate(LocalDate date);
    public LocalDate getDate();
    public List<Worker> getWorkers();


}
