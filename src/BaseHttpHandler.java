import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Optional;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonWriter;
import com.google.gson.stream.JsonReader;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class BaseHttpHandler {
    protected static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    protected final TaskManager taskManager;
    public static Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .create();

    public BaseHttpHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    public static class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {
        private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm");

        @Override
        public void write(final JsonWriter jsonWriter, final LocalDateTime localDateTime) throws IOException {
            if (localDateTime != null) {
                jsonWriter.value(localDateTime.format(dtf));
            } else {
                jsonWriter.value("");
            }
        }

        @Override
        public LocalDateTime read(final JsonReader jsonReader) throws IOException {
            String dateTimeString = jsonReader.nextString();
            if (dateTimeString.isBlank()) {
                return null;
            } else {
                return LocalDateTime.parse(dateTimeString, dtf);
            }
        }
    }

    public static class DurationAdapter extends TypeAdapter<Duration> {

        @Override
        public void write(final JsonWriter jsonWriter, final Duration duration) throws IOException {
            jsonWriter.value(duration.toMinutes());
        }

        @Override
        public Duration read(final JsonReader jsonReader) throws IOException {
            String durationString = jsonReader.nextString();
            if (durationString.isBlank()) {
                return null;
            } else {
                return Duration.ofMinutes(Long.parseLong(durationString));
            }
        }
    }

    protected Endpoint getEndpoint(String requestPath, String requestMethod) {
        String[] pathParts = requestPath.split("/");

        if (pathParts.length == 2) {
            if (requestMethod.equals("POST")) {
                return Endpoint.CREATE_UPDATE_OBJECT;
            } else if (pathParts[1].equals("tasks") || pathParts[1].equals("subtasks") || pathParts[1].equals("epics")) {
                return Endpoint.GET_OBJECTS;
            } else if (pathParts[1].equals("history")) {
                return Endpoint.GET_HISTORY;
            } else if (pathParts[1].equals("prioritized")) {
                return Endpoint.GET_PRIORITIZED_TASKS;
            }
        }
        if (pathParts.length == 3) {
            if (requestMethod.equals("GET")) {
                return Endpoint.GET_OBJECT_BY_ID;
            } else if (requestMethod.equals("DELETE")) {
                return Endpoint.DELETE_OBJECT;
            }
        }
        if (pathParts.length == 4 && pathParts[3].equals("subtasks")) {
            return Endpoint.GET_EPIC_SUBTASKS;
        }
        return Endpoint.UNKNOWN;
    }

    protected Optional<Integer> getTaskId(HttpExchange exchange) {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");
        try {
            return Optional.of(Integer.parseInt(pathParts[2]));
        } catch (NumberFormatException exception) {
            return Optional.empty();
        }
    }

    //Честно говоря, не понял, зачем разбивать этот метод на 3 и как вообще это сделать,
    //если для каждого из этих 3 методов может быть разный код ответа и разный текст
    //могу оставить так? мне кажется, это норм решение))
    protected void writeResponse(HttpExchange exchange,
                                 String responseString,
                                 int responseCode) throws IOException {
        try (OutputStream os = exchange.getResponseBody()) {
            exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
            exchange.sendResponseHeaders(responseCode, 0);
            os.write(responseString.getBytes(DEFAULT_CHARSET));
        }
        exchange.close();
    }

    public static String epicToJson(Epic epic) {
        return gson.toJson(epic);
    }

    public static Epic epicFromJson(String jsonTask) {
        return gson.fromJson(jsonTask, Epic.class);
    }

    public static String epicsJson(ArrayList<Epic> epics) {
        return gson.toJson(epics);
    }
}
