package managers;

import taskstructure.Epic;
import taskstructure.Subtask;
import taskstructure.Task;

import exceptions.NotFoundException;

import java.util.ArrayList;
import java.util.List;

public interface TaskManager {
    ArrayList<Task> getTasks();

    ArrayList<Epic> getEpics();

    ArrayList<Subtask> getSubtasks();

    ArrayList<Subtask> getSubTasksByEpic(Epic epic);

    void removeTasks();

    void removeEpics();

    void removeSubtasks();

    Task getTaskById(Integer id) throws NotFoundException;

    Epic getEpicById(Integer id) throws NotFoundException;

    Subtask getSubtaskById(Integer id) throws NotFoundException;

    void addTask(Task task) throws InMemoryTaskManager.IntersectionException;

    void addEpic(Epic epic);

    void addSubtask(Subtask subtask) throws InMemoryTaskManager.IntersectionException, NotFoundException;

    void updateTask(Task task) throws InMemoryTaskManager.IntersectionException;

    void updateEpic(Epic epic);

    void updateSubtask(Subtask subtask) throws InMemoryTaskManager.IntersectionException, NotFoundException;

    void removeTask(Integer id) throws NotFoundException;

    void removeEpic(Integer id) throws NotFoundException;

    void removeSubtask(Integer id) throws NotFoundException;

    HistoryManager getHistoryManager();

    List<Task> getPrioritizedTasks();

    boolean checkIntersections(Task task);
}


