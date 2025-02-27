import java.util.ArrayList;

public interface TaskManager {
    ArrayList<Task> getTasks();
    ArrayList<Epic> getEpics();
    ArrayList<Subtask> getSubtasks();
    ArrayList<Subtask> getSubTasksByEpic(Epic epic);
    void removeTasks();
    void removeEpics();
    void removeSubtasks();
    Task getTaskById(Integer id);
    Epic getEpicById(Integer id);
    Subtask getSubtaskById(Integer id);
    void addTask(Task task);
    void addEpic(Epic epic);
    void addSubtask(Subtask subtask);
    void updateTask(Task task);
    void updateEpic(Epic epic);
    void updateSubtask(Subtask subtask);
    void removeTask(Integer id);
    void removeEpic(Integer id);
    void removeSubtask(Integer id);
    public HistoryManager getHistoryManager();
}
