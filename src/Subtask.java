class Subtask extends Task {

    private int iDOfEpic;

    public Subtask(String name, String description, int iDOfEpic) {
        super(name, description);
        this.iDOfEpic = iDOfEpic;
    }

    public int getIDOfEpic() {
        return iDOfEpic;
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
                ", IDOfEpic=" + iDOfEpic +
                '}';
    }
}
