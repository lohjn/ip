package kibo.ui;

import kibo.exception.KiboException;
import kibo.task.Task;
import kibo.task.TaskList;
import java.util.Scanner;

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
        System.out.println("Hello! I'm Kibo. I am AI.");
        System.out.println("What can I do for you?");
        showSeparator();
    }

    /**
     * Returns whether another command is available from the user.
     *
     * @return {@code true} if a command can be read
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Reads and trims the next command entered by the user.
     *
     * @return command text
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
     * Displays the task list.
     *
     * @param tasks tasks to display
     */
    public void showTaskList(TaskList tasks) {
        System.out.println(" Here are the tasks in your list:");
        for (int index = 0; index < tasks.size(); index++) {
            System.out.println(" " + (index + 1) + "." + tasks.get(index));
        }
    }

    /**
     * Displays confirmation that a task was added.
     *
     * @param task added task
     * @param taskCount number of tasks after adding
     */
    public void showTaskAdded(Task task, int taskCount) {
        System.out.println(" Got it. I've added this task:");
        System.out.println("   " + task);
        System.out.println(" Now you have " + taskCount + " tasks in the list.");
    }

    /**
     * Displays confirmation that a task was marked done.
     *
     * @param task marked task
     */
    public void showTaskMarked(Task task) {
        System.out.println(" Nice! I've marked this task as done:");
        System.out.println("   " + task);
    }

    /**
     * Displays confirmation that a task was marked not done.
     *
     * @param task unmarked task
     */
    public void showTaskUnmarked(Task task) {
        System.out.println(" OK, I've marked this task as not done yet:");
        System.out.println("   " + task);
    }

    /**
     * Displays confirmation that a task was removed.
     *
     * @param task removed task
     * @param taskCount number of tasks after removal
     */
    public void showTaskDeleted(Task task, int taskCount) {
        System.out.println(" Noted. I've removed this task:");
        System.out.println("   " + task);
        System.out.println(" Now you have " + taskCount + " tasks in the list.");
    }

    /**
     * Displays a user-facing chatbot error.
     *
     * @param exception error to display
     */
    public void showError(KiboException exception) {
        String indentedMessage = exception.getMessage().replace("\n", "\n ");
        System.out.println(" " + indentedMessage);
    }

    /**
     * Displays Kibo's farewell message.
     */
    public void showGoodbye() {
        System.out.println(" Bye. Hope to see you again soon!");
        showSeparator();
    }
}
