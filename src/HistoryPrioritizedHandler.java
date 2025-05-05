import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;

public class HistoryPrioritizedHandler extends BaseHttpHandler implements HttpHandler {

    public HistoryPrioritizedHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());

        //try {
        switch (endpoint) {
            case GET_HISTORY: {
                HistoryManager historyManager = taskManager.getHistoryManager();
                List<Task> historyList = historyManager.getHistory();
                Collections.reverse(historyList);
                String response = gson.toJson(historyList);
                writeResponse(exchange, response, 200);
                break;
            }
            case GET_PRIORITIZED_TASKS: {
                TreeSet<Task> prioritizedTasks = taskManager.getPrioritizedTasks();
                String response = gson.toJson(prioritizedTasks);
                writeResponse(exchange, response, 200);
                break;
            }
            default:
                writeResponse(exchange, "Эндпоинт не найден", 404);
        }
    }
}