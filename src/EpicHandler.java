import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Optional;

public class EpicHandler extends BaseHttpHandler implements HttpHandler {

    public EpicHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());

        try {
            switch (endpoint) {
                case GET_OBJECTS: {
                    handleGetEpics(exchange);
                    break;
                }
                case GET_OBJECT_BY_ID: {
                    handleGetEpicByID(exchange);
                    break;
                }
                case CREATE_UPDATE_OBJECT: {
                    handleCreateUpdateEpic(exchange);
                    break;
                }
                case DELETE_OBJECT: {
                    handleDeleteEpic(exchange);
                    break;
                }
                case GET_EPIC_SUBTASKS: {
                    handleGetEpicSubtasks(exchange);
                    break;
                }
                default:
                    writeResponse(exchange, "Эндпоинт не найден", 404);
            }
        } catch (NotFoundException e) {
            writeResponse(exchange, "Эпик по указанному id не найден", 404);
        } catch (IllegalStateException e) {
            writeResponse(exchange, "Некорректный формат задачи", 400);
        } catch (InMemoryTaskManager.IntersectionException e) {
            writeResponse(exchange, "Найдены пересечения задач по времени", 406);
        } catch (FileBackedTaskManager.ManagerSaveException e) {
            writeResponse(exchange, "Ошибка сохранения данных в файл", 500);
        }
    }

    private void handleGetEpics(HttpExchange exchange) throws IOException {
        String response = gson.toJson(taskManager.getEpics());
        writeResponse(exchange, response, 200);
    }

    private void handleGetEpicByID(HttpExchange exchange) throws IOException, NotFoundException {
        Optional<Integer> epicIdOpt = getTaskId(exchange);
        if (epicIdOpt.isEmpty()) {
            writeResponse(exchange, "Некорректный идентификатор эпика", 404);
            return;
        }
        int epicId = epicIdOpt.get();
        String response = gson.toJson(taskManager.getEpicById(epicId));
        writeResponse(exchange, response, 200);
    }

    private void handleCreateUpdateEpic(HttpExchange exchange) throws IOException, NotFoundException, InMemoryTaskManager.IntersectionException {
        InputStream inputStream = exchange.getRequestBody();
        String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        Epic epic = gson.fromJson(body, Epic.class);
        if (epic.getId() == 0) {
            taskManager.addEpic(epic);
            writeResponse(exchange, "Эпик успешно добавлен", 201);
        } else {
            taskManager.updateEpic(epic);
            writeResponse(exchange, "Эпик успешно обновлен", 201);
        }
    }

    private void handleDeleteEpic(HttpExchange exchange) throws IOException, NotFoundException {
        Optional<Integer> epicIdOpt = getTaskId(exchange);
        if (epicIdOpt.isEmpty()) {
            writeResponse(exchange, "Некорректный идентификатор эпика", 404);
            return;
        }
        int epicId = epicIdOpt.get();
        taskManager.removeEpic(epicId);
        String response = gson.toJson("Эпик с ид " + epicId + " успешно удален");
        writeResponse(exchange, response, 201);
    }

    private void handleGetEpicSubtasks(HttpExchange exchange) throws IOException, NotFoundException {
        Optional<Integer> epicIdOpt = getTaskId(exchange);
        if (epicIdOpt.isEmpty()) {
            writeResponse(exchange, "Некорректный идентификатор эпика", 404);
            return;
        }
        int epicId = epicIdOpt.get();
        Epic epic = taskManager.getEpicById(epicId);
        ArrayList<Subtask> subtasks = taskManager.getSubTasksByEpic(epic);
        String response = gson.toJson(subtasks);
        writeResponse(exchange, response, 200);
    }
}
