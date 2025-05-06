package taskstructure;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Subtask extends Task {

    private int iDOfEpic;

    public Subtask(String name, String description, Duration duration, int iDOfEpic) {
        super(name, description, duration);
        this.iDOfEpic = iDOfEpic;
    }

    public Subtask(String name, String description, Duration duration, LocalDateTime startTime, int iDOfEpic) {
        super(name, description, duration, startTime);
        this.iDOfEpic = iDOfEpic;
    }

    public int getIDOfEpic() {
        return iDOfEpic;
    }


    public Subtask copy() {
        Subtask subtaskCopy = new Subtask(this.getName(), this.getDescription(), this.getDuration(), this.getStartTime(), this.getIDOfEpic());
        subtaskCopy.setId(this.getId());
        subtaskCopy.setStatus(this.getStatus());
        return subtaskCopy;
    }

    @Override
    public String toString() {
        String startTimeFormatted = "";
        if (startTime != null) {
            startTimeFormatted = startTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm"));
        }
        String endTimeFormatted = "";
        LocalDateTime endTime = getEndTime();
        if (endTime != null) {
            endTimeFormatted = endTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm"));
        }
        long durationMinutes = 0;
        if (duration != null) {
            durationMinutes = duration.toMinutes();
        }
        return "TaskStructure.Subtask{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", start time='" + startTimeFormatted + '\'' +
                ", duration='" + durationMinutes + '\'' +
                ", end time='" + endTimeFormatted + '\'' +
                ", status=" + status +
                ", id=" + id +
                ", IDOfEpic=" + iDOfEpic +
                '}';
    }
}
