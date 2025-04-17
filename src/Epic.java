import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

class Epic extends Task {
    private ArrayList<Integer> iDOfSubtasks;
    private LocalDateTime endTime;

    public Epic(String name, String description) {
        super(name, description, Duration.ofMinutes(0));
        iDOfSubtasks = new ArrayList<>();
    }

    public ArrayList<Integer> getIDOfSubtasks() {
        return iDOfSubtasks;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
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
        epicCopy.setStartTime(this.getStartTime());
        epicCopy.setDuration(this.getDuration());
        epicCopy.setEndTime(this.getEndTime());
        for (Integer idOfSubtask : this.getIDOfSubtasks()) {
            epicCopy.addIDOfSubtask(idOfSubtask);
        }
        return epicCopy;
    }

    @Override
    public String toString() {
        String startTimeFormatted = "";
        if (startTime != null) {
            startTimeFormatted = startTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm"));
        }
        String endTimeFormatted = "";
        if (endTime != null) {
            endTimeFormatted = endTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm"));
        }
        long durationMinutes = 0;
        if (duration != null) {
            durationMinutes = duration.toMinutes();
        }
        return "Epic{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", start time='" + startTimeFormatted + '\'' +
                ", duration='" + durationMinutes + '\'' +
                ", end time='" + endTimeFormatted + '\'' +
                ", status=" + status +
                ", id=" + id +
                ", IDOfSubtasks=" + iDOfSubtasks +
                '}';
    }
}