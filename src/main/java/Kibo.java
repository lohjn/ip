/**
 * Runs the Kibo chatbot.
 */
public class Kibo {
    public static void main(String[] args) {
        Ui ui = new Ui();
        Parser parser = new Parser();
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
                CommandType commandType = parser.parseCommandType(input);
                switch (commandType) {
                case BYE -> {
                    parser.ensureNoArguments(input, commandType);
                    ui.showGoodbye();
                    return;
                }
                case LIST -> {
                    parser.ensureNoArguments(input, commandType);
                    ui.showTaskList(tasks);
                }
                case MARK -> {
                    int taskIndex = parser.parseTaskIndex(input, commandType, tasks.size());
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
                    int taskIndex = parser.parseTaskIndex(input, commandType, tasks.size());
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
                    int taskIndex = parser.parseTaskIndex(input, commandType, tasks.size());
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
                    Task task = parser.parseTodo(input);
                    addTask(tasks, task, ui);
                }
                case DEADLINE -> {
                    Task task = parser.parseDeadline(input);
                    addTask(tasks, task, ui);
                }
                case EVENT -> {
                    Task task = parser.parseEvent(input);
                    addTask(tasks, task, ui);
                }
                case UNKNOWN -> throw new IllegalStateException("Unexpected command type");
                }
            } catch (KiboException exception) {
                ui.showError(exception);
            }

            ui.showSeparator();
        }
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
