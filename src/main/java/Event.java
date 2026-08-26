/**
 * Represents a task that occurs between a start and end date or time.
 */
public class Event extends Task {
    protected String from;
    protected String to;

    /**
     * Creates an event task with the given description, start, and end.
     *
     * @param description description of the event
     * @param from start date or time stored as entered by the user
     * @param to end date or time stored as entered by the user
     */
    public Event(String description, String from, String to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    /**
     * Returns the event start text for storage.
     *
     * @return event start text
     */
    public String getFrom() {
        return from;
    }

    /**
     * Returns the event end text for storage.
     *
     * @return event end text
     */
    public String getTo() {
        return to;
    }

    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + from + " to: " + to + ")";
    }
}
