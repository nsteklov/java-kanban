import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private int id = 0;
    private HashMap<Integer, Task> tasks;
    private HashMap<Integer, Epic> epics;
    private HashMap<Integer, Subtask> subtasks;

    public TaskManager() {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
    }

    public ArrayList<Task> getTasks() {
        ArrayList<Task> listOfTasks = new ArrayList<>();
        for (Task task : tasks.values()) {
            listOfTasks.add(task);
        }
        return listOfTasks;
    }

    public ArrayList<Epic> getEpics() {
        ArrayList<Epic> listOfEpics = new ArrayList<>();
        for (Epic epic : epics.values()) {
            listOfEpics.add(epic);
        }
        return listOfEpics;
    }

    public ArrayList<Subtask> getSubtasks() {
        ArrayList<Subtask> listOfSubtasks = new ArrayList<>();
        for (Subtask subtask : subtasks.values()) {
            listOfSubtasks.add(subtask);
        }
        return listOfSubtasks;
    }

    public ArrayList<Subtask> getSubTasksByEpic(Epic epic) {
        ArrayList<Subtask> listOfSubtasks = new ArrayList<>();
        if (epic.getIDOfSubtasks() == null) {
            return listOfSubtasks;
        }
        for (Integer IdOfSubtask : epic.getIDOfSubtasks()) {
            Subtask subtask = getSubtaskById(IdOfSubtask);
            if (subtask != null) {
                listOfSubtasks.add(subtask);
            }
        }
        return listOfSubtasks;
    }

    public void removeTasks() {
        tasks.clear();
    }

    public void removeEpics() {
        epics.clear();
        removeSubtasks();
    }

    public void removeSubtasks() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.removeSubtasks();
            updateEpicStatus(epic);
        }
    }

    public Task getTaskById(Integer id) {
        return tasks.get(id);
    }

    public Epic getEpicById(Integer id) {
        return epics.get(id);
    }

    public Subtask getSubtaskById(Integer id) {
        return subtasks.get(id);
    }

    public void addTask(Task task) {
        id = generateId();
        task.setId(id);
        task.setStatus(Status.NEW);
        tasks.put(task.getId(), task);
    }

    public void addEpic(Epic epic) {
        id = generateId();
        epic.setId(id);
        epic.setStatus(Status.NEW);
        epics.put(epic.getId(), epic);
    }

    public void addSubtask(Subtask subtask) {
        id = generateId();
        subtask.setId(id);
        subtask.setStatus(Status.NEW);
        subtasks.put(subtask.getId(), subtask);
        Integer IDOfSubtask = subtask.getId();
        Integer IDOfEpic = subtask.getIDOfEpic();
        Epic epic = getEpicById(IDOfEpic);
        if (epic != null) {
            epic.addIDOfSubtask(IDOfSubtask);
            updateEpicStatus(epic);
        }
    }

    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    public void updateEpic(Epic epic) {
        updateEpicStatus(epic);
        epics.put(epic.getId(), epic);
    }

    public void updateSubtask(Subtask subtask) {
        Epic epic = getEpicById(subtask.getIDOfEpic());
        if (epic != null) {
            updateEpicStatus(epic);
        }
        subtasks.put(subtask.getId(), subtask);
    }

    public void removeTask(Integer id) {
        tasks.remove(id);
    }

    public void removeEpic(Integer id) {
        Epic epic = getEpicById(id);
        if (epic != null) {
            ArrayList<Subtask> listOfSubtasks = getSubTasksByEpic(epic);
            for (Subtask subtask : listOfSubtasks) {
                subtasks.remove(subtask.getId());
            }
        }
        epics.remove(id);
    }

    public void removeSubtask(Integer id) {
        Subtask subtask = getSubtaskById(id);
        if (subtask != null) {
            Epic epic = getEpicById(subtask.getIDOfEpic());
            if (epic != null) {
                epic.removeIDOfSubtask(id);
                updateEpicStatus(epic);
            }
        }
        subtasks.remove(id);
    }

    private int generateId() {
        id++;
        return id;
    }

    private void updateEpicStatus(Epic epic) {
        if (epic == null) {
            return;
        }
        ArrayList<Subtask> subTasks =  getSubTasksByEpic(epic);
        boolean allSubtasksNew = true;
        boolean allSubtasksDone = true;
        for (Subtask subTask : subTasks) {
            if (subTask.getStatus() != Status.NEW) {
                allSubtasksNew = false;
            }
            if (subTask.getStatus() != Status.DONE) {
                allSubtasksDone = false;
            }
            if (!allSubtasksNew && !allSubtasksDone) {
                break;
            }
        }
        if (allSubtasksNew) {
            epic.setStatus(Status.NEW);
        } else if (allSubtasksDone) {
            epic.setStatus(Status.DONE);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }
}
