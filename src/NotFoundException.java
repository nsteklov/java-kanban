public class NotFoundException extends Exception {
    private int taskId;

    public NotFoundException(final String message) {
        super(message);
    }

    public NotFoundException(final String message, final int taskId) {
        super(message);
        this.taskId = taskId;
    }

    public String getDetailMessage() {
        return getMessage() + " (id = " + taskId + ")";
    }
}
