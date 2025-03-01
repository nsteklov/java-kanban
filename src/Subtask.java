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
