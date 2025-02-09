import java.util.ArrayList;
import java.util.HashMap;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();

        Task task1 = new Task("Task1", "Петь");
        taskManager.addTask(task1);
        Task task2 = new Task("Task2", "Плясать");
        taskManager.addTask(task2);
        ArrayList<Task>listOfTasks = taskManager.getTasks();
        System.out.println(listOfTasks);

        Epic epic1 = new Epic("Epic1", "Лениться");
        taskManager.addEpic(epic1);
        int IdOfEpic1 = epic1.getId();
        Subtask subtask1 = new Subtask("Subtask1", "Лежать на диване", IdOfEpic1);
        taskManager.addSubtask(subtask1);
        Subtask subtask2 = new Subtask("Subtask2", "Переписываться в телефоне", IdOfEpic1);
        taskManager.addSubtask(subtask2);

        Epic epic2 = new Epic("Epic2", "Идти в спортзал");
        taskManager.addEpic(epic2);
        int IdOfEpic2 = epic2.getId();
        Subtask subtask3 = new Subtask("Subtask3", "Поднять гантели", IdOfEpic2);
        taskManager.addSubtask(subtask3);

        ArrayList<Epic>listOfEpics = taskManager.getEpics();
        System.out.println(listOfEpics);

        ArrayList<Subtask>listOfSubtasks = taskManager.getSubtasks();
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

        taskManager.removeTask(task2.getId());
        listOfTasks = taskManager.getTasks();
        System.out.println(listOfTasks);

        subtask1.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask1);
        System.out.println(listOfSubtasks);
        //taskManager.removeSubtasks();
        System.out.println(listOfEpics);

        System.out.println();

        taskManager.removeSubtask(subtask3.getId());
        listOfSubtasks = taskManager.getSubtasks();
        System.out.println(listOfSubtasks);

        taskManager.removeEpic(epic1.getId());
        listOfEpics = taskManager.getEpics();
        listOfSubtasks = taskManager.getSubtasks();
        System.out.println(listOfSubtasks);
        System.out.println(listOfEpics);
    }
}
