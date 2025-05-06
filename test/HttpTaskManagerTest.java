import exceptions.NotFoundException;
import managers.Managers;
import managers.TaskManager;
import taskstructure.Epic;
import taskstructure.Subtask;
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

import java.util.ArrayList;
import java.util.List;

public class HttpTaskManagerTest {
    private static final int PORT = 8080;
    private final TaskManager manager;
    private final HttpServer taskServer;
    private Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(LocalDateTime.class, new BaseHttpHandler.LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new BaseHttpHandler.DurationAdapter())
            .create();

    public HttpTaskManagerTest() throws IOException {
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
    public void testAddTask() throws IOException, InterruptedException {
        Task task = new Task("TaskStructure.Task 1", "Testing task 1", Duration.ofMinutes(5), LocalDateTime.now());
        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        List<Task> tasksFromManager = manager.getTasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("TaskStructure.Task 1", tasksFromManager.get(0).getName(), "Некорректное имя задачи");
    }

    @Test
    public void testTasksIntersected() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");

        Task task = new Task("TaskStructure.Task 1", "Testing task 1", Duration.ofMinutes(5), LocalDateTime.now());
        String taskJson = gson.toJson(task);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Task task2 = new Task("TaskStructure.Task 2", "Testing task 2", Duration.ofMinutes(9), LocalDateTime.now());
        String taskJson2 = gson.toJson(task2);
        HttpRequest request2 = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());

        assertEquals(406, response2.statusCode());
    }

    @Test
    public void testUpdateTask() throws IOException, InterruptedException {
        Task task = new Task("TaskStructure.Task 1", "Testing task 1", Duration.ofMinutes(5), LocalDateTime.now());
        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        try {
            task = manager.getTaskById(1);
        } catch (NotFoundException exception) {
            System.out.println(exception.getMessage());
        }
        task.setDescription("Testing task 1 update");
        taskJson = gson.toJson(task);
        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        List<Task> tasksFromManager = manager.getTasks();

        assertEquals("Testing task 1 update", tasksFromManager.get(0).getDescription(), "Задача не обновилась");
    }

    @Test
    public void getTaskByID() throws IOException, InterruptedException {
        Task task = new Task("TaskStructure.Task 1", "Testing task 1", Duration.ofMinutes(5), LocalDateTime.now());
        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertDoesNotThrow(() -> manager.getTaskById(1), "Задача не найдена");

        URI url2 = URI.create("http://localhost:8080/tasks/111");
        HttpRequest request2 = HttpRequest.newBuilder().uri(url2).GET().build();
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response2.statusCode());
    }

    @Test
    public void deleteTask() throws IOException, InterruptedException {
        Task task = new Task("TaskStructure.Task 1", "Testing task 1", Duration.ofMinutes(5), LocalDateTime.now());
        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        URI url2 = URI.create("http://localhost:8080/tasks/1");
        HttpRequest request2 = HttpRequest.newBuilder().uri(url2).DELETE().build();
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        List<Task> tasksFromManager = manager.getTasks();
        assertThrows(NotFoundException.class, () -> manager.getTaskById(1));
    }

    @Test
    public void testAddEpicSubtask() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        Epic epic = new Epic("TaskStructure.Epic 1", "Testing epic 1");
        String epicJson = gson.toJson(epic);

        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        Subtask subtask = new Subtask("TaskStructure.Subtask 1", "Testing subtask 1", Duration.ofMinutes(5), LocalDateTime.now(), 1);
        String subtaskJson = gson.toJson(subtask);
        URI url2 = URI.create("http://localhost:8080/subtasks");
        HttpRequest request2 = HttpRequest.newBuilder().uri(url2).POST(HttpRequest.BodyPublishers.ofString(subtaskJson)).build();
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response2.statusCode());

        List<Subtask> subtasksFromManager = manager.getSubtasks();
        assertNotNull(subtasksFromManager, "Подзадача не возвращаются");
        assertEquals(1, subtasksFromManager.size(), "Некорректное количество подзадач");
        assertEquals("TaskStructure.Subtask 1", subtasksFromManager.get(0).getName(), "Некорректное имя подзадачи");

        List<Epic> epicsFromManager = manager.getEpics();
        assertNotNull(epicsFromManager, "Эпики не возвращаются");
        assertEquals(1, epicsFromManager.size(), "Некорректное количество эпиков");
        assertEquals("TaskStructure.Epic 1", epicsFromManager.get(0).getName(), "Некорректное имя эпика");
    }

    @Test
    public void testUpdateEpicSubtask() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        Epic epic = new Epic("TaskStructure.Epic 1", "Testing epic 1");
        String epicJson = gson.toJson(epic);
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        Subtask subtask = new Subtask("TaskStructure.Subtask 1", "Testing subtask 1", Duration.ofMinutes(5), LocalDateTime.now(), 1);
        String subtaskJson = gson.toJson(subtask);
        URI url2 = URI.create("http://localhost:8080/subtasks");
        HttpRequest request2 = HttpRequest.newBuilder().uri(url2).POST(HttpRequest.BodyPublishers.ofString(subtaskJson)).build();
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response2.statusCode());

        try {
            epic = manager.getEpicById(1);
        } catch (NotFoundException exception) {
            System.out.println(exception.getMessage());
        }
        epic.setDescription("Testing epic 1 update");
        epicJson = gson.toJson(epic);
        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        List<Epic> epicsFromManager = manager.getEpics();
        assertEquals("Testing epic 1 update", epicsFromManager.get(0).getDescription(), "Эпик не обновился");

        try {
            subtask = manager.getSubtaskById(2);
        } catch (NotFoundException exception) {
            System.out.println(exception.getMessage());
        }
        subtask.setDescription("Testing subtask 1 update");
        subtaskJson = gson.toJson(subtask);
        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(subtaskJson)).build();
        response2 = client.send(request, HttpResponse.BodyHandlers.ofString());

        List<Subtask> subtasksFromManager = manager.getSubtasks();
        assertEquals("Testing subtask 1 update", subtasksFromManager.get(0).getDescription(), "Подзадача не обновилась");
    }

    @Test
    public void getEpicSubtaskByID() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        Epic epic = new Epic("TaskStructure.Epic 1", "Testing epic 1");
        String epicJson = gson.toJson(epic);
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Subtask subtask = new Subtask("TaskStructure.Subtask 1", "Testing subtask 1", Duration.ofMinutes(5), LocalDateTime.now(), 1);
        String subtaskJson = gson.toJson(subtask);
        URI url2 = URI.create("http://localhost:8080/subtasks");
        HttpRequest request2 = HttpRequest.newBuilder().uri(url2).POST(HttpRequest.BodyPublishers.ofString(subtaskJson)).build();
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());

        assertDoesNotThrow(() -> manager.getEpicById(1), "Эпик не найден");
        assertDoesNotThrow(() -> manager.getSubtaskById(2), "Подзадача не найдена");

        URI url3 = URI.create("http://localhost:8080/epics/111");
        HttpRequest request3 = HttpRequest.newBuilder().uri(url3).GET().build();
        HttpResponse<String> response3 = client.send(request3, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response3.statusCode());

        URI url4 = URI.create("http://localhost:8080/subtasks/111");
        HttpRequest request4 = HttpRequest.newBuilder().uri(url4).GET().build();
        HttpResponse<String> response4 = client.send(request4, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response3.statusCode());
    }

    @Test
    public void deleteEpicSubtask() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        Epic epic = new Epic("TaskStructure.Epic 1", "Testing epic 1");
        String epicJson = gson.toJson(epic);
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Subtask subtask = new Subtask("TaskStructure.Subtask 1", "Testing subtask 1", Duration.ofMinutes(5), LocalDateTime.now(), 1);
        String subtaskJson = gson.toJson(subtask);
        URI url2 = URI.create("http://localhost:8080/subtasks");
        HttpRequest request2 = HttpRequest.newBuilder().uri(url2).POST(HttpRequest.BodyPublishers.ofString(subtaskJson)).build();
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());

        URI url3 = URI.create("http://localhost:8080/epics/3");
        HttpRequest request3 = HttpRequest.newBuilder().uri(url3).DELETE().build();
        HttpResponse<String> response3 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        List<Epic> epicsFromManager = manager.getEpics();
        assertThrows(NotFoundException.class, () -> manager.getTaskById(1));

        URI url4 = URI.create("http://localhost:8080/subtasks/4");
        HttpRequest request4 = HttpRequest.newBuilder().uri(url4).DELETE().build();
        HttpResponse<String> response4 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        List<Subtask> subtasksFromManager = manager.getSubtasks();
        assertThrows(NotFoundException.class, () -> manager.getTaskById(2));
    }

    @Test
    public void getEpicSubtasks() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        Epic epic = new Epic("TaskStructure.Epic 1", "Testing epic 1");
        String epicJson = gson.toJson(epic);
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Subtask subtask = new Subtask("TaskStructure.Subtask 1", "Testing subtask 1", Duration.ofMinutes(5), LocalDateTime.now(), 1);
        String subtaskJson = gson.toJson(subtask);
        URI url2 = URI.create("http://localhost:8080/subtasks");
        HttpRequest request2 = HttpRequest.newBuilder().uri(url2).POST(HttpRequest.BodyPublishers.ofString(subtaskJson)).build();
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());

        Subtask subtask2 = new Subtask("TaskStructure.Subtask 2", "Testing subtask 21", Duration.ofMinutes(5), LocalDateTime.now(), 1);
        String subtask2Json = gson.toJson(subtask2);
        request2 = HttpRequest.newBuilder().uri(url2).POST(HttpRequest.BodyPublishers.ofString(subtaskJson)).build();
        response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());

        URI url3 = URI.create("http://localhost:8080/epics/1");
        HttpRequest request3 = HttpRequest.newBuilder().uri(url3).GET().build();
        HttpResponse<String> response3 = client.send(request3, HttpResponse.BodyHandlers.ofString());
        if (response3.statusCode() == 200) {
            Epic epicFromJson = gson.fromJson(response3.body(), Epic.class);

            ArrayList<Subtask> listOfSubtasks = new ArrayList<>();

            URI url4 = URI.create("http://localhost:8080/subtasks/2");
            HttpRequest request4 = HttpRequest.newBuilder().uri(url4).GET().build();
            HttpResponse<String> response4 = client.send(request4, HttpResponse.BodyHandlers.ofString());
            if (response4.statusCode() == 200) {
                Subtask subtaskFromJson = gson.fromJson(response4.body(), Subtask.class);
                listOfSubtasks.add(subtaskFromJson);
            }

            URI url5 = URI.create("http://localhost:8080/subtasks/3");
            HttpRequest request5 = HttpRequest.newBuilder().uri(url5).GET().build();
            HttpResponse<String> response5 = client.send(request5, HttpResponse.BodyHandlers.ofString());
            if (response5.statusCode() == 200) {
                Subtask subtaskFromJson = gson.fromJson(response5.body(), Subtask.class);
                listOfSubtasks.add(subtaskFromJson);
            }

            assertEquals(listOfSubtasks, manager.getSubTasksByEpic(epicFromJson));
        }
    }
}
