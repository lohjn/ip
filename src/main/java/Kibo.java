import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * Runs the Kibo chatbot.
 */
public class Kibo {
    private static final String TODO_USAGE = "Usage: todo [description]";
    private static final String DEADLINE_USAGE =
            "Usage: deadline [description] /by yyyy-MM-dd";
    private static final String EVENT_USAGE =
            "Usage: event [description] /from [start] /to [end]";

    public static void main(String[] args) {
        Ui ui = new Ui();
        ui.showWelcome();

        TaskList tasks;
        try {
            tasks = new TaskList(Storage.load());
        } catch (StorageException exception) {
            ui.showError(exception);
            ui.showSeparator();
            return;
        }

        while (ui.hasNextCommand()) {
            String input = ui.readCommand();
            ui.showSeparator();

            try {
                if (input.isEmpty()) {
                    throw new InvalidCommandException("Please enter a command.\n"
                            + "Available commands: todo, deadline, event, list, mark, unmark, "
                            + "delete, bye");
                }
                CommandType commandType = CommandType.fromInput(input);
                switch (commandType) {
                case BYE -> {
                    ensureNoArguments(input, CommandType.BYE.getKeyword());
                    ui.showGoodbye();
                    return;
                }
                case LIST -> {
                    ensureNoArguments(input, CommandType.LIST.getKeyword());
                    ui.showTaskList(tasks);
                }
                case MARK -> {
                    int taskIndex = parseTaskIndex(
                            input, CommandType.MARK.getKeyword(), tasks.size());
                    Task task = tasks.get(taskIndex);
                    boolean wasDone = task.isDone();
                    task.markAsDone();
                    try {
                        Storage.save(tasks);
                    } catch (StorageException exception) {
                        if (!wasDone) {
                            task.markAsNotDone();
                        }
                        throw exception;
                    }
                    ui.showTaskMarked(task);
                }
                case UNMARK -> {
                    int taskIndex = parseTaskIndex(
                            input, CommandType.UNMARK.getKeyword(), tasks.size());
                    Task task = tasks.get(taskIndex);
                    boolean wasDone = task.isDone();
                    task.markAsNotDone();
                    try {
                        Storage.save(tasks);
                    } catch (StorageException exception) {
                        if (wasDone) {
                            task.markAsDone();
                        }
                        throw exception;
                    }
                    ui.showTaskUnmarked(task);
                }
                case DELETE -> {
                    int taskIndex = parseTaskIndex(
                            input, CommandType.DELETE.getKeyword(), tasks.size());
                    Task removedTask = tasks.remove(taskIndex);
                    try {
                        Storage.save(tasks);
                    } catch (StorageException exception) {
                        tasks.add(taskIndex, removedTask);
                        throw exception;
                    }
                    ui.showTaskDeleted(removedTask, tasks.size());
                }
                case TODO -> {
                    Task task = parseTodo(input);
                    addTask(tasks, task, ui);
                }
                case DEADLINE -> {
                    Task task = parseDeadline(input);
                    addTask(tasks, task, ui);
                }
                case EVENT -> {
                    Task task = parseEvent(input);
                    addTask(tasks, task, ui);
                }
                case UNKNOWN ->
                    throw new InvalidCommandException("Sorry, that is not a valid command.\n"
                            + "Available commands: todo, deadline, event, list, mark, unmark, "
                            + "delete, bye");
                }
            } catch (KiboException exception) {
                ui.showError(exception);
            }

            ui.showSeparator();
        }
    }

    /**
     * Extracts and validates the task number used by mark, unmark, and delete commands.
     *
     * @param input full user input
     * @param command mark, unmark, or delete
     * @param taskCount number of tasks currently stored
     * @return zero-based task index
     * @throws KiboException if the number is missing, invalid, or out of range
     */
    private static int parseTaskIndex(String input, String command, int taskCount)
            throws KiboException {
        String numberText = input.substring(command.length()).trim();
        int taskNumber;

        try {
            taskNumber = Integer.parseInt(numberText);
        } catch (NumberFormatException exception) {
            throw new InvalidCommandException("Please provide a valid task number.\n"
                    + "Usage: " + command + " [task number]");
        }

        if (taskNumber < 1 || taskNumber > taskCount) {
            throw new KiboException("Task " + taskNumber + " does not exist in your list.");
        }
        return taskNumber - 1;
    }

    /**
     * Rejects additional words supplied to a command that has no arguments.
     *
     * @param input full user input
     * @param command command keyword
     * @throws InvalidCommandException if the user supplied extra text
     */
    private static void ensureNoArguments(String input, String command)
            throws InvalidCommandException {
        if (!input.equals(command)) {
            throw new InvalidCommandException("This command does not take any additional text.\n"
                    + "Usage: " + command);
        }
    }

    /**
     * Creates a to-do task from validated user input.
     *
     * @param input full todo command
     * @return parsed to-do task
     * @throws InvalidCommandException if the description is empty
     */
    private static Task parseTodo(String input) throws InvalidCommandException {
        String description = input.substring(CommandType.TODO.getKeyword().length()).trim();
        if (description.isEmpty()) {
            throw new InvalidCommandException(
                    "The description of a todo cannot be empty.\n" + TODO_USAGE);
        }
        return new Todo(description);
    }

    /**
     * Creates a deadline task from validated user input.
     *
     * @param input full deadline command
     * @return parsed deadline task
     * @throws InvalidCommandException if the description, marker, or date is missing or invalid
     */
    private static Task parseDeadline(String input) throws InvalidCommandException {
        String taskDetails = input.substring(CommandType.DEADLINE.getKeyword().length()).trim();
        int byMarkerIndex = taskDetails.indexOf(" /by");
        if (byMarkerIndex < 0) {
            throw new InvalidCommandException(
                    "A deadline needs a description and /by date.\n"
                    + DEADLINE_USAGE);
        }

        String description = taskDetails.substring(0, byMarkerIndex).trim();
        String dateText = taskDetails.substring(byMarkerIndex + 4).trim();
        if (description.isEmpty() || dateText.isEmpty()) {
            throw new InvalidCommandException(
                    "A deadline needs a description and /by date.\n"
                    + DEADLINE_USAGE);
        }

        try {
            return new Deadline(description, LocalDate.parse(dateText));
        } catch (DateTimeParseException exception) {
            throw new InvalidCommandException("The deadline date must use yyyy-MM-dd format.\n"
                    + DEADLINE_USAGE);
        }
    }

    /**
     * Creates an event task from validated user input.
     *
     * @param input full event command
     * @return parsed event task
     * @throws InvalidCommandException if the description, markers, start, or end is missing
     */
    private static Task parseEvent(String input) throws InvalidCommandException {
        String taskDetails = input.substring(CommandType.EVENT.getKeyword().length()).trim();
        int fromMarkerIndex = taskDetails.indexOf(" /from");
        int toMarkerIndex = taskDetails.indexOf(" /to", fromMarkerIndex + 1);
        if (fromMarkerIndex < 0 || toMarkerIndex < 0 || toMarkerIndex < fromMarkerIndex) {
            throw new InvalidCommandException(
                    "An event needs a description, /from start, and /to end.\n"
                    + EVENT_USAGE);
        }

        String description = taskDetails.substring(0, fromMarkerIndex).trim();
        String from = taskDetails.substring(fromMarkerIndex + 6, toMarkerIndex).trim();
        String to = taskDetails.substring(toMarkerIndex + 4).trim();
        if (description.isEmpty() || from.isEmpty() || to.isEmpty()) {
            throw new InvalidCommandException(
                    "An event needs a description, /from start, and /to end.\n"
                    + EVENT_USAGE);
        }
        return new Event(description, from, to);
    }

    /**
     * Stores a task and prints the standard confirmation message.
     *
     * @param tasks task storage
     * @param task task to add
     */
    private static void addTask(TaskList tasks, Task task, Ui ui) throws StorageException {
        tasks.add(task);
        try {
            Storage.save(tasks);
        } catch (StorageException exception) {
            tasks.remove(tasks.size() - 1);
            throw exception;
        }
        ui.showTaskAdded(task, tasks.size());
    }
}
