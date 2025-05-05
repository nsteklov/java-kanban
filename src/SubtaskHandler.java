import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class SubtaskHandler extends BaseHttpHandler implements HttpHandler {

    public SubtaskHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());

        try {
            switch (endpoint) {
                case GET_OBJECTS: {
                    handleGetSubtasks(exchange);
                    break;
                }
                case GET_OBJECT_BY_ID: {
                    handleGetSubtaskByID(exchange);
                    break;
                }
                case CREATE_UPDATE_OBJECT: {
                    handleCreateUpdateSubtask(exchange);
                    break;
                }
                case DELETE_OBJECT: {
                    handleDeleteSubtask(exchange);
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

    private void handleGetSubtasks(HttpExchange exchange) throws IOException {
        String response = gson.toJson(taskManager.getSubtasks());
        writeResponse(exchange, response, 200);
    }

    private void handleGetSubtaskByID(HttpExchange exchange) throws IOException, NotFoundException {
        Optional<Integer> subtaskIdOpt = getTaskId(exchange);
        if (subtaskIdOpt.isEmpty()) {
            writeResponse(exchange, "Некорректный идентификатор подзадачи", 404);
            return;
        }
        int subtaskId = subtaskIdOpt.get();
        String response = gson.toJson(taskManager.getSubtaskById(subtaskId));
        writeResponse(exchange, response, 200);
    }

    private void handleCreateUpdateSubtask(HttpExchange exchange) throws IOException, NotFoundException, InMemoryTaskManager.IntersectionException {
        InputStream inputStream = exchange.getRequestBody();
        String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        Subtask subtask = gson.fromJson(body, Subtask.class);
        if (subtask.getId() == 0) {
            taskManager.addSubtask(subtask);
            writeResponse(exchange, "Подзадача успешно добавлена", 201);
        } else {
            taskManager.updateSubtask(subtask);
            writeResponse(exchange, "Подзадача успешно обновлена", 201);
        }
    }

    private void handleDeleteSubtask(HttpExchange exchange) throws IOException, NotFoundException {
        Optional<Integer> subtaskIdOpt = getTaskId(exchange);
        if (subtaskIdOpt.isEmpty()) {
            writeResponse(exchange, "Некорректный идентификатор подзадачи", 404);
            return;
        }
        int subtaskId = subtaskIdOpt.get();
        taskManager.removeSubtask(subtaskId);
        String response = gson.toJson("Подзадача с ид " + subtaskId + " успешно удалена");
        writeResponse(exchange, response, 201);
    }
}
