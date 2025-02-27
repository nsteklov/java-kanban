import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    List<Task> taskHistory = new ArrayList<>();

    @Override
    public void add(Task task) {
        if (taskHistory.size() == 10) {
            taskHistory.remove(0);
        }
        taskHistory.add(task.copy());
    }

    @Override
    public List<Task> getHistory() {
        List<Task> historyList = new ArrayList<>();
        for (int i = taskHistory.size(); i >= 1; i--) {
            historyList.add(taskHistory.get(i - 1));
        }
        return historyList;
    }
}
