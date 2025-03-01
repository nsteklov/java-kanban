import java.util.ArrayList;

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