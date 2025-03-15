import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;


public class InMemoryTaskManagerTest {
    private static TaskManager taskManager;
    private static HistoryManager historyManager;

    @BeforeAll
    public static void newManagers() {
        taskManager = Managers.getDefault();
        historyManager = Managers.getDefaultHistory();
    }

    @Test
    public void managersInitialized() {
        assertNotNull(taskManager, "Менеджер задач не проинициализирован");
        assertNotNull(historyManager, "Менеджер истории не проинициализирован");
    }

    @Test
    public void tasksWithTheSameIDAreEqual() {
        Task task1 = new Task("Task1", "Есть");
        task1.setId(1);
        Task task2 = new Task("Task2", "Спать");
        task2.setId(1);

        assertEquals(task1, task2);
    }

    @Test
    public void subtasksWithTheSameIDAndEpicsWithTheSameIDAreEqual() {
        Epic epic1 = new Epic("Epic1", "Играть");
        epic1.setId(2);
        Epic epic2 = new Epic("Epic2", "Готовить");
        epic2.setId(2);
        Subtask subtask1 = new Subtask("Subtask1", "Играть в компьютер", epic1.getId());
        subtask1.setId(3);
        Subtask subtask2 = new Subtask("Subtask2", "Играть в приставку", epic1.getId());
        subtask2.setId(3);

        assertEquals(epic1, epic2);
        assertEquals(subtask1, subtask2);
    }

    @Test
    public void shouldCreateAndFindDifferentTasks() {
        Task task1 = new Task("Task1", "Задача номер 1");
        taskManager.addTask(task1);
        int idOfTask1 = task1.getId();
        Epic epic1 = new Epic("Epic1", "Эпик номер 1");
        taskManager.addEpic(epic1);
        int idOfEpic1 = epic1.getId();
        Subtask subtask1 = new Subtask("Subtask1", "Подзадача 1", idOfEpic1);
        taskManager.addSubtask(subtask1);
        int idOfSubtask1 = subtask1.getId();

        assertEquals(task1, taskManager.getTaskById(idOfTask1));
        assertEquals(epic1, taskManager.getEpicById(idOfEpic1));
        assertEquals(subtask1, taskManager.getSubtaskById(idOfSubtask1));
    }

    @Test
    public void noConflictsBetweenGivenAndGeneratedID() {
        Task task1 = new Task("Task1", "Не иди на конфликт");
        task1.setId(1);
        Task task2 = new Task("Task1", "Не иди на конфликт");
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        assertNotEquals(task1, task2);
    }

    @Test
    public void taskNotChangedAfterAddition() {
        Task task1 = new Task("Task1", "Не меняйся");
        taskManager.addTask(task1);

        Epic epic1 = new Epic("Epic1", "Эпичный эпик");
        taskManager.addEpic(epic1);
        int idOfEpic1 = epic1.getId();
        Subtask subtask1 = new Subtask("Subtask1", "Подзадача 1", idOfEpic1);
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
        Task task1 = new Task("Task1", "Не меняйся");
        taskManager.addTask(task1);
        historyManager.add(task1);
        task1.setStatus(Status.DONE);
        task1.setName("Task2");
        task1.setDescription("Поменялся");
        taskManager.updateTask(task1);
        List<Task> historyList = historyManager.getHistory();
        Task task1Initial = historyList.get(historyList.size() -1);

        assertEquals("Task1", task1Initial.getName());
        assertEquals("Не меняйся", task1Initial.getDescription());
        assertEquals(Status.NEW, task1Initial.getStatus());
    }

    @Test
    public void tasksRemoved() {
        Task task1 = new Task("Task1", "Не меняйся");
        taskManager.addTask(task1);
        Epic epic1 = new Epic("Epic1", "Эпичный эпик");
        taskManager.addEpic(epic1);
        int idOfEpic1 = epic1.getId();
        Subtask subtask1 = new Subtask("Subtask1", "Подзадача 1", idOfEpic1);
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
        Subtask subtask1 = new Subtask("Subtask1", "Подзадача 1", idOfEpic1);
        taskManager.addSubtask(subtask1);
        int idOfSubtask1 = subtask1.getId();

        ArrayList<Integer> idOfSubtasks =  epic1.getIDOfSubtasks();
        assertTrue(idOfSubtasks.contains(idOfSubtask1));
    }

    @Test
    public void epicStatusUpdated() {
        Epic epic1 = new Epic("Epic1", "Эпичный эпик");
        taskManager.addEpic(epic1);
        int idOfEpic1 = epic1.getId();
        Subtask subtask1 = new Subtask("Subtask1", "Подзадача 1", idOfEpic1);
        taskManager.addSubtask(subtask1);
        subtask1.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask1);
        int idOfSubtask1 = subtask1.getId();

        assertEquals(Status.DONE, epic1.getStatus());
    }

    @Test
    public void taskRemovedFromHistory() {
        Task task1 = new Task("Task1", "Не меняйся");
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
        Subtask subtask1 = new Subtask("Subtask1", "Эпичная подзадача", idOfEpic1);
        taskManager.addSubtask(subtask1);
        int idOfSubtask1 = subtask1.getId();
        Subtask subtask2 = new Subtask("Subtask2", "Бесполезная подзадача", idOfEpic1);
        taskManager.addSubtask(subtask2);
        int idOfSubtask2 = subtask2.getId();

        taskManager.removeSubtask(idOfSubtask2);
        assertFalse(epic1.getIDOfSubtasks().contains(idOfSubtask2));
    }

    @Test
    public void taskFieldsUpdatedInManager() {
        Task task1 = new Task("Task1", "ФЗ спринта 6");
        taskManager.addTask(task1);
        task1.setName("Task 1*");
        task1.setDescription("ФЗ спринта 6 очень сложное");
        taskManager.updateTask(task1);
        task1 = taskManager.getTaskById(task1.getId());
        assertEquals(task1.getName(), "Task 1*");
        assertEquals(task1.getDescription(), "ФЗ спринта 6 очень сложное");
    }
}
