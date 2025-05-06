import exceptions.NotFoundException;
import managers.FileBackedTaskManager;
import managers.InMemoryTaskManager;
import taskstructure.Epic;
import taskstructure.Subtask;
import taskstructure.Task;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;

public class FileBackedTaskManagerTest extends TaskManagerTest {

    @Test
    public void emptyFileLoadedEmptyFileBackedTaskManagerCreated() {
        File file = null;

        try {
            file = File.createTempFile("test", ".csv");
            fileBackedTaskManager.setFileName(file.getAbsolutePath());
            FileBackedTaskManager fileBackedTaskManagerLoaded = FileBackedTaskManager.loadFromFile(file);
            assertNotNull(fileBackedTaskManager, "Менеджер задач не проинициализирован");
            if (fileBackedTaskManagerLoaded != null) {
                assertEquals(fileBackedTaskManagerLoaded.getTasks().size(), 0);
                assertEquals(fileBackedTaskManagerLoaded.getEpics().size(), 0);
                assertEquals(fileBackedTaskManagerLoaded.getSubtasks().size(), 0);
            }
        } catch (IOException e) {
            throw new FileBackedTaskManager.ManagerSaveException(e);
        } catch (NotFoundException exception) {
            System.out.println(exception.getDetailMessage());
        } finally {
            if (file != null) {
                file.delete();
            }
        }
    }

    @Test
    public void tasksSavedInFile() {
        File file = null;

        try {
            file = File.createTempFile("test", ".csv");
            fileBackedTaskManager.setFileName(file.getAbsolutePath());
        } catch (IOException e) {
            throw new FileBackedTaskManager.ManagerSaveException(e);
        }
        FileBackedTaskManager fileBackedTaskManagerLoaded = new FileBackedTaskManager();
        try {
            Task task1 = new Task("Task1", "Таск первый", Duration.ofMinutes(32));
            fileBackedTaskManager.addTask(task1);
            Task task2 = new Task("Task2", "Таск второй", Duration.ofMinutes(32));
            fileBackedTaskManager.addTask(task2);
            Epic epic1 = new Epic("Epic1", "Очень эпичный эпик");
            fileBackedTaskManager.addEpic(epic1);
            int idOfEpic1 = epic1.getId();
            Subtask subtask1 = new Subtask("Subtask1", "Подзадача 1", Duration.ofMinutes(32), idOfEpic1);
            fileBackedTaskManager.addSubtask(subtask1);
            fileBackedTaskManagerLoaded = FileBackedTaskManager.loadFromFile(file);
        } catch (NotFoundException exception) {
            System.out.println(exception.getDetailMessage());
        } catch (InMemoryTaskManager.IntersectionException exception) {
            System.out.println(exception.getMessage());
        }
        assertNotNull(fileBackedTaskManager, "Менеджер задач не проинициализирован");
        if (fileBackedTaskManagerLoaded != null) {
            assertEquals(fileBackedTaskManagerLoaded.getTasks().size(), 2);
            assertEquals(fileBackedTaskManagerLoaded.getEpics().size(), 1);
            //assertEquals(fileBackedTaskManagerLoaded.getSubtasks().size(), 1);
        }
        if (file != null) {
            file.delete();
        }
    }

    @Test
    public void exceptionsWhileLoading() {
        File file = new File("Некорректное имя файла...///  ");
        assertThrows(FileBackedTaskManager.ManagerSaveException.class, () -> FileBackedTaskManager.loadFromFile(file));
    }

    @Test
    public void exceptionsWhileSaving() {
        fileBackedTaskManager.setFileName("Некорректное имя файла...///  ");
        Task task1 = new Task("Task1", "Таск первый", Duration.ofMinutes(32));
        assertThrows(FileBackedTaskManager.ManagerSaveException.class, () -> fileBackedTaskManager.addTask(task1));
    }
}
