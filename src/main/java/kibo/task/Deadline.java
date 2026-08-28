package kibo.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Represents a task that must be completed by a specific date.
 */
public class Deadline extends Task {
    private static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd uuuu", Locale.ENGLISH);
    /** Date by which the task must be completed. */
    protected LocalDate by;

    /**
     * Creates a deadline task with the given description and deadline.
     *
     * @param description description of the task.
     * @param by deadline date.
     */
    public Deadline(String description, LocalDate by) {
        super(description);
        this.by = by;
    }

    /**
     * Returns the deadline date for storage.
     *
     * @return deadline date.
     */
    public LocalDate getBy() {
        return by;
    }

    /**
     * Returns this task in the deadline display format.
     *
     * @return task text containing the {@code [D]} type marker and formatted deadline date.
     */
    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + by.format(DISPLAY_FORMAT) + ")";
    }
}
