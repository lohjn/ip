package kibo.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
        assertFalse(event.isScheduledOn(LocalDate.of(2019, 12, 2)));
    }

    @Test
    void parseEvent_validDatedStart_returnsScheduledEvent() throws InvalidCommandException {
        Task task = parser.parseEvent(
                "event project meeting /from 2019-12-02 2pm /to 4pm");

        Event event = assertInstanceOf(Event.class, task);
        assertTrue(event.isScheduledOn(LocalDate.of(2019, 12, 2)));
        assertFalse(event.isScheduledOn(LocalDate.of(2019, 12, 3)));
        assertEquals("2019-12-02 2pm", event.getFrom());
        assertEquals("4pm", event.getTo());
    }

    @Test
    void parseEvent_invalidDatedStart_throwsInvalidCommandException() {
        InvalidCommandException exception = assertThrows(
                InvalidCommandException.class, () -> parser.parseEvent(
                        "event project meeting /from 2019-02-29 2pm /to 4pm"));

        assertEquals("The event start date must use yyyy-MM-dd format.\n"
                + "Date-aware event usage: event [description] "
                + "/from yyyy-MM-dd [time] /to [end]", exception.getMessage());
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

    @Test
    void parseScheduleDate_validDate_returnsLocalDate() throws InvalidCommandException {
        assertEquals(LocalDate.of(2019, 12, 2),
                parser.parseScheduleDate("schedule 2019-12-02"));
    }

    @Test
    void parseScheduleDate_missingOrInvalidDate_throwsInvalidCommandException() {
        assertInvalidScheduleDate("schedule");
        assertInvalidScheduleDate("schedule 2019-02-29");
        assertInvalidScheduleDate("schedule tomorrow");
        assertInvalidScheduleDate("schedule 2019-12-02 extra");
    }

    @Test
    void parseTaskIndex_nonNumberedCommand_throwsAssertionError() {
        assertThrows(AssertionError.class, () ->
                parser.parseTaskIndex("todo 1", CommandType.TODO, 1));
    }

    @Test
    void parseTaskIndex_negativeTaskCount_throwsAssertionError() {
        assertThrows(AssertionError.class, () ->
                parser.parseTaskIndex("mark 1", CommandType.MARK, -1));
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

    /**
     * Verifies each malformed schedule command produces the standard usage error.
     *
     * @param input malformed schedule command.
     */
    private void assertInvalidScheduleDate(String input) {
        InvalidCommandException exception = assertThrows(
                InvalidCommandException.class, () -> parser.parseScheduleDate(input));
        assertEquals("The schedule date must use yyyy-MM-dd format.\n"
                + "Usage: schedule yyyy-MM-dd", exception.getMessage());
    }
}
