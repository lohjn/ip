/**
 * Represents a task that must be completed by a specific date or time.
 */
public class Deadline extends Task {
    protected String by;

    /**
     * Creates a deadline task with the given description and deadline.
     *
     * @param description description of the task
     * @param by deadline stored as entered by the user
     */
    public Deadline(String description, String by) {
        super(description);
        this.by = by;
    }

    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + by + ")";
    }
}
