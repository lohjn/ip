import java.util.ArrayList;
import java.util.Scanner;

/**
 * Runs the Kibo chatbot.
 */
public class Kibo {
    private static final String SEPARATOR = "____________________________________________________________";
    private static final String TODO_USAGE = "Usage: todo [description]";
    private static final String DEADLINE_USAGE =
            "Usage: deadline [description] /by [date/time]";
    private static final String EVENT_USAGE =
            "Usage: event [description] /from [start] /to [end]";

    public static void main(String[] args) {
        String banner = " _  __ _ _           \n"
                + "| |/ /(_) |__   ___  \n"
                + "| ' / | | '_ \\ / _ \\\n"
                + "| . \\ | | |_) | (_) |\n"
                + "|_|\\_\\|_|_.__/ \\___/\n";

        System.out.print(banner);
        System.out.println("Hello! I'm Kibo. I am AI.");
        System.out.println("What can I do for you?");
        System.out.println(SEPARATOR);

        ArrayList<Task> tasks = new ArrayList<>();
        Scanner scanner = new Scanner(System.in);

        while (scanner.hasNextLine()) {
            String input = scanner.nextLine().trim();
            System.out.println(SEPARATOR);

            try {
                CommandType commandType = CommandType.fromInput(input);
                switch (commandType) {
                case BYE -> {
                    System.out.println(" Bye. Hope to see you again soon!");
                    System.out.println(SEPARATOR);
                    return;
                }
                case LIST -> {
                    System.out.println(" Here are the tasks in your list:");
                    for (int i = 0; i < tasks.size(); i++) {
                        System.out.println(" " + (i + 1) + "." + tasks.get(i));
                    }
                }
                case MARK -> {
                    int taskIndex = parseTaskIndex(
                            input, CommandType.MARK.getKeyword(), tasks.size());
                    tasks.get(taskIndex).markAsDone();
                    System.out.println(" Nice! I've marked this task as done:");
                    System.out.println("   " + tasks.get(taskIndex));
                }
                case UNMARK -> {
                    int taskIndex = parseTaskIndex(
                            input, CommandType.UNMARK.getKeyword(), tasks.size());
                    tasks.get(taskIndex).markAsNotDone();
                    System.out.println(" OK, I've marked this task as not done yet:");
                    System.out.println("   " + tasks.get(taskIndex));
                }
                case DELETE -> {
                    int taskIndex = parseTaskIndex(
                            input, CommandType.DELETE.getKeyword(), tasks.size());
                    Task removedTask = tasks.remove(taskIndex);
                    System.out.println(" Noted. I've removed this task:");
                    System.out.println("   " + removedTask);
                    System.out.println(" Now you have " + tasks.size() + " tasks in the list.");
                }
                case TODO -> {
                    Task task = parseTodo(input);
                    addTask(tasks, task);
                }
                case DEADLINE -> {
                    Task task = parseDeadline(input);
                    addTask(tasks, task);
                }
                case EVENT -> {
                    Task task = parseEvent(input);
                    addTask(tasks, task);
                }
                case UNKNOWN ->
                    throw new InvalidCommandException("Sorry, that is not a valid command.\n"
                            + "Available commands: todo, deadline, event, list, mark, unmark, "
                            + "delete, bye");
                }
            } catch (KiboException exception) {
                String indentedMessage = exception.getMessage().replace("\n", "\n ");
                System.out.println(" " + indentedMessage);
            }

            System.out.println(SEPARATOR);
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
     * @throws InvalidCommandException if the description, marker, or deadline is missing
     */
    private static Task parseDeadline(String input) throws InvalidCommandException {
        String taskDetails = input.substring(CommandType.DEADLINE.getKeyword().length()).trim();
        int byMarkerIndex = taskDetails.indexOf(" /by");
        if (byMarkerIndex < 0) {
            throw new InvalidCommandException(
                    "A deadline needs a description and /by date or time.\n"
                    + DEADLINE_USAGE);
        }

        String description = taskDetails.substring(0, byMarkerIndex).trim();
        String by = taskDetails.substring(byMarkerIndex + 4).trim();
        if (description.isEmpty() || by.isEmpty()) {
            throw new InvalidCommandException(
                    "A deadline needs a description and /by date or time.\n"
                    + DEADLINE_USAGE);
        }
        return new Deadline(description, by);
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
    private static void addTask(ArrayList<Task> tasks, Task task) {
        tasks.add(task);
        System.out.println(" Got it. I've added this task:");
        System.out.println("   " + task);
        System.out.println(" Now you have " + tasks.size() + " tasks in the list.");
    }
}
