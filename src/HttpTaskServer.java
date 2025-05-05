import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class HttpTaskServer {
    private static final int PORT = 8080;

    public static void main(String[] args) throws IOException {
        TaskManager taskManager = Managers.getDefault();
        if (taskManager instanceof FileBackedTaskManager) {
            ((FileBackedTaskManager) taskManager).setFileName("C:\\Users\\Java\\java-kanban\\java-kanban\\tasks.csv");
        }
        HistoryManager historyManager = taskManager.getHistoryManager();

        Task task1 = new Task("Task1", "Есть", Duration.ofMinutes(10), LocalDateTime.of(2025, 4, 15, 9, 15));
        try {
            taskManager.addTask(task1);
        } catch (InMemoryTaskManager.IntersectionException exception) {
            System.out.println(exception.getMessage());
        }

        Task task2 = new Task("Task2", "Спать", Duration.ofMinutes(20), LocalDateTime.of(2025, 4, 15, 18, 00));
        try {
            taskManager.addTask(task2);
        } catch (InMemoryTaskManager.IntersectionException exception) {
            System.out.println(exception.getMessage());
        }

        int idTask1 = task1.getId();
        int idTask2 = task2.getId();
        try {
            taskManager.getTaskById(idTask1);
        } catch (NotFoundException exception) {
            System.out.println(exception.getDetailMessage());
        }
        try {
            taskManager.getTaskById(idTask2);
        } catch (NotFoundException exception) {
            System.out.println(exception.getDetailMessage());
        }

        Epic epic1 = new Epic("Эпик1", "Под");
        taskManager.addEpic(epic1);
        int idEpic1 = epic1.getId();

        Subtask subtask1 = new Subtask("Субтаск1", "Под", Duration.ofMinutes(10), LocalDateTime.of(2026, 6, 15, 9, 15), idEpic1);
        try {
            taskManager.addSubtask(subtask1);
        } catch (NotFoundException exception) {
            System.out.println(exception.getDetailMessage());
        } catch (InMemoryTaskManager.IntersectionException exception) {
            System.out.println(exception.getMessage());
        }
        Subtask subtask2 = new Subtask("Субтаск2", "Дача", Duration.ofMinutes(10), LocalDateTime.of(2026, 6, 11, 9, 15), idEpic1);
        try {
            taskManager.addSubtask(subtask2);
        } catch (NotFoundException exception) {
            System.out.println(exception.getDetailMessage());
        } catch (InMemoryTaskManager.IntersectionException exception) {
            System.out.println(exception.getMessage());
        }

        Epic epic2 = new Epic("Эпик2", "Эп");
        taskManager.addEpic(epic2);
        int idEpic2 = epic2.getId();

        String newJson = "{\"name\":\"Эпик3\",\"description\":\"Эпик3 описание\"}";
        Epic epic = BaseHttpHandler.epicFromJson(newJson);
        taskManager.addEpic(epic);
        ArrayList<Epic> epics = taskManager.getEpics();
        String epicsJson = BaseHttpHandler.epicsJson(epics);
        System.out.println(epicsJson);

        // настройка и запуск HTTP-сервера
        HttpServer httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
        HistoryPrioritizedHandler hpHandler = new HistoryPrioritizedHandler(taskManager);
        httpServer.createContext("/tasks", new TaskHandler(taskManager));
        httpServer.createContext("/subtasks", new SubtaskHandler(taskManager));
        httpServer.createContext("/epics", new EpicHandler(taskManager));
        httpServer.createContext("/history", hpHandler);
        httpServer.createContext("/prioritized", hpHandler);
        httpServer.start(); // запускаем сервер

        System.out.println("HTTP-сервер запущен на " + PORT + " порту!");

    }
}
