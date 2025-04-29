import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class TaskManagerTest<T extends TaskManager> {
    protected static TaskManager taskManager;
    protected static HistoryManager historyManager;
    protected static FileBackedTaskManager fileBackedTaskManager;

    @BeforeEach
    public void newManagers() {
        taskManager = Managers.getDefault();
        ;
        historyManager = Managers.getDefaultHistory();
        fileBackedTaskManager = new FileBackedTaskManager();
    }

    @Test
    public void managersInitialized() {
        assertNotNull(taskManager, "Менеджер задач не проинициализирован");
        assertNotNull(historyManager, "Менеджер истории не проинициализирован");
        assertNotNull(fileBackedTaskManager, "Менеджер истории задач не проинициализирован");
        int i = 0;
    }

    @Test
    public void tasksWithTheSameIDAreEqual() {
        Task task1 = new Task("Task1", "Есть", Duration.ofMinutes(32));
        task1.setId(1);
        Task task2 = new Task("Task2", "Спать", Duration.ofMinutes(42));
        task2.setId(1);

        assertEquals(task1, task2);
    }

    @Test
    public void subtasksWithTheSameIDAndEpicsWithTheSameIDAreEqual() {
        Epic epic1 = new Epic("Epic1", "Играть");
        epic1.setId(2);
        Epic epic2 = new Epic("Epic2", "Готовить");
        epic2.setId(2);
        Subtask subtask1 = new Subtask("Subtask1", "Играть в компьютер", Duration.ofMinutes(32), epic1.getId());
        subtask1.setId(3);
        Subtask subtask2 = new Subtask("Subtask2", "Играть в приставку", Duration.ofMinutes(32), epic1.getId());
        subtask2.setId(3);

        assertEquals(epic1, epic2);
        assertEquals(subtask1, subtask2);
    }

    @Test
    public void shouldCreateAndFindDifferentTasks() {
        Task task1 = new Task("Task1", "Задача номер 1", Duration.ofMinutes(32));
        taskManager.addTask(task1);
        int idOfTask1 = task1.getId();
        Epic epic1 = new Epic("Epic1", "Эпик номер 1");
        taskManager.addEpic(epic1);
        int idOfEpic1 = epic1.getId();
        Subtask subtask1 = new Subtask("Subtask1", "Подзадача 1", Duration.ofMinutes(32), idOfEpic1);
        taskManager.addSubtask(subtask1);
        int idOfSubtask1 = subtask1.getId();

        assertEquals(task1, taskManager.getTaskById(idOfTask1));
        assertEquals(epic1, taskManager.getEpicById(idOfEpic1));
        assertEquals(subtask1, taskManager.getSubtaskById(idOfSubtask1));
    }

    @Test
    public void noConflictsBetweenGivenAndGeneratedID() {
        Task task1 = new Task("Task1", "Не иди на конфликт", Duration.ofMinutes(32));
        task1.setId(1);
        Task task2 = new Task("Task1", "Не иди на конфликт", Duration.ofMinutes(32));
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        assertNotEquals(task1, task2);
    }

    @Test
    public void taskNotChangedAfterAddition() {
        Task task1 = new Task("Task1", "Не меняйся", Duration.ofMinutes(32));
        taskManager.addTask(task1);

        Epic epic1 = new Epic("Epic1", "Эпичный эпик");
        taskManager.addEpic(epic1);
        int idOfEpic1 = epic1.getId();
        Subtask subtask1 = new Subtask("Subtask1", "Подзадача 1", Duration.ofMinutes(32), idOfEpic1);
        taskManager.addSubtask(subtask1);

        assertEquals("Task1", task1.getName());
        assertEquals("Не меняйся", task1.getDescription());

        assertEquals("Epic1", epic1.getName());
        assertEquals("Эпичный эпик", epic1.getDescription());

        assertEquals("Subtask1", subtask1.getName());
        assertEquals("Подзадача 1", subtask1.getDescription());
        assertEquals(idOfEpic1, subtask1.getIDOfEpic());
    }

    @Test
    public void historyManagerSavesPreviousData() {
        HistoryManager historyManager = taskManager.getHistoryManager();
        Task task1 = new Task("Task1", "Не меняйся", Duration.ofMinutes(32));
        taskManager.addTask(task1);
        historyManager.add(task1);
        task1.setStatus(Status.DONE);
        task1.setName("Task2");
        task1.setDescription("Поменялся");
        taskManager.updateTask(task1);
        List<Task> historyList = historyManager.getHistory();
        Task task1Initial = historyList.get(historyList.size() - 1);

        assertEquals("Task1", task1Initial.getName());
        assertEquals("Не меняйся", task1Initial.getDescription());
        assertEquals(Status.NEW, task1Initial.getStatus());
    }

    @Test
    public void tasksRemoved() {
        Task task1 = new Task("Task1", "Не меняйся", Duration.ofMinutes(32));
        taskManager.addTask(task1);
        Epic epic1 = new Epic("Epic1", "Эпичный эпик");
        taskManager.addEpic(epic1);
        int idOfEpic1 = epic1.getId();
        Subtask subtask1 = new Subtask("Subtask1", "Подзадача 1", Duration.ofMinutes(32), idOfEpic1);
        taskManager.addSubtask(subtask1);

        int task1ID = task1.getId();
        int epic1ID = epic1.getId();
        int subtask1ID = subtask1.getId();

        taskManager.removeTask(task1ID);
        taskManager.removeEpic(epic1ID);
        taskManager.removeSubtask(subtask1ID);
        assertNull(taskManager.getTaskById(task1ID));
        assertNull(taskManager.getEpicById(epic1ID));
        assertNull(taskManager.getSubtaskById(subtask1ID));
    }

    @Test
    public void subtaskAddedToEpic() {
        Epic epic1 = new Epic("Epic1", "Эпичный эпик");
        taskManager.addEpic(epic1);
        int idOfEpic1 = epic1.getId();
        Subtask subtask1 = new Subtask("Subtask1", "Подзадача 1", Duration.ofMinutes(32), idOfEpic1);
        taskManager.addSubtask(subtask1);
        int idOfSubtask1 = subtask1.getId();

        ArrayList<Integer> idOfSubtasks = epic1.getIDOfSubtasks();
        assertTrue(idOfSubtasks.contains(idOfSubtask1));
    }

    @Test
    public void epicStatusUpdated() {
        Epic epic1 = new Epic("Epic1", "Эпичный эпик");
        taskManager.addEpic(epic1);
        int idOfEpic1 = epic1.getId();
        Subtask subtask1 = new Subtask("Subtask1", "Подзадача 1", Duration.ofMinutes(32), idOfEpic1);
        taskManager.addSubtask(subtask1);
        subtask1.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask1);
        Subtask subtask2 = new Subtask("Subtask2", "Подзадача 2", Duration.ofMinutes(32), idOfEpic1);
        taskManager.addSubtask(subtask2);
        subtask2.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask2);
        assertEquals(Status.DONE, epic1.getStatus());

        subtask2.setStatus(Status.NEW);
        taskManager.updateSubtask(subtask2);
        assertEquals(Status.IN_PROGRESS, epic1.getStatus());

        subtask1.setStatus(Status.NEW);
        taskManager.updateSubtask(subtask1);
        assertEquals(Status.NEW, epic1.getStatus());
    }

    @Test
    public void taskRemovedFromHistory() {
        Task task1 = new Task("Task1", "Не меняйся", Duration.ofMinutes(32));
        taskManager.addTask(task1);
        historyManager.add(task1);
        int taskID = task1.getId();
        historyManager.remove(taskID);
        List<Task> historyList = historyManager.getHistory();
        List<Integer> taskIdList = new ArrayList<>();
        for (Task task : historyList) {
            taskIdList.add(task.getId());
        }
        assertFalse(taskIdList.contains(taskID));
    }

    @Test
    public void noIDOfDeletedSubtasksInEpic() {
        Epic epic1 = new Epic("Epic1", "Эпичный эпик");
        taskManager.addEpic(epic1);
        int idOfEpic1 = epic1.getId();
        Subtask subtask1 = new Subtask("Subtask1", "Эпичная подзадача", Duration.ofMinutes(32), idOfEpic1);
        taskManager.addSubtask(subtask1);
        int idOfSubtask1 = subtask1.getId();
        Subtask subtask2 = new Subtask("Subtask2", "Бесполезная подзадача", Duration.ofMinutes(32), idOfEpic1);
        taskManager.addSubtask(subtask2);
        int idOfSubtask2 = subtask2.getId();

        taskManager.removeSubtask(idOfSubtask2);
        assertFalse(epic1.getIDOfSubtasks().contains(idOfSubtask2));
    }

    @Test
    public void taskFieldsUpdatedInManager() {
        Task task1 = new Task("Task1", "ФЗ спринта 6", Duration.ofMinutes(32));
        taskManager.addTask(task1);
        task1.setName("Task 1*");
        task1.setDescription("ФЗ спринта 6 очень сложное");
        taskManager.updateTask(task1);
        task1 = taskManager.getTaskById(task1.getId());
        assertEquals(task1.getName(), "Task 1*");
        assertEquals(task1.getDescription(), "ФЗ спринта 6 очень сложное");
    }

    @Test
    public void epicOfSubtaskExists() {
        Epic epic1 = new Epic("Epic1", "Эпичный эпик");
        taskManager.addEpic(epic1);
        int idOfEpic1 = epic1.getId();
        Subtask subtask1 = new Subtask("Subtask1", "Подзадача 1", Duration.ofMinutes(32), idOfEpic1);
        taskManager.addSubtask(subtask1);

        int idOfEpic = subtask1.getIDOfEpic();
        assertNotNull(taskManager.getEpicById(idOfEpic));
    }

    @Test
    public void correctStatus() {
        Epic epic1 = new Epic("Epic1", "Эпичный эпик");
        taskManager.addEpic(epic1);
        int idOfEpic1 = epic1.getId();
        Subtask subtask1 = new Subtask("Subtask1", "Эпичная подзадача", Duration.ofMinutes(32), idOfEpic1);
        taskManager.addSubtask(subtask1);
        int idOfSubtask1 = subtask1.getId();
        Subtask subtask2 = new Subtask("Subtask2", "Бесполезная подзадача", Duration.ofMinutes(32), idOfEpic1);
        taskManager.addSubtask(subtask2);
        int idOfSubtask2 = subtask2.getId();
    }

    @Test
    public void intervalsIntersected() {
        Task task1 = new Task("Task1", "Пересекусь с 3", Duration.ofMinutes(30), LocalDateTime.of(2025, 4, 16, 9, 00));
        taskManager.addTask(task1);
        Task task2 = new Task("Task2", "Пересекусь с 3", Duration.ofMinutes(30), LocalDateTime.of(2025, 4, 16, 9, 30));
        taskManager.addTask(task2);
        Task task3 = new Task("Task3", "Пересекусь с 1, 2", Duration.ofMinutes(30), LocalDateTime.of(2025, 4, 16, 9, 59));
        assertTrue(taskManager.checkIntersections(task3));
    }

    @Test
    public void emptyHistoryManager() {
        HistoryManager historyManager = taskManager.getHistoryManager();
        List<Task> historyList = historyManager.getHistory();
        assertEquals(historyList.size(), 0);
    }

    @Test
    public void noRepetitionsInTaskHistory() {
        Task task1 = new Task("Task1", "Таск", Duration.ofMinutes(30), LocalDateTime.of(2025, 4, 16, 9, 00));
        taskManager.addTask(task1);
        int idOfTask = task1.getId();
        taskManager.getTaskById(idOfTask);
        taskManager.getTaskById(idOfTask);
        taskManager.getTaskById(idOfTask);
        HistoryManager historyManager = taskManager.getHistoryManager();
        List<Task> historyList = historyManager.getHistory();
        long occurrences = historyList.stream()
                .filter(task1::equals)
                .count(); // Считаем количество "яблок"
        assertEquals(occurrences, 1);
    }

    @Test
    public void removingFromTaskHistory() {
        Task task1 = new Task("Task1", "Задача 1", Duration.ofMinutes(30));
        Task task2 = new Task("Task2", "Задача 2", Duration.ofMinutes(30));
        Task task3 = new Task("Task3", "Задача 3", Duration.ofMinutes(30));
        Task task4 = new Task("Task4", "Задача 4", Duration.ofMinutes(30));
        Task task5 = new Task("Task5", "Задача 5", Duration.ofMinutes(30));
        Task task6 = new Task("Task6", "Задача 6", Duration.ofMinutes(30));
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addTask(task3);
        taskManager.addTask(task4);
        taskManager.addTask(task5);
        taskManager.addTask(task6);
        int idTask1 = task1.getId();
        int idTask2 = task2.getId();
        int idTask3 = task3.getId();
        int idTask4 = task4.getId();
        int idTask5 = task5.getId();
        int idTask6 = task6.getId();
        taskManager.getTaskById(idTask1);
        taskManager.getTaskById(idTask2);
        taskManager.getTaskById(idTask3);
        taskManager.getTaskById(idTask4);
        taskManager.getTaskById(idTask5);
        taskManager.getTaskById(idTask6);
        taskManager.removeTask(idTask1);
        taskManager.removeTask(idTask6);
        taskManager.removeTask(idTask4);

        HistoryManager historyManager = taskManager.getHistoryManager();
        List<Task> historyList = historyManager.getHistory();
        List<Integer> taskIdHistoryList = historyList.stream()
                .map(task -> task.getId())
                .collect(Collectors.toList());
        assertFalse(taskIdHistoryList.contains(idTask1));
        assertTrue(taskIdHistoryList.contains(idTask2));
        assertTrue(taskIdHistoryList.contains(idTask3));
        assertFalse(taskIdHistoryList.contains(idTask4));
        assertTrue(taskIdHistoryList.contains(idTask5));
        assertFalse(taskIdHistoryList.contains(idTask6));
    }
}
