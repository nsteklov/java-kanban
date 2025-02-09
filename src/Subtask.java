public class Subtask {
    private String name;
    private String description;
    private Status status;
    private int id;
    private int IDOfEpic;

    public Subtask(String name, String description, int IDOfEpic) {
        this.name = name;
        this.description = description;
        this.IDOfEpic = IDOfEpic;
    }

    public int getIDOfEpic() {
        return IDOfEpic;
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
