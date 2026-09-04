package kibo.ui;

import java.util.Scanner;

import kibo.exception.KiboException;
import kibo.task.Task;
import kibo.task.TaskList;

/**
 * Handles all console input and output for Kibo.
 */
public class Ui {
    private static final String SEPARATOR = "____________________________________________________________";
    private static final String BANNER = " _  __ _ _           \n"
            + "| |/ /(_) |__   ___  \n"
            + "| ' / | | '_ \\ / _ \\\n"
            + "| . \\ | | |_) | (_) |\n"
            + "|_|\\_\\|_|_.__/ \\___/\n";

    private final Scanner scanner;

    /**
     * Creates a UI that reads commands from standard input.
     */
    public Ui() {
        scanner = new Scanner(System.in);
    }

    /**
     * Displays Kibo's welcome message.
     */
    public void showWelcome() {
        System.out.print(BANNER);
        System.out.println(getWelcomeMessage());
        showSeparator();
    }

    /**
     * Returns Kibo's greeting.
     *
     * @return welcome message.
     */
    public String getWelcomeMessage() {
        return "Hello! I'm Kibo. I am AI.\n"
                + "What can I do for you?";
    }

    /**
     * Returns whether another command is available from the user.
     *
     * @return {@code true} if a command can be read.
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Reads and trims the next command entered by the user.
     *
     * @return command text.
     */
    public String readCommand() {
        return scanner.nextLine().trim();
    }

    /**
     * Displays the line used to separate Kibo messages.
     */
    public void showSeparator() {
        System.out.println(SEPARATOR);
    }

    /**
     * Displays a response using the console's standard indentation.
     *
     * @param message response to display.
     */
    public void showMessage(String message) {
        System.out.println(" " + message.replace("\n", "\n "));
    }

    /**
     * Returns the formatted task list.
     *
     * @param tasks tasks to display.
     * @return task-list message.
     */
    public String getTaskListMessage(TaskList tasks) {
        StringBuilder message = new StringBuilder("Here are the tasks in your list:");
        for (int index = 0; index < tasks.size(); index++) {
            message.append("\n").append(index + 1).append(".").append(tasks.get(index));
        }
        return message.toString();
    }

    /**
     * Returns tasks that match a search keyword.
     *
     * @param matchingTasks matching tasks to display.
     * @return matching-task message.
     */
    public String getMatchingTasksMessage(TaskList matchingTasks) {
        StringBuilder message = new StringBuilder("Here are the matching tasks in your list:");
        for (int index = 0; index < matchingTasks.size(); index++) {
            message.append("\n").append(index + 1).append(".")
                    .append(matchingTasks.get(index));
        }
        return message.toString();
    }

    /**
     * Returns confirmation that a task was added.
     *
     * @param task added task.
     * @param taskCount number of tasks after adding.
     * @return task-added message.
     */
    public String getTaskAddedMessage(Task task, int taskCount) {
        return "Got it. I've added this task:\n"
                + "  " + task + "\n"
                + "Now you have " + taskCount + " tasks in the list.";
    }

    /**
     * Returns confirmation that a task was marked done.
     *
     * @param task marked task.
     * @return task-marked message.
     */
    public String getTaskMarkedMessage(Task task) {
        return "Nice! I've marked this task as done:\n  " + task;
    }

    /**
     * Returns confirmation that a task was marked not done.
     *
     * @param task unmarked task.
     * @return task-unmarked message.
     */
    public String getTaskUnmarkedMessage(Task task) {
        return "OK, I've marked this task as not done yet:\n  " + task;
    }

    /**
     * Returns confirmation that a task was removed.
     *
     * @param task removed task.
     * @param taskCount number of tasks after removal.
     * @return task-deleted message.
     */
    public String getTaskDeletedMessage(Task task, int taskCount) {
        return "Noted. I've removed this task:\n"
                + "  " + task + "\n"
                + "Now you have " + taskCount + " tasks in the list.";
    }

    /**
     * Returns a user-facing chatbot error.
     *
     * @param exception error to display.
     * @return error message.
     */
    public String getErrorMessage(KiboException exception) {
        return exception.getMessage();
    }

    /**
     * Returns Kibo's farewell message.
     *
     * @return farewell message.
     */
    public String getGoodbyeMessage() {
        return "Bye. Hope to see you again soon!";
    }
}
