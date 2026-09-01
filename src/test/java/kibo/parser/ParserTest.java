package kibo.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import kibo.exception.InvalidCommandException;
import kibo.task.Deadline;
import kibo.task.Event;
import kibo.task.Task;

/**
 * Tests command parsing that can be checked without running the command-line UI.
 */
public class ParserTest {
    private final Parser parser = new Parser();

    @Test
    void parseDeadline_validDescriptionAndDate_returnsDeadline() throws InvalidCommandException {
        Task task = parser.parseDeadline("deadline return book /by 2019-12-02");

        Deadline deadline = assertInstanceOf(Deadline.class, task);
        assertEquals("return book", deadline.getDescription());
        assertEquals(LocalDate.of(2019, 12, 2), deadline.getBy());
        assertEquals("[D][ ] return book (by: Dec 02 2019)", deadline.toString());
    }

    @Test
    void parseDeadline_missingMarkerDescriptionOrDate_throwsInvalidCommandException() {
        assertInvalidDeadline("deadline return book by 2019-12-02");
        assertInvalidDeadline("deadline /by 2019-12-02");
        assertInvalidDeadline("deadline return book /by");
    }

    @Test
    void parseDeadline_invalidDate_throwsInvalidCommandException() {
        InvalidCommandException exception = assertThrows(
                InvalidCommandException.class, () ->
                        parser.parseDeadline("deadline return book /by 2019-02-29"));

        assertEquals("The deadline date must use yyyy-MM-dd format.\n"
                + "Usage: deadline [description] /by yyyy-MM-dd", exception.getMessage());
    }

    @Test
    void parseEvent_validDescriptionStartAndEnd_returnsEvent() throws InvalidCommandException {
        Task task = parser.parseEvent("event project meeting /from Mon 2pm /to 4pm");

        Event event = assertInstanceOf(Event.class, task);
        assertEquals("project meeting", event.getDescription());
        assertEquals("Mon 2pm", event.getFrom());
        assertEquals("4pm", event.getTo());
        assertEquals("[E][ ] project meeting (from: Mon 2pm to: 4pm)", event.toString());
    }

    @Test
    void parseEvent_missingMarkerDescriptionStartOrEnd_throwsInvalidCommandException() {
        assertInvalidEvent("event project meeting from Mon 2pm to 4pm");
        assertInvalidEvent("event project meeting /from Mon 2pm");
        assertInvalidEvent("event /from Mon 2pm /to 4pm");
        assertInvalidEvent("event project meeting /from /to 4pm");
        assertInvalidEvent("event project meeting /from Mon 2pm /to");
    }

    @Test
    void parseFindKeyword_validKeyword_returnsKeyword() throws InvalidCommandException {
        assertEquals("book", parser.parseFindKeyword("find book"));
    }

    @Test
    void parseFindKeyword_emptyKeyword_throwsInvalidCommandException() {
        InvalidCommandException exception = assertThrows(
                InvalidCommandException.class, () -> parser.parseFindKeyword("find"));

        assertEquals("The search keyword cannot be empty.\nUsage: find [keyword]",
                exception.getMessage());
    }

    /**
     * Verifies each malformed deadline command produces the standard usage error.
     *
     * @param input malformed deadline command.
     */
    private void assertInvalidDeadline(String input) {
        InvalidCommandException exception = assertThrows(
                InvalidCommandException.class, () -> parser.parseDeadline(input));
        assertEquals("A deadline needs a description and /by date.\n"
                + "Usage: deadline [description] /by yyyy-MM-dd", exception.getMessage());
    }

    /**
     * Verifies each malformed event command produces the standard usage error.
     *
     * @param input malformed event command.
     */
    private void assertInvalidEvent(String input) {
        InvalidCommandException exception = assertThrows(
                InvalidCommandException.class, () -> parser.parseEvent(input));
        assertEquals("An event needs a description, /from start, and /to end.\n"
                + "Usage: event [description] /from [start] /to [end]", exception.getMessage());
    }
}
