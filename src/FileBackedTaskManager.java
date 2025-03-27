import java.io.Writer;
import java.io.FileWriter;
import java.io.Reader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.File;
import java.nio.file.Files;
import java.io.BufferedReader;
import java.util.ArrayList;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private String fileName;

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

    public void setFileName (String fileName) {
        this.fileName = fileName;
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
    public void addTask(Task task) {
        super.addTask(task);
        save();
    }

    @Override
    public void addEpic(Epic epic) {
        super.addEpic(epic);
        save();
    }

    @Override
    public void addSubtask(Subtask subtask) {
        super.addSubtask(subtask);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void removeTask(Integer id) {
        super.removeTask(id);
        save();
    }

    @Override
    public void removeEpic(Integer id) {
        super.removeEpic(id);
        save();
    }

    @Override
    public void removeSubtask(Integer id) {
        super.removeSubtask(id);
        save();
    }

    public void save () {
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

    public static String toString(Task task) {
        String string = "";
        if (task instanceof Epic) {
            string = task.getId() + "," + "EPIC," + task.getName() + "," + task.getStatus() + "," + task.getDescription();
        } else if (task instanceof Subtask) {
            string = task.getId() + "," + "SUBTASK," + task.getName() + "," + task.getStatus() + "," + task.getDescription() +  "," + ((Subtask) task).getIDOfEpic();
        } else {
            string = task.getId() + "," + "TASK," + task.getName() + "," + task.getStatus() + "," + task.getDescription();
        }
        return string;
    }

    public static FileBackedTaskManager loadFromFile(File file) {

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
                    }
                    fileBackedTaskManager.updateSubtask((Subtask) task);
                } else {
                    fileBackedTaskManager.updateTask(task);
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

    public static Task fromString(String value) {
        String[] split = value.split(",", 6);
        int id = Integer.parseInt(split[0]);
        String type = split[1];
        String name = split[2];
        Status status = Status.valueOf(split[3]);
        String description = split[4];
        if (type.equals("EPIC")) {
            Epic epic = new Epic(name, description);
            epic.setId(id);
            epic.setStatus(status);
            return epic;
        } else if (type.equals("SUBTASK")) {
            int idOfEpic = Integer.parseInt(split[5]);
            Subtask subtask = new Subtask(name, description, idOfEpic);
            subtask.setId(id);
            subtask.setStatus(status);
            return subtask;
        } else {
            Task task = new Task(name, description);
            task.setId(id);
            task.setStatus(status);
            return task;
        }
    }

    public static void main(String[] args) {
        Path filePath = Paths.get("C:\\Users\\Java\\java-kanban\\java-kanban\\tasksForLoad.csv");
        File file = filePath.toFile();
        FileBackedTaskManager fileBackedTaskManager = FileBackedTaskManager.loadFromFile(file);

        ArrayList<Task> listOfTasks = fileBackedTaskManager.getTasks();
        System.out.println(listOfTasks);

        ArrayList<Epic> listOfEpics = fileBackedTaskManager.getEpics();
        System.out.println(listOfEpics);

        ArrayList<Subtask> listOfSubtasks = fileBackedTaskManager.getSubtasks();
        System.out.println(listOfSubtasks);
    }
}
