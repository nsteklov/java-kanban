import java.util.ArrayList;

class Epic extends Task {
    private ArrayList<Integer> iDOfSubtasks;


    public Epic(String name, String description) {
        super(name, description);
        iDOfSubtasks = new ArrayList<>();
    }

    public ArrayList<Integer> getIDOfSubtasks() {
        return iDOfSubtasks;
    }

    public void addIDOfSubtask(Integer iDOfSubtask) {
        if (!iDOfSubtasks.contains(iDOfSubtask)) {
            iDOfSubtasks.add(iDOfSubtask);
        }
    }

    public void removeIDOfSubtask(Integer iDOfSubtask) {
        if (iDOfSubtasks.contains(iDOfSubtask)) {
            iDOfSubtasks.remove(iDOfSubtask);
        }
    }

    public void removeSubtasks() {
        iDOfSubtasks.clear();
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
                ", IDOfSubtasks=" + iDOfSubtasks +
                '}';
    }
}