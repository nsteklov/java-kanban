package managers;

import taskstructure.Epic;
import taskstructure.Status;
import taskstructure.Subtask;
import taskstructure.Task;
import exceptions.NotFoundException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.time.Duration;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    private int id = 0;
    private HashMap<Integer, Task> tasks;
    private HashMap<Integer, Epic> epics;
    private HashMap<Integer, Subtask> subtasks;
    private HistoryManager historyManager;
    private TreeSet<Task> prioritizedTasks;

    public class IntersectionException extends Exception {
        public IntersectionException(final String message) {
            super(message);
        }
    }

    public InMemoryTaskManager() {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
        historyManager = Managers.getDefaultHistory();
        prioritizedTasks = new TreeSet<>((o1, o2) -> {
            if (o1.getStartTime().isAfter(o2.getStartTime())) {
                return 1;
            } else if (o1.getStartTime().isBefore(o2.getStartTime())) {
                return -1;
            } else {
                return 0;
            }
        });
    }

    @Override
    public ArrayList<Task> getTasks() {
        List<Task> listOfTasks = tasks.values().stream()
                .collect(Collectors.toList());
        return new ArrayList<>(listOfTasks);
    }

    @Override
    public ArrayList<Epic> getEpics() {
        List<Epic> listOfEpics = epics.values().stream()
                .collect(Collectors.toList());
        return new ArrayList<>(listOfEpics);
    }

    @Override
    public ArrayList<Subtask> getSubtasks() {
        List<Subtask> listOfEpics = subtasks.values().stream()
                .collect(Collectors.toList());
        return new ArrayList<>(listOfEpics);
    }

    @Override
    public ArrayList<Subtask> getSubTasksByEpic(Epic epic) {
        if (epic.getIDOfSubtasks() == null) {
            return new ArrayList<>();
        }
            List<Subtask> listOfSubtasks = epic.getIDOfSubtasks().stream()
                    .map(iDOfSubtask -> {
                        try {
                            return getSubtaskById(iDOfSubtask);
                        } catch (NotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .filter(subtask -> subtask != null)
                    .collect(Collectors.toList());
            return new ArrayList<>(listOfSubtasks);
    }

    @Override
    public void removeTasks() {
        for (Task task : tasks.values()) {
            prioritizedTasks.remove(task);
        }
        for (int id : tasks.keySet()) {
            historyManager.remove(id);
        }
        tasks.clear();
    }

    @Override
    public void removeEpics() {
        for (int id : epics.keySet()) {
            historyManager.remove(id);
        }
        epics.clear();
        removeSubtasks();
    }

    @Override
    public void removeSubtasks() {
        for (int id : subtasks.keySet()) {
            historyManager.remove(id);
        }
        for (Subtask subtask : subtasks.values()) {
            prioritizedTasks.remove(subtask);
        }
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.removeSubtasks();
            updateEpicStatus(epic);
            updateEpicDuration(epic);
        }
    }

    @Override
    public Task getTaskById(Integer id) throws NotFoundException {
        Task task = tasks.get(id);
        if (task == null) {
            throw new NotFoundException("Задача не найдена", id);
        } else {
            historyManager.add(task);
        }
        return task;
    }

    @Override
    public Epic getEpicById(Integer id) throws NotFoundException {
        Epic epic = epics.get(id);
        if (epic != null) {
            historyManager.add(epic);
        } else {
            throw new NotFoundException("Эпик не найден", id);
        }
        return epic;
    }

    @Override
    public Subtask getSubtaskById(Integer id) throws NotFoundException {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            historyManager.add(subtask);
        } else {
            throw new NotFoundException("Подзадача не найдена", id);
        }
        return subtask;
    }

    @Override
    public void addTask(Task task) throws IntersectionException {
        Boolean intersectionsFound = tasks.values().stream()
                .anyMatch(currentTask -> checkIntersection(currentTask, task));
        if (intersectionsFound) {
            throw new IntersectionException("Обнаружены пересечения задач");
        }
        id = generateId();
        task.setId(id);
        task.setStatus(Status.NEW);
        tasks.put(task.getId(), task);
        if (task.getStartTime() != null) {
            prioritizedTasks.add(task);
        }
    }

    @Override
    public void addEpic(Epic epic) {
        id = generateId();
        epic.setId(id);
        epic.setStatus(Status.NEW);
        epic.setDuration(Duration.ofMinutes(0));
        epics.put(epic.getId(), epic);
    }

    @Override
    public void addSubtask(Subtask subtask) throws IntersectionException, NotFoundException {
        Boolean intersectionsFound = subtasks.values().stream()
                .anyMatch(currentTask -> checkIntersection(currentTask, subtask));
        if (intersectionsFound) {
            throw new IntersectionException("Обнаружены пересечения задач");
        }
        id = generateId();
        subtask.setId(id);
        subtask.setStatus(Status.NEW);
        subtasks.put(subtask.getId(), subtask);
        if (subtask.getStartTime() != null) {
            prioritizedTasks.add(subtask);
        }
        Integer iDOfSubtask = subtask.getId();
        Integer iDOfEpic = subtask.getIDOfEpic();
        Epic epic = getEpicById(iDOfEpic);
        if (epic != null) {
            epic.addIDOfSubtask(iDOfSubtask);
            updateEpicStatus(epic);
            updateEpicDuration(epic);
        } else {
            throw new NotFoundException("Эпик не найден", iDOfEpic);
        }
    }

    @Override
    public void updateTask(Task task) throws IntersectionException {
        Boolean intersectionsFound = tasks.values().stream()
                .anyMatch(currentTask -> checkIntersection(currentTask, task));
        if (intersectionsFound) {
            throw new IntersectionException("Обнаружены пересечения задач");
        }
        tasks.put(task.getId(), task);
        if (task.getStartTime() != null) {
            prioritizedTasks.add(task);
        } else {
            prioritizedTasks.remove(task);
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        updateEpicStatus(epic);
        updateEpicDuration(epic);
        epics.put(epic.getId(), epic);
    }

    @Override
    public void updateSubtask(Subtask subtask) throws IntersectionException, NotFoundException {
        Boolean intersectionsFound = subtasks.values().stream()
                .anyMatch(currentTask -> checkIntersection(currentTask, subtask));
        if (intersectionsFound) {
            throw new IntersectionException("Обнаружены пересечения задач");
        }
        subtasks.put(subtask.getId(), subtask);
        Epic epic = getEpicById(subtask.getIDOfEpic());
        if (epic != null) {
            updateEpic(epic);
        } else {
            throw new NotFoundException("Эпик не найден", subtask.getIDOfEpic());
        }
        if (subtask.getStartTime() != null) {
            prioritizedTasks.add(subtask);
        } else {
            prioritizedTasks.remove(subtask);
        }
    }

    @Override
    public void removeTask(Integer id) throws NotFoundException {
        Task task = getTaskById(id);
        if (task == null) {
            throw new NotFoundException("Задача не найдена", id);
        }
        prioritizedTasks.remove(task);
        historyManager.remove(id);
        tasks.remove(id);
    }

    @Override
    public void removeEpic(Integer id) throws NotFoundException {
        Epic epic = getEpicById(id);
        if (epic == null) {
            throw new NotFoundException("Эпик не найден", id);
        }
        if (epic != null) {
            ArrayList<Subtask> listOfSubtasks = getSubTasksByEpic(epic);
            for (Subtask subtask : listOfSubtasks) {
                prioritizedTasks.remove(subtask);
                historyManager.remove(subtask.getId());
                subtasks.remove(subtask.getId());
            }
        }
        historyManager.remove(id);
        epics.remove(id);
    }

    @Override
    public void removeSubtask(Integer id) throws NotFoundException {
        Subtask subtask = getSubtaskById(id);
        if (subtask != null) {
            Epic epic = getEpicById(subtask.getIDOfEpic());
            if (epic != null) {
                epic.removeIDOfSubtask(id);
                updateEpicStatus(epic);
                updateEpicDuration(epic);
            }
        } else {
            throw new NotFoundException("Подзадача не найдена", id);
        }
        prioritizedTasks.remove(getSubtaskById(id));
        historyManager.remove(id);
        subtasks.remove(id);
    }

    @Override
    public HistoryManager getHistoryManager() {
        return historyManager;
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return prioritizedTasks.stream()
                .toList();
    }

    public void setID(int id) {
        this.id = id;
    }

    @Override
    public boolean checkIntersections(Task task) {
        for (Task currentTask : tasks.values()) {
            if (currentTask == task) {
                continue;
            }
            if (checkIntersection(task, currentTask)) {
                return true;
            }
        }
        return false;
    }

    private boolean checkIntersection(Task task1, Task task2) {
        LocalDateTime startTime1 = task1.getStartTime();
        LocalDateTime startTime2 = task2.getStartTime();
        if (startTime1 == null || startTime2 == null || task1.equals(task2)) {
            return false;
        }
        LocalDateTime endTime1 = task1.getEndTime();
        LocalDateTime endTime2 = task2.getEndTime();
        return startTime1.isAfter(startTime2) && startTime1.isBefore(endTime2)
                || endTime1.isAfter(startTime2) && endTime1.isBefore(endTime2)
                || startTime2.isAfter(startTime1) && startTime2.isBefore(endTime1)
                || endTime2.isAfter(startTime1) && endTime2.isBefore(endTime1)
                || startTime1.equals(startTime2)
                || endTime1.equals(endTime2);
    }

    private int generateId() {
        id++;
        return id;
    }

    private void updateEpicStatus(Epic epic) {
        if (epic == null) {
            return;
        }
        ArrayList<Subtask> subTasks = getSubTasksByEpic(epic);
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

    private void updateEpicDuration(Epic epic) {
        if (epic == null) {
            return;
        }
        LocalDateTime startTimeOfEpic = null;
        LocalDateTime endTimeOfEpic = null;
        Duration epicDuration = null;
        ArrayList<Subtask> subTasks = getSubTasksByEpic(epic);
        for (Subtask subTask : subTasks) {
            LocalDateTime startTimeOfSubtask = subTask.getStartTime();
            LocalDateTime endTimeOfSubtask = subTask.getEndTime();
            Duration subtaskDuration = subTask.getDuration();
            if (startTimeOfSubtask != null) {
                if (startTimeOfEpic == null) {
                    startTimeOfEpic = startTimeOfSubtask;
                } else if (startTimeOfSubtask.isBefore(startTimeOfEpic)) {
                    startTimeOfEpic = startTimeOfSubtask;
                }
                if (endTimeOfEpic == null) {
                    endTimeOfEpic = endTimeOfSubtask;
                } else if (endTimeOfSubtask.isAfter(endTimeOfEpic)) {
                    endTimeOfEpic = endTimeOfSubtask;
                }
            }
            if (epicDuration == null) {
                epicDuration = subtaskDuration;
            } else {
                epicDuration = epicDuration.plus(subtaskDuration);
            }
        }
        epic.setStartTime(startTimeOfEpic);
        epic.setDuration(epicDuration);
        epic.setEndTime(endTimeOfEpic);
    }
}
