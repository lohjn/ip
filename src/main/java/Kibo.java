import java.util.Scanner;

/**
 * Runs the Kibo chatbot.
 */
public class Kibo {
    private static final int MAX_TASKS = 100;
    private static final String SEPARATOR = "____________________________________________________________";

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

        Task[] tasks = new Task[MAX_TASKS];
        int taskCount = 0;
        Scanner scanner = new Scanner(System.in);

        while (scanner.hasNextLine()) {
            String input = scanner.nextLine();
            System.out.println(SEPARATOR);

            if (input.equals("bye")) {
                System.out.println(" Bye. Hope to see you again soon!");
                System.out.println(SEPARATOR);
                break;
            }

            if (input.equals("list")) {
                System.out.println(" Here are the tasks in your list:");
                for (int i = 0; i < taskCount; i++) {
                    System.out.println(" " + (i + 1) + "." + tasks[i]);
                }
            } else if (input.startsWith("mark ")) {
                int taskNumber = Integer.parseInt(input.substring(5));
                int taskIndex = taskNumber - 1;
                tasks[taskIndex].markAsDone();
                System.out.println(" Nice! I've marked this task as done:");
                System.out.println("   " + tasks[taskIndex]);
            } else if (input.startsWith("unmark ")) {
                int taskNumber = Integer.parseInt(input.substring(7));
                int taskIndex = taskNumber - 1;
                tasks[taskIndex].markAsNotDone();
                System.out.println(" OK, I've marked this task as not done yet:");
                System.out.println("   " + tasks[taskIndex]);
            } else if (input.startsWith("todo ")) {
                String description = input.substring(5);
                Task task = new Todo(description);
                tasks[taskCount] = task;
                taskCount++;
                System.out.println(" Got it. I've added this task:");
                System.out.println("   " + task);
                System.out.println(" Now you have " + taskCount + " tasks in the list.");
            } else if (input.startsWith("deadline ")) {
                String taskDetails = input.substring(9);
                int byMarkerIndex = taskDetails.indexOf(" /by ");
                String description = taskDetails.substring(0, byMarkerIndex);
                String by = taskDetails.substring(byMarkerIndex + 5);
                Task task = new Deadline(description, by);
                tasks[taskCount] = task;
                taskCount++;
                System.out.println(" Got it. I've added this task:");
                System.out.println("   " + task);
                System.out.println(" Now you have " + taskCount + " tasks in the list.");
            } else if (input.startsWith("event ")) {
                String taskDetails = input.substring(6);
                int fromMarkerIndex = taskDetails.indexOf(" /from ");
                int toMarkerIndex = taskDetails.indexOf(" /to ", fromMarkerIndex);
                String description = taskDetails.substring(0, fromMarkerIndex);
                String from = taskDetails.substring(fromMarkerIndex + 7, toMarkerIndex);
                String to = taskDetails.substring(toMarkerIndex + 5);
                Task task = new Event(description, from, to);
                tasks[taskCount] = task;
                taskCount++;
                System.out.println(" Got it. I've added this task:");
                System.out.println("   " + task);
                System.out.println(" Now you have " + taskCount + " tasks in the list.");
            } else {
                tasks[taskCount] = new Task(input);
                taskCount++;
                System.out.println(" added: " + input);
            }

            System.out.println(SEPARATOR);
        }
    }
}
