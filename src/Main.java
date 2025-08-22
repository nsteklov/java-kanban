import exceptions.NotFoundException;
import managers.*;
import taskstructure.Epic;
import taskstructure.Status;
import taskstructure.Subtask;
import taskstructure.Task;
import exceptions.NotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;
import java.time.Duration;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();
        if (taskManager instanceof FileBackedTaskManager) {
            ((FileBackedTaskManager) taskManager).setFileName("C:\\Users\\Java\\java-kanban\\java-kanban\\tasks.csv");
        }
        HistoryManager historyManager = taskManager.getHistoryManager();

        Task task1 = new Task("Task1", "Петь", Duration.ofMinutes(10), LocalDateTime.of(2025, 4, 15, 9, 15));
        try {
            taskManager.addTask(task1);
        } catch (InMemoryTaskManager.IntersectionException exception) {
            System.out.println(exception.getMessage());
        }

        Task task2 = new Task("Task2", "Плясать", Duration.ofMinutes(20), LocalDateTime.of(2025, 4, 15, 9, 00));
        try {
            taskManager.addTask(task2);
        } catch (InMemoryTaskManager.IntersectionException exception) {
            System.out.println(exception.getMessage());
        }

        ArrayList<Task> listOfTasks = taskManager.getTasks();
        System.out.println(listOfTasks);

        Epic epic1 = new Epic("Epic1", "Лениться");
        taskManager.addEpic(epic1);
        int idOfEpic1 = epic1.getId();
        Subtask subtask1 = new Subtask("Subtask1", "Лежать на диване", Duration.ofMinutes(30), LocalDateTime.of(2025, 4, 15, 10, 50), idOfEpic1);
        try {
            taskManager.addSubtask(subtask1);
        } catch (NotFoundException exception) {
            System.out.println(exception.getDetailMessage());
        } catch (InMemoryTaskManager.IntersectionException exception) {
            System.out.println(exception.getMessage());
        }
        Subtask subtask2 = new Subtask("Subtask2", "Переписываться в телефоне", Duration.ofMinutes(20), LocalDateTime.of(2025, 4, 15, 19, 10), idOfEpic1);
        try {
            taskManager.addSubtask(subtask2);
        } catch (NotFoundException exception) {
            System.out.println(exception.getDetailMessage());
        } catch (InMemoryTaskManager.IntersectionException exception) {
            System.out.println(exception.getMessage());
        }
        Epic epic2 = new Epic("Epic2", "Идти в спортзал");
        taskManager.addEpic(epic2);
        int idOfEpic2 = epic2.getId();
        Subtask subtask3 = new Subtask("Subtask3", "Поднять гантели", Duration.ofMinutes(20), LocalDateTime.of(2025, 4, 15, 12, 40), idOfEpic2);
        try {
            taskManager.addSubtask(subtask3);
        } catch (NotFoundException exception) {
            System.out.println(exception.getDetailMessage());
        } catch (InMemoryTaskManager.IntersectionException exception) {
            System.out.println(exception.getMessage());
        }
        ArrayList<Epic> listOfEpics = taskManager.getEpics();
        System.out.println(listOfEpics);

        ArrayList<Subtask> listOfSubtasks = taskManager.getSubtasks();
        System.out.println(listOfSubtasks);

        System.out.println();

        task1.setStatus(Status.IN_PROGRESS);
        task1.setDescription("Пою");
        try {
            taskManager.updateTask(task1);
        } catch (InMemoryTaskManager.IntersectionException exception) {
            System.out.println(exception.getMessage());
        }
        task2.setStatus(Status.DONE);
        task2.setDescription("Сплясал");
        try {
            taskManager.updateTask(task2);
        } catch (InMemoryTaskManager.IntersectionException exception) {
            System.out.println(exception.getMessage());
        }
        listOfTasks = taskManager.getTasks();
        System.out.println(listOfTasks);

        subtask1.setStatus(Status.DONE);
        try {
            taskManager.updateSubtask(subtask1);
        } catch (NotFoundException exception) {
            System.out.println(exception.getDetailMessage());
        } catch (InMemoryTaskManager.IntersectionException exception) {
            System.out.println(exception.getMessage());
        }
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

        try {
            taskManager.getTaskById(idTask1);
        } catch (NotFoundException exception) {
            System.out.println(exception.getDetailMessage());
        }

        try {
            taskManager.getEpicById(idEpic2);
        } catch (NotFoundException exception) {
            System.out.println(exception.getDetailMessage());
        }
        try {
            taskManager.getEpicById(idEpic2);
        } catch (NotFoundException exception) {
            System.out.println(exception.getDetailMessage());
        }

        try {
            taskManager.getTaskById(idTask2);
        } catch (NotFoundException exception) {
            System.out.println(exception.getDetailMessage());
        }
        try {
            taskManager.getSubtaskById(idSubtask1);
        } catch (NotFoundException exception) {
            System.out.println(exception.getDetailMessage());
        }
        try {
            taskManager.getSubtaskById(idSubtask2);
        } catch (NotFoundException exception) {
            System.out.println(exception.getDetailMessage());
        }
        try {
            taskManager.getTaskById(idTask1);
        } catch (NotFoundException exception) {
            System.out.println(exception.getDetailMessage());
        }
        try {
            taskManager.getEpicById(idEpic1);
        } catch (NotFoundException exception) {
            System.out.println(exception.getDetailMessage());
        }
        try {
            taskManager.getTaskById(idTask2);
        } catch (NotFoundException exception) {
            System.out.println(exception.getDetailMessage());
        }
        try {
            taskManager.getSubtaskById(idSubtask3);
        } catch (NotFoundException exception) {
            System.out.println(exception.getDetailMessage());
        }

        try {
            taskManager.getTaskById(idTask1);
        } catch (NotFoundException exception) {
            System.out.println(exception.getDetailMessage());
        }

        List<Task> historyList = historyManager.getHistory();
        for (int i = historyList.size(); i >= 1; i--) {
            System.out.println(historyList.get(i - 1));
        }

        System.out.println();

        Task task3 = new Task("Task3", "Вне времени", Duration.ofMinutes(45));
        try {
            taskManager.addTask(task3);
        } catch (InMemoryTaskManager.IntersectionException exception) {
            System.out.println(exception.getMessage());
        }

        List<Task> prioritizedTasks = taskManager.getPrioritizedTasks();
        for (Task task : prioritizedTasks) {
            System.out.println(task);
        }

        try {
            taskManager.removeTask(idTask2);
        } catch (NotFoundException exception) {
            System.out.println(exception.getMessage());
        }

        try {
            taskManager.removeEpic(idEpic1);
        } catch (NotFoundException exception) {
            System.out.println(exception.getMessage());
        }

        System.out.println();

        historyList = historyManager.getHistory();
        for (int i = historyList.size(); i >= 1; i--) {
            System.out.println(historyList.get(i - 1));
        }
    }
}
