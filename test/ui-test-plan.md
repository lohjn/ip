# Command-line UI test plan

## Test environment

- Working directory: repository root
- Build command: `find src/main/java -name '*.java' -print0 | xargs -0 javac -d out`
- Launch command: `java -cp out kibo.Kibo`
- Timeout: 5 seconds
- Setup/reset: start a fresh process for each test case. Before the test session, back up any
  existing `data/duke.txt` file and restore it when testing finishes. Before UI-001 through
  UI-004 and UI-006, replace `data/duke.txt` with an empty file so the program starts with no
  tasks. Before UI-005 and UI-007, replace it with the fixture shown in that case. The program
  writes test data to `data/duke.txt` while each case runs.
- Output comparison: exact equality after converting CRLF to LF and ignoring one final newline
- Expected exit behavior: exit normally after receiving `bye`

## UI-001: Add and display all task types

Aim: Verify creation, display, marking, and unmarking of all task types, including formatted
deadline dates.

Input, in order:

```text
todo borrow book
deadline do homework /by 2019-12-02
event project meeting /from Mon 2pm /to 4pm
mark 1
list
unmark 1
list
bye
```

Expected program output:

```text
 _  __ _ _           
| |/ /(_) |__   ___  
| ' / | | '_ \ / _ \
| . \ | | |_) | (_) |
|_|\_\|_|_.__/ \___/
Hello! I'm Kibo. I am AI.
What can I do for you?
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] borrow book
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [D][ ] do homework (by: Dec 02 2019)
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [E][ ] project meeting (from: Mon 2pm to: 4pm)
 Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
 Nice! I've marked this task as done:
   [T][X] borrow book
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][X] borrow book
 2.[D][ ] do homework (by: Dec 02 2019)
 3.[E][ ] project meeting (from: Mon 2pm to: 4pm)
____________________________________________________________
____________________________________________________________
 OK, I've marked this task as not done yet:
   [T][ ] borrow book
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] borrow book
 2.[D][ ] do homework (by: Dec 02 2019)
 3.[E][ ] project meeting (from: Mon 2pm to: 4pm)
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

## UI-002: Reject invalid commands without exiting

Aim: Verify malformed task commands, unknown commands, and invalid task numbers produce helpful errors while the chatbot continues accepting input.

Input, in order:

```text
todo
blah
deadline by 5
deadline return book /by
deadline return book /by 2019-02-29
event project meeting from 4 to 6
event project meeting /from /to 6
mark one
mark 1
unmark 1
bye
```

Expected program output:

```text
 _  __ _ _           
| |/ /(_) |__   ___  
| ' / | | '_ \ / _ \
| . \ | | |_) | (_) |
|_|\_\|_|_.__/ \___/
Hello! I'm Kibo. I am AI.
What can I do for you?
____________________________________________________________
____________________________________________________________
 The description of a todo cannot be empty.
 Usage: todo [description]
____________________________________________________________
____________________________________________________________
 Sorry, that is not a valid command.
 Available commands: todo, deadline, event, list, mark, unmark, delete, bye
____________________________________________________________
____________________________________________________________
 A deadline needs a description and /by date.
 Usage: deadline [description] /by yyyy-MM-dd
____________________________________________________________
____________________________________________________________
 A deadline needs a description and /by date.
 Usage: deadline [description] /by yyyy-MM-dd
____________________________________________________________
____________________________________________________________
 The deadline date must use yyyy-MM-dd format.
 Usage: deadline [description] /by yyyy-MM-dd
____________________________________________________________
____________________________________________________________
 An event needs a description, /from start, and /to end.
 Usage: event [description] /from [start] /to [end]
____________________________________________________________
____________________________________________________________
 An event needs a description, /from start, and /to end.
 Usage: event [description] /from [start] /to [end]
____________________________________________________________
____________________________________________________________
 Please provide a valid task number.
 Usage: mark [task number]
____________________________________________________________
____________________________________________________________
 Task 1 does not exist in your list.
____________________________________________________________
____________________________________________________________
 Task 1 does not exist in your list.
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

## UI-003: Delete tasks and renumber the list

Aim: Verify deletion removes the selected task, retains its displayed state in the confirmation, renumbers remaining tasks, and rejects invalid delete indexes.

Input, in order:

```text
todo read book
deadline return book /by 2019-06-06
event project meeting /from Aug 6th 2pm /to 4pm
mark 1
mark 2
list
delete 2
list
delete 9
delete
bye
```

Expected program output:

```text
 _  __ _ _           
| |/ /(_) |__   ___  
| ' / | | '_ \ / _ \
| . \ | | |_) | (_) |
|_|\_\|_|_.__/ \___/
Hello! I'm Kibo. I am AI.
What can I do for you?
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] read book
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [D][ ] return book (by: Jun 06 2019)
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [E][ ] project meeting (from: Aug 6th 2pm to: 4pm)
 Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
 Nice! I've marked this task as done:
   [T][X] read book
____________________________________________________________
____________________________________________________________
 Nice! I've marked this task as done:
   [D][X] return book (by: Jun 06 2019)
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][X] read book
 2.[D][X] return book (by: Jun 06 2019)
 3.[E][ ] project meeting (from: Aug 6th 2pm to: 4pm)
____________________________________________________________
____________________________________________________________
 Noted. I've removed this task:
   [D][X] return book (by: Jun 06 2019)
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][X] read book
 2.[E][ ] project meeting (from: Aug 6th 2pm to: 4pm)
____________________________________________________________
____________________________________________________________
 Task 9 does not exist in your list.
____________________________________________________________
____________________________________________________________
 Please provide a valid task number.
 Usage: delete [task number]
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

## UI-004: Save changed task lists to disk

Aim: Verify that adding, marking, and deleting tasks automatically updates `data/duke.txt`.

Input, in order:

```text
todo read book
deadline return book /by 2019-12-01
mark 1
delete 2
bye
```

Expected program output:

```text
 _  __ _ _           
| |/ /(_) |__   ___  
| ' / | | '_ \ / _ \
| . \ | | |_) | (_) |
|_|\_\|_|_.__/ \___/
Hello! I'm Kibo. I am AI.
What can I do for you?
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] read book
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [D][ ] return book (by: Dec 01 2019)
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Nice! I've marked this task as done:
   [T][X] read book
____________________________________________________________
____________________________________________________________
 Noted. I've removed this task:
   [D][ ] return book (by: Dec 01 2019)
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

Expected contents of `data/duke.txt` after the program exits:

```text
T | 1 | read book
```

## UI-005: Load saved tasks when Kibo starts

Aim: Verify that Kibo reconstructs all task types and their done statuses from `data/duke.txt`
before it accepts commands.

Setup fixture for `data/duke.txt`:

```text
T | 1 | read book
D | 0 | return book | 2019-12-01
E | 0 | project meeting | Mon 2pm | 4pm
```

Input, in order:

```text
list
bye
```

Expected program output:

```text
 _  __ _ _           
| |/ /(_) |__   ___  
| ' / | | '_ \ / _ \
| . \ | | |_) | (_) |
|_|\_\|_|_.__/ \___/
Hello! I'm Kibo. I am AI.
What can I do for you?
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][X] read book
 2.[D][ ] return book (by: Dec 01 2019)
 3.[E][ ] project meeting (from: Mon 2pm to: 4pm)
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

## UI-006: Reject empty, extra, and unsafe command input

Aim: Verify that empty input, extra arguments to `list` and `bye`, and text that would make the
save file ambiguous are rejected without changing the task list.

Input, in order:

```text

list extra
bye later
todo read | book
list
bye
```

Expected program output:

```text
 _  __ _ _           
| |/ /(_) |__   ___  
| ' / | | '_ \ / _ \
| . \ | | |_) | (_) |
|_|\_\|_|_.__/ \___/
Hello! I'm Kibo. I am AI.
What can I do for you?
____________________________________________________________
____________________________________________________________
 Please enter a command.
 Available commands: todo, deadline, event, list, mark, unmark, delete, bye
____________________________________________________________
____________________________________________________________
 This command does not take any additional text.
 Usage: list
____________________________________________________________
____________________________________________________________
 This command does not take any additional text.
 Usage: bye
____________________________________________________________
____________________________________________________________
 Task text cannot contain " | " because it is used to save tasks.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

## UI-007: Report malformed saved task data

Aim: Verify that Kibo exits safely with a clear message when its save file has an invalid line.

Setup fixture for `data/duke.txt`:

```text
T | 2 | read book
```

Input: no input; the program exits after reporting the startup error.

Expected program output:

```text
 _  __ _ _           
| |/ /(_) |__   ___  
| ' / | | '_ \ / _ \
| . \ | | |_) | (_) |
|_|\_\|_|_.__/ \___/
Hello! I'm Kibo. I am AI.
What can I do for you?
____________________________________________________________
 The saved task on line 1 has an invalid format.
____________________________________________________________
```

## UI-008: Start and save without a data folder

Aim: Verify that a first-time user can start Kibo without `data/duke.txt` or its parent
`data/` folder, then create and save a task successfully.

Setup: use a fresh empty temporary working directory with no `data/` folder. Launch Kibo with
the compiled classes from the repository, for example:

```text
java -cp /absolute/path/to/ip/out kibo.Kibo
```

Input, in order:

```text
todo first task
bye
```

Expected program output:

```text
 _  __ _ _           
| |/ /(_) |__   ___  
| ' / | | '_ \ / _ \
| . \ | | |_) | (_) |
|_|\_\|_|_.__/ \___/
Hello! I'm Kibo. I am AI.
What can I do for you?
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] first task
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

Expected file created after the program exits:

```text
data/duke.txt
```

Expected contents of that file:

```text
T | 0 | first task
```

## UI-009: Launch the packaged fat JAR

Aim: Verify that the Shadow plugin produces an executable fat JAR with Kibo as its main class.

Setup: run `./gradlew shadowJar` from the repository root. Then use a fresh empty temporary
working directory with no `data/` folder and launch the JAR using its absolute path, for example:

```text
java -jar /absolute/path/to/ip/build/libs/kibo.jar
```

Input, in order:

```text
bye
```

Expected program output:

```text
 _  __ _ _           
| |/ /(_) |__   ___  
| ' / | | '_ \ / _ \
| . \ | | |_) | (_) |
|_|\_\|_|_.__/ \___/
Hello! I'm Kibo. I am AI.
What can I do for you?
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```
