import java.util.ArrayList;
import java.util.Objects;

public class Task {
    protected String name;
    protected String description;
    protected Status status;
    protected int id;

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Status getStatus() {
        return status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Task copy() {
        Task taskCopy = new Task(this.getName(), this.getDescription());
        taskCopy.setId(this.getId());
        taskCopy.setStatus(this.getStatus());
        return taskCopy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(id, task.id);
    }

    @Override
    public int hashCode() {
        int hash = id;

        return hash;
    }

    @Override
    public String toString() {
        return "Task{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", id=" + id +
                '}';
    }
}

class Subtask extends Task {

    private int IDOfEpic;

    public Subtask(String name, String description, int IDOfEpic) {
        super(name, description);
        this.IDOfEpic = IDOfEpic;
    }

    public int getIDOfEpic() {
        return IDOfEpic;
    }


    public Subtask copy() {
        Subtask subtaskCopy = new Subtask(this.getName(), this.getDescription(), this.getIDOfEpic());
        subtaskCopy.setId(this.getId());
        subtaskCopy.setStatus(this.getStatus());
        return subtaskCopy;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", id=" + id +
                ", IDOfEpic=" + IDOfEpic +
                '}';
    }
}

class Epic extends Task {
    private ArrayList<Integer> IDOfSubtasks;


    public Epic(String name, String description) {
        super(name, description);
        IDOfSubtasks = new ArrayList<>();
    }

    public ArrayList<Integer> getIDOfSubtasks() {
        return IDOfSubtasks;
    }

    public void addIDOfSubtask(Integer IDOfSubtask) {
        if (!IDOfSubtasks.contains(IDOfSubtask)) {
            IDOfSubtasks.add(IDOfSubtask);
        };
    }

    public void removeIDOfSubtask(Integer IDOfSubtask) {
        if (IDOfSubtasks.contains(IDOfSubtask)) {
            IDOfSubtasks.remove(IDOfSubtask);
        };
    }

    public void removeSubtasks() {
        IDOfSubtasks.clear();
    }

    public Epic copy() {
        Epic epicCopy = new Epic(this.getName(), this.getDescription());
        epicCopy.setId(this.getId());
        epicCopy.setStatus(this.getStatus());
        for (Integer idOfSubtask : this.getIDOfSubtasks()) {
            epicCopy.addIDOfSubtask(idOfSubtask);
        }
        return epicCopy;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", id=" + id +
                ", IDOfSubtasks=" + IDOfSubtasks +
                '}';
    }
}

