import java.util.ArrayList;

public class Epic  {
    private String name;
    private String description;
    private Status status;
    private int id;
    private ArrayList<Integer> IDOfSubtasks;


    public Epic(String name, String description) {
        this.name = name;
        this.description = description;
        IDOfSubtasks = new ArrayList<>();
    }

    public ArrayList<Integer> getIDOfSubtasks() {
        return IDOfSubtasks;
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

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
