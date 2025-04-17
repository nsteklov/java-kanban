import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;
import java.time.Duration;
import java.util.TreeSet;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();
        if (taskManager instanceof FileBackedTaskManager) {
            ((FileBackedTaskManager) taskManager).setFileName("C:\\Users\\Java\\java-kanban\\java-kanban\\tasks.csv");
        }
        HistoryManager historyManager = taskManager.getHistoryManager();

        Task task1 = new Task("Task1", "Петь", Duration.ofMinutes(10), LocalDateTime.of(2025, 4, 15, 9, 15));
        taskManager.addTask(task1);
        Task task2 = new Task("Task2", "Плясать", Duration.ofMinutes(20), LocalDateTime.of(2025, 4, 15, 18, 00));
        taskManager.addTask(task2);
        ArrayList<Task> listOfTasks = taskManager.getTasks();
        System.out.println(listOfTasks);

        Epic epic1 = new Epic("Epic1", "Лениться");
        taskManager.addEpic(epic1);
        int idOfEpic1 = epic1.getId();
        Subtask subtask1 = new Subtask("Subtask1", "Лежать на диване", Duration.ofMinutes(30), LocalDateTime.of(2025, 4, 15, 10, 50), idOfEpic1);
        taskManager.addSubtask(subtask1);
        Subtask subtask2 = new Subtask("Subtask2", "Переписываться в телефоне", Duration.ofMinutes(20), LocalDateTime.of(2025, 4, 15, 19, 10), idOfEpic1);
        taskManager.addSubtask(subtask2);

        Epic epic2 = new Epic("Epic2", "Идти в спортзал");
        taskManager.addEpic(epic2);
        int idOfEpic2 = epic2.getId();
        Subtask subtask3 = new Subtask("Subtask3", "Поднять гантели", Duration.ofMinutes(20), LocalDateTime.of(2025, 4, 15, 12, 40), idOfEpic2);
        taskManager.addSubtask(subtask3);

        ArrayList<Epic> listOfEpics = taskManager.getEpics();
        System.out.println(listOfEpics);

        ArrayList<Subtask> listOfSubtasks = taskManager.getSubtasks();
        System.out.println(listOfSubtasks);

        System.out.println();

        task1.setStatus(Status.IN_PROGRESS);
        task1.setDescription("Пою");
        taskManager.updateTask(task1);
        task2.setStatus(Status.DONE);
        task2.setDescription("Сплясал");
        taskManager.updateTask(task2);
        listOfTasks = taskManager.getTasks();
        System.out.println(listOfTasks);

        subtask1.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask1);
        System.out.println(listOfSubtasks);
        System.out.println(listOfEpics);

        System.out.println();

        int idTask1 = task1.getId();
        int idTask2 = task2.getId();
        int idEpic1 = epic1.getId();
        int idEpic2 = epic2.getId();
        int idSubtask1 = subtask1.getId();
        int idSubtask2 = subtask2.getId();
        int idSubtask3 = subtask3.getId();

        taskManager.getTaskById(idTask1);
        taskManager.getEpicById(idEpic2);
        taskManager.getEpicById(idEpic2);
        taskManager.getTaskById(idTask2);
        taskManager.getSubtaskById(idSubtask1);
        taskManager.getSubtaskById(idSubtask2);
        taskManager.getTaskById(idTask1);
        taskManager.getEpicById(idEpic1);
        taskManager.getTaskById(idTask2);
        taskManager.getSubtaskById(idSubtask3);
        taskManager.getTaskById(idTask1);

        List<Task> historyList = historyManager.getHistory();
        for (int i = historyList.size(); i >= 1; i--) {
            System.out.println(historyList.get(i - 1));
        }

        System.out.println();

        Task task3 = new Task("Task3", "Вне времени", Duration.ofMinutes(45));
        taskManager.addTask(task3);

        TreeSet<Task> prioritizedTasks = taskManager.getPrioritizedTasks();
        for (Task task : prioritizedTasks) {
            System.out.println(task);
        }

        taskManager.removeTask(idTask2);
        taskManager.removeEpic(idEpic1);

        System.out.println();

        historyList = historyManager.getHistory();
        for (int i = historyList.size(); i >= 1; i--) {
            System.out.println(historyList.get(i - 1));
        }
    }
}
