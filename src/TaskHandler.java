import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.stream.Collectors;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.List;

public class TaskHandler extends BaseHttpHandler implements HttpHandler {

    public TaskHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());

        try {
            switch (endpoint) {
                case GET_OBJECTS: {
                    handleGetTasks(exchange);
                    break;
                }
                case GET_OBJECT_BY_ID: {
                    handleGetTaskByID(exchange);
                    break;
                }
                case CREATE_UPDATE_OBJECT: {
                    handleCreateUpdateTask(exchange);
                    break;
                }
                case DELETE_OBJECT: {
                    handleDeleteTask(exchange);
                    break;
                }
                default:
                    writeResponse(exchange, "Эндпоинт не найден", 404);
            }
        } catch (NotFoundException e) {
            writeResponse(exchange, "Задача по указанному id не найдена", 404);
        } catch (IllegalStateException e) {
            writeResponse(exchange, "Некорректный формат задачи", 400);
        } catch (InMemoryTaskManager.IntersectionException e) {
            writeResponse(exchange, "Найдены пересечения задач по времени", 406);
        } catch (FileBackedTaskManager.ManagerSaveException e) {
            writeResponse(exchange, "Ошибка сохранения данных в файл", 500);
        }
    }

    private void handleGetTasks(HttpExchange exchange) throws IOException {
        String response = gson.toJson(taskManager.getTasks());
        writeResponse(exchange, response, 200);
    }

    private void handleGetTaskByID(HttpExchange exchange) throws IOException, NotFoundException {
        Optional<Integer> taskIdOpt = getTaskId(exchange);
        if (taskIdOpt.isEmpty()) {
            writeResponse(exchange, "Некорректный идентификатор задачи", 404);
            return;
        }
        int taskId = taskIdOpt.get();
        String response = gson.toJson(taskManager.getTaskById(taskId));
        writeResponse(exchange, response, 200);
    }

    private void handleCreateUpdateTask(HttpExchange exchange) throws IOException, NotFoundException, InMemoryTaskManager.IntersectionException {
        InputStream inputStream = exchange.getRequestBody();
        String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        Task task = gson.fromJson(body, Task.class);
        if (task.getId() == 0) {
            taskManager.addTask(task);
            writeResponse(exchange, "Задача успешно добавлена", 201);
        } else {
            taskManager.updateTask(task);
            writeResponse(exchange, "Задача успешно обновлена", 201);
        }
    }

    private void handleDeleteTask(HttpExchange exchange) throws IOException, NotFoundException {
        Optional<Integer> taskIdOpt = getTaskId(exchange);
        if (taskIdOpt.isEmpty()) {
            writeResponse(exchange, "Некорректный идентификатор задачи", 404);
            return;
        }
        int taskId = taskIdOpt.get();
        taskManager.removeTask(taskId);
        String response = gson.toJson("Задача с ид " + taskId + " успешно удалена");
        writeResponse(exchange, response, 201);
    }
}
