package parser;

import java.util.ArrayList;

import commands.ByeCommand;
import commands.Command;
import commands.DeadlineCommand;
import commands.DeleteCommand;
import commands.EventCommand;
import commands.FindCommand;
import commands.ListCommand;
import commands.MarkCommand;
import commands.TodoCommand;
import commands.UnmarkComment;
import duke.DukeException;


/**
 * This class handles all string manipulation needed
 * to read user inputs.
 */
public class Parser {

    private String type = "";

    private enum Commands {
        bye("bye"),
        list("list"),
        mark("mark"),
        unmark("unmark"),
        deadline("deadline"),
        event("event"),
        todo("todo"),
        delete("delete"),
        find("find");

        private final String command;

        Commands(String command) {
            this.command = command;
        }

        @Override
        public String toString() {
            return command;
        }
    }

    /**
     * Parser empty constructor.
     */
    public Parser() {
    }

    /**
     * Parser constructor that takes in a String.
     * @param type The type of task.
     */
    public Parser(String type) {
        this.type = type;
    }

    /**
     * Returns the intended command after reading the user input.
     * @param input Input from the user.
     * @return The intended command.
     * @throws DukeException If user typed invalid commands.
     */
    public Command getCommand(String input) throws DukeException {
        if (input.equals(Commands.bye.toString())) {
            return new ByeCommand();

        } else if (input.equals(Commands.list.toString())) {
            return new ListCommand();

        } else if (input.contains(Commands.unmark.toString())) {
            int selected = Integer.parseInt(input.substring(7)) - 1;
            return new UnmarkComment(selected);

        } else if (input.contains(Commands.mark.toString())) {
            int selected = Integer.parseInt(input.substring(5)) - 1;
            return new MarkCommand(selected);

        } else if (input.contains(Commands.deadline.toString())) {
            if (input.equals(Commands.deadline.toString())) {
                throw new DukeException("Empty deadline");
            }
            if (input.charAt(8) != ' ') {
                throw new DukeException("Invalid command");
            }
            if (!input.contains("/by")) {
                throw new DukeException("Deadline does not contain /by");
            }
            Parser parser = new Parser("D");
            ArrayList<String> texts = parser.convertTaskInput(input);
            return new DeadlineCommand(texts);

        } else if (input.contains(Commands.event.toString())) {
            if (input.equals(Commands.event.toString())) {
                throw new DukeException("Empty event");
            }
            if (input.charAt(5) != ' ') {
                throw new DukeException("Invalid command");
            }
            if (!input.contains("to") || !input.contains("from")) {
                throw new DukeException("Event does not contain to or from");
            }
            Parser parser = new Parser("E");
            ArrayList<String> texts = parser.convertTaskInput(input);
            return new EventCommand(texts);

        } else if (input.contains(Commands.todo.toString())) {
            if (input.equals(Commands.todo.toString())) {
                throw new DukeException("Empty todo");
            }
            if (input.charAt(4) != ' ') {
                throw new DukeException("Invalid command");
            }
            Parser parser = new Parser("T");
            ArrayList<String> texts = parser.convertTaskInput(input);
            return new TodoCommand(texts);

        } else if (input.contains(Commands.delete.toString())) {
            int selected = Integer.parseInt(input.substring(input.indexOf("delete") + 7)) - 1;
            return new DeleteCommand(selected);

        } else if (input.contains(Commands.find.toString())) {
            if (input.equals(Commands.find.toString())) {
                throw new DukeException("find cannot be empty");
            }
            if (input.charAt(4) != ' ') {
                throw new DukeException("Invalid command");
            }
            Parser parser = new Parser("P");
            ArrayList<String> texts = parser.convertTaskInput(input);
            return new FindCommand(texts.get(0));

        } else {
            throw new DukeException("Invalid command");
        }
    }

    /**
     * Converts the user input of different type of tasks into ArrayList.
     * @param input User input of the tasks.
     * @return Required data of task from the user input in ArrayList.
     * @throws DukeException If user typed invalid tasks commands.
     */
    public ArrayList<String> convertTaskInput(String input) throws DukeException {
        ArrayList<String> texts = new ArrayList<>();
        switch (type) {
        case "E": {
            if (input.indexOf("/from") - 1 < 6) {
                throw new DukeException("Your Event name is blank");
            }
            String desc = input.substring(6, input.indexOf("/from") - 1);
            if (desc.isBlank() || desc.isEmpty()) {
                throw new DukeException("Your Event name is blank");
            }
            texts.add(desc);
            String from = input.substring(input.indexOf("/from") + 6, input.indexOf("/to") - 1);
            texts.add((from));
            String to = input.substring(input.indexOf("/to") + 4);
            texts.add(to);
            break;
        }
        case "D": {
            if (input.indexOf("/by") - 1 < 9) {
                throw new DukeException("Your Deadline name is blank");
            }
            String desc = input.substring(9, input.indexOf("/by") - 1);
            if (desc.isBlank() || desc.isEmpty()) {
                throw new DukeException("Your Deadline name is blank");
            }
            texts.add(desc);
            String by = input.substring(input.indexOf("/by") + 4);
            texts.add(by);
            break;
        }
        case "T": {
            String desc = input.substring(input.indexOf("todo") + 5);
            if (desc.isBlank() || desc.isEmpty()) {
                throw new DukeException("Your Todo name is blank");
            }
            texts.add(desc);
            break;
        }
        case "P": {
            String keyWord = splitter(input, " ").get(1);
            texts.add(keyWord);
            break;
        }
        default: {
            assert false : type;
        }
        }
        assert 0 != texts.size();
        return texts;
    }

    /**
     * Separating words from a string that has a divider between it and store the words in
     * ArrayList.
     * @param text The text to split.
     * @param cutOff The divider to cut the string.
     * @return Separated words stored in ArrayList from a string that was merged with a divider.
     */
    public ArrayList<String> splitter(String text, String cutOff) {
        ArrayList<String> res = new ArrayList<>();
        String resStrAftCutting = text;
        while (resStrAftCutting.contains(cutOff)) {
            String sub = resStrAftCutting.substring(0, resStrAftCutting.indexOf(cutOff));
            res.add(sub);
            resStrAftCutting = resStrAftCutting.substring(resStrAftCutting.indexOf(cutOff) + 1);
        }
        res.add(resStrAftCutting);
        return res;
    }

}
