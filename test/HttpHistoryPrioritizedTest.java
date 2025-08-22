import exceptions.NotFoundException;
import managers.HistoryManager;
import managers.Managers;
import managers.TaskManager;
import taskstructure.Task;
import com.sun.net.httpserver.HttpServer;
import httphandlers.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;

import java.io.IOException;

import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.stream.Collectors;

import com.google.gson.reflect.TypeToken;

public class HttpHistoryPrioritizedTest {
    private static final int PORT = 8080;
    private final TaskManager manager;
    private final HttpServer taskServer;
    private Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(LocalDateTime.class, new BaseHttpHandler.LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new BaseHttpHandler.DurationAdapter())
            .create();

    public HttpHistoryPrioritizedTest() throws IOException {
        manager = Managers.getDefault();
        taskServer = HttpServer.create(new InetSocketAddress(PORT), 0);
        HistoryPrioritizedHandler hpHandler = new HistoryPrioritizedHandler(manager);
        taskServer.createContext("/tasks", new TaskHandler(manager));
        taskServer.createContext("/subtasks", new SubtaskHandler(manager));
        taskServer.createContext("/epics", new EpicHandler(manager));
        taskServer.createContext("/history", hpHandler);
        taskServer.createContext("/prioritized", hpHandler);
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDateTime.class, new BaseHttpHandler.LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new BaseHttpHandler.DurationAdapter())
                .create();
    }

    class historyListTypeToken extends TypeToken<List<Task>> {
    }

    @BeforeEach
    public void setUp() {
        manager.removeTasks();
        manager.removeSubtasks();
        manager.removeEpics();
        taskServer.start();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop(0);
    }

    @Test
    public void tasksSavedInHistory() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");

        Task task = new Task("TaskStructure.Task 1", "Testing task 1", Duration.ofMinutes(5), LocalDateTime.now());
        String taskJson = gson.toJson(task);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Task task2 = new Task("TaskStructure.Task 2", "Testing task 2", Duration.ofMinutes(5), LocalDateTime.of(2025, 4, 15, 9, 15));
        String taskJson2 = gson.toJson(task2);
        HttpRequest request2 = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson2)).build();
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());

        try {
            manager.getTaskById(1);
        } catch (NotFoundException exception) {
            System.out.println(exception.getDetailMessage());
            assertTrue(false);
        }
        try {
            manager.getTaskById(2);
        } catch (NotFoundException exception) {
            System.out.println(exception.getDetailMessage());
            assertTrue(false);
        }

        HistoryManager historyManager = manager.getHistoryManager();

        URI urlHistory = URI.create("http://localhost:8080/history");
        HttpRequest requestHistory = HttpRequest.newBuilder().uri(urlHistory).GET().build();
        HttpResponse<String> responseHistory = client.send(requestHistory, HttpResponse.BodyHandlers.ofString());
        if (responseHistory.statusCode() == 200) {
            List<Task> historyList = gson.fromJson(responseHistory.body(), new historyListTypeToken().getType());
            List<Integer> historyListId = historyList.stream()
                    .map(t -> t.getId())
                    .collect(Collectors.toList());
            assertTrue(historyListId.contains(1));
            assertTrue(historyListId.contains(2));
        }
    }

    @Test
    public void prioritizedTasksInCorrectOrder() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");

        Task task = new Task("TaskStructure.Task 1", "Testing task 1", Duration.ofMinutes(5), LocalDateTime.of(2026, 4, 15, 9, 15));
        String taskJson = gson.toJson(task);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Task task2 = new Task("TaskStructure.Task 2", "Testing task 2", Duration.ofMinutes(5), LocalDateTime.of(2025, 4, 15, 9, 15));
        String taskJson2 = gson.toJson(task2);
        HttpRequest request2 = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson2)).build();
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());

        try {
            manager.getTaskById(1);
        } catch (NotFoundException exception) {
            System.out.println(exception.getDetailMessage());
            assertTrue(false);
        }
        try {
            manager.getTaskById(2);
        } catch (NotFoundException exception) {
            System.out.println(exception.getDetailMessage());
            assertTrue(false);
        }

        List<Task> tasksFromManager = manager.getTasks();

        URI urlPrioritized = URI.create("http://localhost:8080/prioritized");
        HttpRequest requestPrioritized = HttpRequest.newBuilder().uri(urlPrioritized).GET().build();
        HttpResponse<String> responsePrioritized = client.send(requestPrioritized, HttpResponse.BodyHandlers.ofString());
        if (responsePrioritized.statusCode() == 200) {
            List<Task> prioritizedTasks = gson.fromJson(responsePrioritized.body(), new historyListTypeToken().getType());
            assertEquals(prioritizedTasks.get(0), tasksFromManager.get(1));
            assertEquals(prioritizedTasks.get(1), tasksFromManager.get(0));
        }
    }
}
