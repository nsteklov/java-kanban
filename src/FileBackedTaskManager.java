import java.io.Writer;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.File;
import java.io.BufferedReader;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.time.Duration;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private String fileName;

    public static void main(String[] args) {
        Path filePath = Paths.get("C:\\Users\\Java\\java-kanban\\java-kanban\\tasksForLoad.csv");
        File file = filePath.toFile();
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager();
        try {
            fileBackedTaskManager = FileBackedTaskManager.loadFromFile(file);
        } catch (NotFoundException exception) {
            System.out.println(exception.getDetailMessage());
        }
        ArrayList<Task> listOfTasks = fileBackedTaskManager.getTasks();
        System.out.println(listOfTasks);

        ArrayList<Epic> listOfEpics = fileBackedTaskManager.getEpics();
        System.out.println(listOfEpics);

        ArrayList<Subtask> listOfSubtasks = fileBackedTaskManager.getSubtasks();
        System.out.println(listOfSubtasks);
    }

    public static class ManagerSaveException extends RuntimeException {
        public ManagerSaveException(Throwable e) {
            super(e);
        }
    }

    public FileBackedTaskManager() {
        super();
        this.fileName = "";
    }

    public FileBackedTaskManager(String fileName) {
        super();
        this.fileName = fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public static FileBackedTaskManager loadFromFile(File file) throws NotFoundException {

        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager();
        boolean firstLine = true;
        int maxID = 0;
        try (BufferedReader fileReader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            while (fileReader.ready()) {
                String data = fileReader.readLine();
                if (firstLine) {
                    firstLine = false;
                    continue;
                }
                Task task = fromString(data);
                if (task instanceof Epic) {
                    fileBackedTaskManager.updateEpic((Epic) task);
                } else if (task instanceof Subtask) {
                    Subtask subtask = (Subtask) task;
                    Integer iDOfSubtask = subtask.getId();
                    Integer iDOfEpic = subtask.getIDOfEpic();
                    Epic epic = fileBackedTaskManager.getEpicById(iDOfEpic);
                    if (epic != null) {
                        epic.addIDOfSubtask(iDOfSubtask);
                    } else {
                        throw new NotFoundException("Эпик не найден", iDOfEpic);
                    }
                    try {
                        fileBackedTaskManager.updateSubtask((Subtask) task);
                        fileBackedTaskManager.updateEpic(epic);
                    } catch (InMemoryTaskManager.IntersectionException exception) {
                        System.out.println(exception.getMessage());
                    }
                } else {
                    try {
                        fileBackedTaskManager.updateTask(task);
                    } catch (InMemoryTaskManager.IntersectionException exception) {
                        System.out.println(exception.getMessage());
                    }
                }
                int idOfTask = task.getId();
                if (idOfTask > maxID) {
                    maxID = idOfTask;
                }
            }
            fileBackedTaskManager.setID(maxID);
        } catch (IOException e) {
            throw new ManagerSaveException(e);
        }

        return fileBackedTaskManager;
    }

    @Override
    public void removeTasks() {
        super.removeTasks();
        save();
    }

    @Override
    public void removeEpics() {
        super.removeEpics();
        save();
    }

    @Override
    public void removeSubtasks() {
        super.removeSubtasks();
        save();
    }

    @Override
    public void addTask(Task task) throws IntersectionException {
        super.addTask(task);
        save();
    }

    @Override
    public void addEpic(Epic epic) {
        super.addEpic(epic);
        save();
    }

    @Override
    public void addSubtask(Subtask subtask) throws IntersectionException, NotFoundException {
        super.addSubtask(subtask);
        save();
    }

    @Override
    public void updateTask(Task task) throws IntersectionException {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) throws IntersectionException, NotFoundException {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void removeTask(Integer id) throws NotFoundException {
        super.removeTask(id);
        save();
    }

    @Override
    public void removeEpic(Integer id) throws NotFoundException {
        super.removeEpic(id);
        save();
    }

    @Override
    public void removeSubtask(Integer id) throws NotFoundException {
        super.removeSubtask(id);
        save();
    }

    private void save() {
        if (fileName.isBlank()) {
            return;
        }
        try (Writer fileWriter = new FileWriter(fileName, StandardCharsets.UTF_8)) {
            fileWriter.write("id,type,name,status,description,epic\n");
            for (Task task : getTasks()) {
                fileWriter.write(toString(task) + "\n");
            }
            for (Epic epic : getEpics()) {
                fileWriter.write(toString(epic) + "\n");
            }
            for (Subtask subtask : getSubtasks()) {
                fileWriter.write(toString(subtask) + "\n");
            }
        } catch (IOException e) {
            throw new ManagerSaveException(e);
        }
    }

    private static String toString(Task task) {
        String string = "";
        String startTimeString = "";
        long durationMinutes = 0;
        LocalDateTime startTime = task.getStartTime();
        if (startTime != null) {
            startTimeString = startTime.toString();
        }
        Duration duration = task.getDuration();
        if (duration != null) {
            durationMinutes = duration.toMinutes();
        }
        if (task instanceof Epic) {
            String endTimeString = "";
            LocalDateTime endTime = task.getEndTime();
            if (endTime != null) {
                endTimeString = endTime.toString();
            }
            string = task.getId() + "," + "EPIC," + task.getName() + "," + task.getStatus() + "," + task.getDescription() + "," + startTimeString + "," + durationMinutes + "," + endTimeString;
        } else if (task instanceof Subtask) {
            string = task.getId() + "," + "SUBTASK," + task.getName() + "," + task.getStatus() + "," + task.getDescription() + "," + startTimeString + "," + durationMinutes + "," + ((Subtask) task).getIDOfEpic();
        } else {
            string = task.getId() + "," + "TASK," + task.getName() + "," + task.getStatus() + "," + task.getDescription() + "," + startTimeString + "," + durationMinutes;
        }
        return string;
    }

    private static Task fromString(String value) {
        String[] split = value.split(",", 8);
        int id = Integer.parseInt(split[0]);
        String type = split[1];
        String name = split[2];
        Status status = Status.valueOf(split[3]);
        String description = split[4];
        String startTimeString = split[5];
        Duration duration = Duration.ofMinutes(Long.parseLong(split[6]));
        if (type.equals("EPIC")) {
            String endTimeString = split[7];
            Epic epic = new Epic(name, description);
            if (!startTimeString.isBlank()) {
                epic.setStartTime(LocalDateTime.parse(startTimeString));
            }
            if (!endTimeString.isBlank()) {
                epic.setEndTime(LocalDateTime.parse(endTimeString));
            }
            epic.setDuration(duration);
            epic.setId(id);
            epic.setStatus(status);
            return epic;
        } else if (type.equals("SUBTASK")) {
            int idOfEpic = Integer.parseInt(split[7]);
            Subtask subtask = new Subtask(name, description, duration, idOfEpic);
            if (!startTimeString.isBlank()) {
                subtask.setStartTime(LocalDateTime.parse(startTimeString));
            }
            subtask.setDuration(duration);
            subtask.setId(id);
            subtask.setStatus(status);
            return subtask;
        } else {
            Task task = new Task(name, description, duration);
            if (!startTimeString.isBlank()) {
                task.setStartTime(LocalDateTime.parse(startTimeString));
            }
            task.setDuration(duration);
            task.setId(id);
            task.setStatus(status);
            return task;
        }
    }
}
