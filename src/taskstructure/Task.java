package taskstructure;

import java.util.Objects;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Task {
    protected String name;
    protected String description;
    protected Status status;
    protected int id;
    protected Duration duration;
    protected LocalDateTime startTime;

    public Task(String name, String description, Duration duration) {
        this.name = name;
        this.description = description;
        this.duration = duration;
    }

    public Task(String name, String description, Duration duration, LocalDateTime startTime) {
        this.name = name;
        this.description = description;
        this.duration = duration;
        this.startTime = startTime;
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

    public Duration getDuration() {
        return duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        if (startTime == null) {
            return null;
        } else {
            return startTime.plus(duration);
        }
    }

    public Task copy() {
        Task taskCopy = new Task(this.getName(), this.getDescription(), this.getDuration(), this.getStartTime());
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
        return "TaskStructure.Task{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", start time='" + startTimeFormatted + '\'' +
                ", duration='" + durationMinutes + '\'' +
                ", end time='" + endTimeFormatted + '\'' +
                ", status=" + status +
                ", id=" + id +
                '}';
    }
}




