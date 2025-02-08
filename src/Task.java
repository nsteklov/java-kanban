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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(name, task.name) && Objects.equals(description, task.description) && Objects.equals(id, task.id) && status == task.status;
    }

    @Override
    public int hashCode() {
        int hash = 17;
        if (name != null) {
            hash = hash + name.hashCode();
        }
        hash = hash * 31;
        if (description != null) {
            hash = hash + description.hashCode();
        }
        hash = hash * 31;
        hash = hash + id;

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

class Subtask extends Task {
    private int IDOfEpic;

    public Subtask(String name, String description, int IDOfEpic) {
        super(name, description);
        this.IDOfEpic = IDOfEpic;
    }

    public int getIDOfEpic() {
        return IDOfEpic;
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