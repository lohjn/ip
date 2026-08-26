/**
 * Identifies the commands understood by the Kibo chatbot.
 */
public enum CommandType {
    TODO("todo"),
    DEADLINE("deadline"),
    EVENT("event"),
    LIST("list"),
    MARK("mark"),
    UNMARK("unmark"),
    DELETE("delete"),
    BYE("bye"),
    UNKNOWN("");

    private final String keyword;

    /**
     * Creates a command type associated with its user-facing keyword.
     *
     * @param keyword word entered to invoke the command
     */
    CommandType(String keyword) {
        this.keyword = keyword;
    }

    /**
     * Returns the word used to invoke this command.
     *
     * @return command keyword
     */
    public String getKeyword() {
        return keyword;
    }

    /**
     * Determines the command represented by the given input.
     *
     * @param input full user input
     * @return matching command type, or {@link #UNKNOWN} if none matches
     */
    public static CommandType fromInput(String input) {
        for (CommandType commandType : values()) {
            if (commandType == UNKNOWN) {
                continue;
            }
            if (input.equals(commandType.keyword)
                    || (input.startsWith(commandType.keyword)
                    && input.length() > commandType.keyword.length()
                    && Character.isWhitespace(input.charAt(commandType.keyword.length())))) {
                return commandType;
            }
        }
        return UNKNOWN;
    }
}
