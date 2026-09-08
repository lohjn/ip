package kibo.task;

import static org.junit.jupiter.api.Assertions.assertThrows;

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
}
