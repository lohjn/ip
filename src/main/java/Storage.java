import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Saves Kibo tasks in a simple text file.
 *
 * <p>Loading tasks is intentionally not included in this increment.</p>
 */
public class Storage {
    private static final Path SAVE_PATH = Path.of("data", "duke.txt");

    /**
     * Saves every task, replacing the previous saved list.
     *
     * @param tasks tasks to save
     * @throws StorageException if the file cannot be written
     */
    public static void save(List<Task> tasks) throws StorageException {
        StringBuilder contents = new StringBuilder();
        for (Task task : tasks) {
            contents.append(toStorageLine(task)).append(System.lineSeparator());
        }

        try {
            Files.createDirectories(SAVE_PATH.getParent());
            Files.writeString(SAVE_PATH, contents.toString(), StandardCharsets.UTF_8);
        } catch (IOException exception) {
            throw new StorageException("I could not save your tasks to " + SAVE_PATH + ".");
        }
    }

    /**
     * Converts one task to the on-disk format.
     *
     * @param task task to serialize
     * @return text representation of the task
     */
    private static String toStorageLine(Task task) {
        String doneStatus = task.isDone() ? "1" : "0";
        if (task instanceof Deadline deadline) {
            return "D | " + doneStatus + " | " + deadline.getDescription()
                    + " | " + deadline.getBy();
        }
        if (task instanceof Event event) {
            return "E | " + doneStatus + " | " + event.getDescription()
                    + " | " + event.getFrom() + " | " + event.getTo();
        }
        return "T | " + doneStatus + " | " + task.getDescription();
    }
}
