package kibo.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Tests operations for searching a task list.
 */
public class TaskListTest {

    @Test
    void find_matchingDescriptions_returnsMatchesInOriginalOrder() {
        Task readBook = new Todo("read book");
        Task returnBook = new Deadline("return book", LocalDate.of(2019, 6, 6));
        Task projectMeeting = new Event("project meeting", "2pm", "4pm");
        TaskList tasks = new TaskList(List.of(readBook, returnBook, projectMeeting));

        TaskList matches = tasks.find("book");

        assertEquals(2, matches.size());
        assertSame(readBook, matches.get(0));
        assertSame(returnBook, matches.get(1));
    }

    @Test
    void find_keywordWithDifferentCase_returnsMatch() {
        TaskList tasks = new TaskList(List.of(new Todo("Read Book")));

        TaskList matches = tasks.find("BOOK");

        assertEquals(1, matches.size());
    }

    @Test
    void find_keywordAbsentFromDescriptions_returnsEmptyList() {
        TaskList tasks = new TaskList(List.of(
                new Deadline("return book", LocalDate.of(2019, 6, 6))));

        TaskList matches = tasks.find("2019");

        assertEquals(0, matches.size());
    }
}
