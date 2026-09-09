package kibo.task;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/**
 * Tests the assumptions enforced by task assertions.
 */
public class TaskTest {

    @Test
    void constructor_nullDescription_throwsAssertionError() {
        assertThrows(AssertionError.class, () -> new Todo(null));
    }

    @Test
    void constructor_blankDescription_throwsAssertionError() {
        assertThrows(AssertionError.class, () -> new Todo("   "));
    }

    @Test
    void deadlineConstructor_nullDate_throwsAssertionError() {
        assertThrows(AssertionError.class, () -> new Deadline("return book", null));
    }

    @Test
    void eventConstructor_blankStartOrEnd_throwsAssertionError() {
        assertThrows(AssertionError.class, () -> new Event("meeting", "", "4pm"));
        assertThrows(AssertionError.class, () -> new Event("meeting", "2pm", " "));
    }

    @Test
    void isScheduledOn_taskTypes_returnsWhetherTaskBelongsToDate() {
        LocalDate date = LocalDate.of(2019, 12, 2);

        assertFalse(new Todo("read book").isScheduledOn(date));
        assertTrue(new Deadline("submit report", date).isScheduledOn(date));
        assertTrue(new Event("dated meeting", "2019-12-02 2pm", "4pm")
                .isScheduledOn(date));
        assertFalse(new Event("legacy meeting", "Mon 2pm", "4pm")
                .isScheduledOn(date));
    }
}
