package kibo.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import kibo.task.Todo;

/**
 * Tests that Kibo's friendly confirmations preserve task details and accurate counts.
 */
public class UiTest {
    private final Ui ui = new Ui();

    @Test
    void getTaskAddedMessage_singleAndMultipleTasks_usesCorrectNoun() {
        Todo task = new Todo("read book");
        String confirmation = "On the list! Let's make it happen:\n  [T][ ] read book\n";

        assertEquals(confirmation + "You've got 1 task on your list.", ui.getTaskAddedMessage(task, 1));
        assertEquals(confirmation + "You've got 2 tasks on your list.", ui.getTaskAddedMessage(task, 2));
    }

    @Test
    void getTaskDeletedMessage_lastCompletedTask_keepsStatusAndShowsZero() {
        Todo task = new Todo("read book");
        task.markAsDone();

        assertEquals("All cleared! I've removed this task:\n  [T][X] read book\n"
                + "You've got 0 tasks on your list.", ui.getTaskDeletedMessage(task, 0));
    }
}
