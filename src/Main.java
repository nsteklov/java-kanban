import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();
        HistoryManager historyManager = taskManager.getHistoryManager();

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

/*        inMemoryTaskManager.removeTask(task2.getId());
        listOfTasks = inMemoryTaskManager.getTasks();
        System.out.println(listOfTasks);*/

        subtask1.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask1);
        System.out.println(listOfSubtasks);
        //taskManager.removeSubtasks();
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
        for (Task task : historyList) {
            System.out.println(task);
        }
       /* inMemoryTaskManager.removeSubtask(subtask3.getId());
        listOfSubtasks = inMemoryTaskManager.getSubtasks();
        System.out.println(listOfSubtasks);

        inMemoryTaskManager.removeEpic(epic1.getId());
        listOfEpics = inMemoryTaskManager.getEpics();
        listOfSubtasks = inMemoryTaskManager.getSubtasks();
        System.out.println(listOfSubtasks);
        System.out.println(listOfEpics);*/




    }
}
