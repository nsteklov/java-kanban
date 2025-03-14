import java.util.List;

public interface HistoryManager {

    void add(Task task);

    void removeFromHistory(int id);

    List<Task> getHistory();
}
